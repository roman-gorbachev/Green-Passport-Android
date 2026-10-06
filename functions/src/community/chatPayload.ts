export const FORUM_CHAT_ID = 'forum';
export const PREVIEW_MAX_LENGTH = 100;
const FORWARD_MARK = '↪ ';
const ELLIPSIS = '…';

export interface ChatMessageData {
  senderName: string | null;
  text: string;
  isForwarded: boolean;
}

export function messageBody(message: ChatMessageData): string {
  const text = `${message.isForwarded ? FORWARD_MARK : ''}${message.text.trim()}`;
  const preview = text.length > PREVIEW_MAX_LENGTH ? `${text.slice(0, PREVIEW_MAX_LENGTH - ELLIPSIS.length)}${ELLIPSIS}` : text;
  const name = message.senderName?.trim();
  return name ? `${name}: ${preview}` : preview;
}

export interface RecipientFilter {
  candidateIds: readonly string[];
  senderId: string;
  disabledUserIds: ReadonlySet<string>;
  mutedUserIds: ReadonlySet<string>;
}

export function selectRecipients(filter: RecipientFilter): string[] {
  return [...new Set(filter.candidateIds)].filter((id) =>
    id !== filter.senderId && !filter.disabledUserIds.has(id) && !filter.mutedUserIds.has(id),
  );
}
