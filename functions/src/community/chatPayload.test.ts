import assert from 'node:assert/strict';
import { test } from 'node:test';
import { PREVIEW_MAX_LENGTH, messageBody, revisionKind, selectRecipients } from './chatPayload';

test('the body is the sender name and the text, or the text alone', () => {
  assert.equal(messageBody({ senderName: 'Алиса', text: ' Привет ', isForwarded: false }), 'Алиса: Привет');
  assert.equal(messageBody({ senderName: null, text: 'Привет', isForwarded: false }), 'Привет');
  assert.equal(messageBody({ senderName: '  ', text: 'Привет', isForwarded: true }), '↪ Привет');
});

test('a long text is cut with an ellipsis', () => {
  const body = messageBody({ senderName: null, text: 'а'.repeat(PREVIEW_MAX_LENGTH * 2), isForwarded: false });
  assert.equal(body.length, PREVIEW_MAX_LENGTH);
  assert.ok(body.endsWith('…'));
});

test('recipients exclude the sender, muted chats and disabled notifications', () => {
  const recipients = selectRecipients({
    candidateIds: ['alice', 'bob', 'carol', 'dave', 'bob'],
    senderId: 'alice',
    disabledUserIds: new Set(['carol']),
    mutedUserIds: new Set(['dave']),
  });
  assert.deepEqual(recipients, ['bob']);
});

test('an edit or a deletion of a post is a revision, other updates are not', () => {
  const original = { text: 'Привет', deleted: false };
  assert.equal(revisionKind(original, { text: 'Привет всем', deleted: false }), 'EDIT');
  assert.equal(revisionKind(original, { text: '', deleted: true }), 'DELETE');
  assert.equal(revisionKind(original, { text: 'Привет', deleted: false }), null);
  assert.equal(revisionKind({ text: '', deleted: true }, { text: '', deleted: true }), null);
});
