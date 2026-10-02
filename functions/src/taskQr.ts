export type TaskQrProblem = 'notActive' | 'limitReached' | null;

export interface TaskQrSettings {
  qrActiveFromEpochMillis?: number;
  qrActiveUntilEpochMillis?: number;
  qrScanLimit?: number;
}

export function taskQrProblem(task: TaskQrSettings, scanCount: number, now: number): TaskQrProblem {
  const from = task.qrActiveFromEpochMillis;
  const until = task.qrActiveUntilEpochMillis;
  if ((from !== undefined && now < from) || (until !== undefined && now > until)) return 'notActive';
  const limit = task.qrScanLimit;
  if (limit !== undefined && limit > 0 && scanCount >= limit) return 'limitReached';
  return null;
}
