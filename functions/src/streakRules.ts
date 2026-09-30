import { STREAK_DAILY_BONUS, STREAK_WEEK_BONUS, STREAK_WEEK_LENGTH } from './config';

export interface Streak {
  count: number;
  lastDay: string;
}

export interface StreakUpdate {
  streak: Streak;
  bonus: number;
}

export function nextStreak(previous: Streak | undefined, today: string, yesterday: string): StreakUpdate | null {
  if (previous?.lastDay === today) {
    return null;
  }
  const count = previous?.lastDay === yesterday ? previous.count + 1 : 1;
  const bonus = count % STREAK_WEEK_LENGTH === 0 ? STREAK_WEEK_BONUS : STREAK_DAILY_BONUS;
  return { streak: { count, lastDay: today }, bonus };
}
