export const COUPON_STATUS_EXPIRED = 'EXPIRED';

export type CouponScanOutcome = 'redeemed' | 'alreadyUsed' | 'expired' | 'notFound';

export interface ScannedCoupon {
  code?: string;
  status?: string;
  expiresAtEpochMillis?: number;
}

export function couponScanOutcome(coupon: ScannedCoupon | undefined, code: string, now: number): CouponScanOutcome {
  if (!coupon || !code || coupon.code !== code) return 'notFound';
  if (coupon.status === 'USED') return 'alreadyUsed';
  if (coupon.status === COUPON_STATUS_EXPIRED) return 'expired';
  if (coupon.expiresAtEpochMillis !== undefined && coupon.expiresAtEpochMillis < now) return 'expired';
  return 'redeemed';
}

const MESSAGES: Record<CouponScanOutcome, Record<'ru' | 'en', { title: string; detail: string }>> = {
  redeemed: {
    ru: { title: 'Купон погашен', detail: 'Выдайте награду покупателю.' },
    en: { title: 'Coupon redeemed', detail: 'Give the reward to the customer.' },
  },
  alreadyUsed: {
    ru: { title: 'Купон уже использован', detail: 'Повторно погасить его нельзя.' },
    en: { title: 'Coupon already used', detail: 'It cannot be redeemed twice.' },
  },
  expired: {
    ru: { title: 'Срок купона истёк', detail: 'Награду по нему выдать нельзя.' },
    en: { title: 'Coupon expired', detail: 'The reward cannot be given.' },
  },
  notFound: {
    ru: { title: 'Купон не найден', detail: 'Проверьте QR-код и попробуйте снова.' },
    en: { title: 'Coupon not found', detail: 'Check the QR code and try again.' },
  },
};

const ICONS: Record<CouponScanOutcome, string> = {
  redeemed: '✅',
  alreadyUsed: '⚠️',
  expired: '⌛',
  notFound: '❓',
};

export const STATUS_CODES: Record<CouponScanOutcome, number> = {
  redeemed: 200,
  alreadyUsed: 409,
  expired: 410,
  notFound: 404,
};

export function scanLanguage(acceptLanguage: string | undefined): 'ru' | 'en' {
  const primary = (acceptLanguage ?? '').split(',')[0].trim().toLowerCase();
  return primary.startsWith('ru') || primary.startsWith('be') ? 'ru' : 'en';
}

function escapeHtml(text: string): string {
  return text.replace(/[&<>"']/g, (character) => `&#${character.charCodeAt(0)};`);
}

export function renderScanPage(outcome: CouponScanOutcome, rewardTitle: string | undefined, language: 'ru' | 'en'): string {
  const message = MESSAGES[outcome][language];
  const reward = rewardTitle && outcome === 'redeemed' ? `<p class="reward">${escapeHtml(rewardTitle)}</p>` : '';
  return `<!doctype html>
<html lang="${language}">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Green Passport</title>
<style>
body { margin: 0; min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #f2f2f7; color: #0b0f10; font-family: -apple-system, system-ui, Roboto, sans-serif; text-align: center; }
main { padding: 24px; max-width: 360px; }
.icon { font-size: 64px; }
h1 { font-size: 24px; margin: 16px 0 8px; }
p { margin: 0; color: #6a7f75; }
.reward { margin-top: 16px; color: #1f6b47; font-weight: 600; font-size: 18px; }
@media (prefers-color-scheme: dark) { body { background: #000; color: #e4eee7; } p { color: #8aa398; } .reward { color: #2e8c5e; } }
</style>
</head>
<body>
<main>
<div class="icon">${ICONS[outcome]}</div>
<h1>${message.title}</h1>
<p>${message.detail}</p>
${reward}
</main>
</body>
</html>`;
}
