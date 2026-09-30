import { DAY_MILLIS, TIME_ZONE } from './config';

const dayFormatter = new Intl.DateTimeFormat('en-CA', {
  timeZone: TIME_ZONE,
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
});

export function dayKey(date: Date = new Date()): string {
  return dayFormatter.format(date);
}

export function previousDayKey(date: Date = new Date()): string {
  return dayFormatter.format(new Date(date.getTime() - DAY_MILLIS));
}
