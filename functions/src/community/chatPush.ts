import { getMessaging, type Message } from 'firebase-admin/messaging';
import { logger } from 'firebase-functions/v2';
import { db, paths } from '../db';
import { FORUM_CHAT_ID, messageBody, selectRecipients, type ChatMessageData } from './chatPayload';

const IN_QUERY_LIMIT = 30;
const CHAT_PUSH_TYPE = 'chat';
const FORUM_TITLE_LOC_KEY = 'community_forum_title';
const STALE_TOKEN_ERRORS = new Set([
  'messaging/registration-token-not-registered',
  'messaging/invalid-registration-token',
]);

export interface ChatPush {
  chatId: string;
  chatTitle: string | null;
  senderId: string;
  message: ChatMessageData;
}

interface Device {
  token: string;
  platform: string;
}

function chunks<T>(items: readonly T[], size: number): T[][] {
  const result: T[][] = [];
  for (let index = 0; index < items.length; index += size) {
    result.push(items.slice(index, index + size));
  }
  return result;
}

async function disabledUserIds(userIds: readonly string[]): Promise<Set<string>> {
  if (userIds.length === 0) return new Set();
  const users = await db.getAll(...userIds.map((uid) => db.doc(paths.user(uid))));
  return new Set(users.filter((user) => user.get('messageNotificationsEnabled') === false).map((user) => user.id));
}

async function mutedUserIds(userIds: readonly string[], chatId: string): Promise<Set<string>> {
  if (userIds.length === 0) return new Set();
  const settings = await db.getAll(...userIds.map((uid) => db.doc(paths.chatSettings(uid, chatId))));
  return new Set(settings.filter((doc) => doc.get('muted') === true).map((doc) => doc.get('userId') as string));
}

export async function groupRecipients(memberIds: readonly string[], groupId: string, senderId: string): Promise<string[]> {
  const candidates = memberIds.filter((id) => id !== senderId);
  const [disabled, muted] = await Promise.all([disabledUserIds(candidates), mutedUserIds(candidates, groupId)]);
  return selectRecipients({ candidateIds: candidates, senderId, disabledUserIds: disabled, mutedUserIds: muted });
}

export async function forumRecipients(senderId: string): Promise<string[]> {
  const unmuted = await db.collection(paths.chatSettingsCollection())
    .where('chatId', '==', FORUM_CHAT_ID)
    .where('muted', '==', false)
    .get();
  const candidates = unmuted.docs.map((doc) => doc.get('userId') as string).filter((id) => id !== senderId);
  const disabled = await disabledUserIds(candidates);
  return selectRecipients({ candidateIds: candidates, senderId, disabledUserIds: disabled, mutedUserIds: new Set() });
}

async function devicesOf(userIds: readonly string[]): Promise<Device[]> {
  const snapshots = await Promise.all(chunks(userIds, IN_QUERY_LIMIT).map((ids) =>
    db.collection(paths.userDevices()).where('userId', 'in', ids).get(),
  ));
  return snapshots.flatMap((snapshot) => snapshot.docs.map((doc) => ({
    token: doc.id,
    platform: String(doc.get('platform') ?? ''),
  })));
}

function messageFor(device: Device, push: ChatPush): Message {
  const body = messageBody(push.message);
  if (device.platform === 'IOS') {
    return {
      token: device.token,
      notification: push.chatTitle ? { title: push.chatTitle, body } : { body },
      data: { type: CHAT_PUSH_TYPE, chatId: push.chatId },
      apns: {
        payload: {
          aps: {
            sound: 'default',
            threadId: push.chatId,
            ...(push.chatTitle ? {} : { alert: { titleLocKey: FORUM_TITLE_LOC_KEY, body } }),
          },
        },
      },
    };
  }
  return {
    token: device.token,
    data: {
      type: CHAT_PUSH_TYPE,
      chatId: push.chatId,
      chatTitle: push.chatTitle ?? '',
      body,
    },
    android: { priority: 'high' },
  };
}

export async function sendChatPush(recipientIds: readonly string[], push: ChatPush): Promise<void> {
  if (recipientIds.length === 0) return;
  const devices = await devicesOf(recipientIds);
  if (devices.length === 0) return;
  const response = await getMessaging().sendEach(devices.map((device) => messageFor(device, push)));
  const staleTokens = response.responses
    .map((result, index) => (result.error && STALE_TOKEN_ERRORS.has(result.error.code) ? devices[index].token : null))
    .filter((token): token is string => token !== null);
  await Promise.all(staleTokens.map((token) => db.doc(paths.userDevice(token)).delete()));
  if (response.failureCount > staleTokens.length) {
    logger.warn('Some chat pushes failed', { chatId: push.chatId, failures: response.failureCount });
  }
}
