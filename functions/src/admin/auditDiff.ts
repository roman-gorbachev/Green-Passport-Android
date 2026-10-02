export type AuditAction = 'create' | 'update' | 'delete';

export const AUDIT_SYSTEM_AUTHOR = 'system';
export const AUDIT_UNKNOWN_AUTHOR = 'unknown';

const UNTRACKED_FIELDS = new Set([
  'updatedBy',
  'updatedAtEpochMillis',
  'issuedCount',
  'usedCount',
  'poolAvailableCount',
  'selfMarkedCount',
]);

type Data = Record<string, unknown>;

export interface AuditRecord {
  action: AuditAction;
  by: string;
  changedFields: string[];
}

function isEqual(left: unknown, right: unknown): boolean {
  if (left === right) return true;
  if (typeof left !== 'object' || typeof right !== 'object' || left === null || right === null) return false;
  if (Array.isArray(left) !== Array.isArray(right)) return false;
  const leftKeys = Object.keys(left);
  const rightKeys = Object.keys(right);
  return leftKeys.length === rightKeys.length &&
    leftKeys.every((key) => isEqual((left as Data)[key], (right as Data)[key]));
}

export function changedFields(before: Data | undefined, after: Data | undefined): string[] {
  const keys = new Set([...Object.keys(before ?? {}), ...Object.keys(after ?? {})]);
  return [...keys]
    .filter((key) => !UNTRACKED_FIELDS.has(key) && !isEqual(before?.[key], after?.[key]))
    .sort();
}

export function auditRecord(before: Data | undefined, after: Data | undefined): AuditRecord | null {
  if (!before && !after) return null;
  const fields = changedFields(before, after);
  if (!after) return { action: 'delete', by: AUDIT_UNKNOWN_AUTHOR, changedFields: fields };
  const author = typeof after.updatedBy === 'string' && after.updatedBy ? after.updatedBy : AUDIT_SYSTEM_AUTHOR;
  if (!before) return { action: 'create', by: author, changedFields: fields };
  if (fields.length === 0) return null;
  const isStamped = after.updatedAtEpochMillis !== before.updatedAtEpochMillis;
  return { action: 'update', by: isStamped ? author : AUDIT_SYSTEM_AUTHOR, changedFields: fields };
}
