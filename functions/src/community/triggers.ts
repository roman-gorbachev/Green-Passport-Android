import type { CollectionReference, DocumentSnapshot } from 'firebase-admin/firestore';
import { onDocumentCreated, onDocumentUpdated } from 'firebase-functions/v2/firestore';
import { APP_ROOT, FIRESTORE_TRIGGER_REGION } from '../config';
import { db, paths } from '../db';
import { isTextAllowed } from '../moderation/wordFilter';
import { FORUM_CHAT_ID, revisionKind, type ChatMessageData, type PostVersion } from './chatPayload';
import { forumRecipients, groupRecipients, sendChatPush } from './chatPush';

const POST_REVISIONS_COLLECTION = 'revisions';

function messageData(snapshot: DocumentSnapshot, nameField: string): ChatMessageData {
  const name = snapshot.get(nameField);
  return {
    senderName: typeof name === 'string' ? name : null,
    text: String(snapshot.get('text') ?? ''),
    isForwarded: snapshot.get('forwardedFrom') != null,
  };
}

function becameDeleted(before: DocumentSnapshot, after: DocumentSnapshot): boolean {
  return before.get('deleted') !== true && after.get('deleted') === true;
}

async function scrubReplies(collection: CollectionReference, messageId: string): Promise<void> {
  const replies = await collection.where('replyTo.messageId', '==', messageId).get();
  if (replies.empty) return;
  const batch = db.batch();
  replies.docs.forEach((reply) => batch.update(reply.ref, { 'replyTo.text': '' }));
  await batch.commit();
}

function postVersion(snapshot: DocumentSnapshot): PostVersion {
  return { text: String(snapshot.get('text') ?? ''), deleted: snapshot.get('deleted') === true };
}

async function saveRevision(before: DocumentSnapshot, after: DocumentSnapshot): Promise<void> {
  const kind = revisionKind(postVersion(before), postVersion(after));
  if (!kind) return;
  await before.ref.collection(POST_REVISIONS_COLLECTION).add({
    text: String(before.get('text') ?? ''),
    kind,
    changedAtEpochMillis: kind === 'EDIT' ? Number(after.get('editedAtEpochMillis') ?? Date.now()) : Date.now(),
    authorId: String(before.get('authorId') ?? ''),
  });
}

export const onForumPostUpdated = onDocumentUpdated(
  { region: FIRESTORE_TRIGGER_REGION, document: `${APP_ROOT}/posts/{postId}` },
  async (event) => {
    const before = event.data?.before;
    const after = event.data?.after;
    if (!before || !after) return;
    await saveRevision(before, after);
    if (becameDeleted(before, after)) {
      await scrubReplies(db.collection(paths.posts()), event.params.postId);
      return;
    }
    const text = String(after.get('text') ?? '');
    if (after.get('deleted') !== true && text !== before.get('text') && !isTextAllowed(text)) {
      await after.ref.update({ hidden: true, hiddenReason: 'banned_words' });
    }
  },
);

export const onChatMessageUpdated = onDocumentUpdated(
  { region: FIRESTORE_TRIGGER_REGION, document: `${APP_ROOT}/chats/{groupId}/messages/{messageId}` },
  async (event) => {
    const before = event.data?.before;
    const after = event.data?.after;
    if (!before || !after || !becameDeleted(before, after)) return;
    await scrubReplies(db.collection(paths.chatMessages(event.params.groupId)), event.params.messageId);
  },
);

export const notifyForumPost = onDocumentCreated(
  { region: FIRESTORE_TRIGGER_REGION, document: `${APP_ROOT}/posts/{postId}` },
  async (event) => {
    const post = event.data;
    if (!post) return;
    const message = messageData(post, 'authorName');
    if (!isTextAllowed(message.text) || (message.senderName && !isTextAllowed(message.senderName))) return;
    const senderId = String(post.get('authorId') ?? '');
    const recipients = await forumRecipients(senderId);
    await sendChatPush(recipients, { chatId: FORUM_CHAT_ID, chatTitle: null, senderId, message });
  },
);

export const notifyGroupMessage = onDocumentCreated(
  { region: FIRESTORE_TRIGGER_REGION, document: `${APP_ROOT}/chats/{groupId}/messages/{messageId}` },
  async (event) => {
    const snapshot = event.data;
    if (!snapshot) return;
    const groupId = event.params.groupId;
    const group = await db.doc(paths.group(groupId)).get();
    if (!group.exists) return;
    await group.ref.update({ lastMessageAtEpochMillis: Number(snapshot.get('createdAtEpochMillis') ?? Date.now()) });
    const message = messageData(snapshot, 'senderName');
    if (!isTextAllowed(message.text)) return;
    const senderId = String(snapshot.get('senderId') ?? '');
    const memberIds = (group.get('memberIds') as string[] | undefined) ?? [];
    const recipients = await groupRecipients(memberIds, groupId, senderId);
    await sendChatPush(recipients, { chatId: groupId, chatTitle: String(group.get('name') ?? ''), senderId, message });
  },
);
