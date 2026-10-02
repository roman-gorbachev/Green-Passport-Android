export const REGION = 'europe-central2';
export const FIRESTORE_TRIGGER_REGION = 'us-central1';
export const APP_ROOT = 'apps/greenpassport';
export const TIME_ZONE = 'Europe/Minsk';

export const SELF_TASKS_PER_DAY = 3;
export const GAME_REWARDS_PER_DAY = 5;
export const GAME_MAX_POINTS = 30;
export const REPORTS_TO_HIDE = 3;
export const MAX_REJECTION_REASON_LENGTH = 200;
export const DAY_MILLIS = 24 * 60 * 60 * 1000;
export const DEFAULT_COUPON_VALIDITY_DAYS = 30;

export const STREAK_DAILY_BONUS = 5;
export const STREAK_WEEK_BONUS = 35;
export const STREAK_WEEK_LENGTH = 7;
export const PROFILE_BONUS_POINTS = 50;
export const FEEDBACK_POINTS = 10;
export const FEEDBACK_REWARD_COOLDOWN_DAYS = 7;
export const SURVEY_POINTS = 10;
export const FEEDBACK_MAX_LENGTH = 2000;
export const EVENT_CODE_PREFIX = 'greenpassport:event:';
export const EVENT_CHECK_IN_OPENS_BEFORE_MILLIS = 2 * 60 * 60 * 1000;
export const EVENT_CHECK_IN_CLOSES_AFTER_MILLIS = 6 * 60 * 60 * 1000;

export const TASK_CODE_PREFIX = 'greenpassport:task:';
export const SUBMISSIONS_STORAGE_PREFIX = 'greenpassport/submissions/';

export const ADMIN_REDEEM_URL = 'https://greenpassport-admin.web.app/redeem';
export const QR_SECRET_BYTES = 16;
export const DYNAMIC_KEY_BYTES = 32;
export const DYNAMIC_WINDOW_MILLIS = 30_000;
export const DYNAMIC_ACCEPTED_PAST_WINDOWS = 1;
export const DYNAMIC_SIGNATURE_LENGTH = 12;
export const MAX_QR_PAYLOADS_PER_CALL = 200;
export const MAX_IMPORTED_CODES_PER_CALL = 5000;
export const MAX_PARTNER_CODE_LENGTH = 64;
export const MAX_BATCH_WRITES = 450;
export const PARTNER_HISTORY_LIMIT = 200;
export const COUPON_LOOKUP_LIMIT = 20;
