import { HttpsError, onCall } from 'firebase-functions/v2/https';
import { GAME_MAX_POINTS, GAME_REWARDS_PER_DAY, REGION } from './config';
import { dayKey } from './dates';
import { db, paths } from './db';
import { optionalNumber, requireString, requireUser } from './guards';
import { award, readDailyCount, writeDailyCount } from './rewards';
import { RewardWithStreak, withStreak } from './streak';

const GAME_REWARDS_COUNTER = 'gameRewards';
const LEGACY_GAMES = new Set(['eco_puzzle', 'waste_sorting', 'eco_maze', 'eco_quiz']);

export const recordTipRead = onCall({ region: REGION }, async (request): Promise<RewardWithStreak> => {
  const uid = requireUser(request);
  const tipId = requireString(request.data, 'tipId');

  const reward = await db.runTransaction(async (tx) => {
    const tip = await tx.get(db.doc(paths.ecoTip(tipId)));
    const read = await tx.get(db.doc(paths.ecoTipRead(uid, tipId)));
    if (!tip.exists) throw new HttpsError('not-found', 'Tip not found');
    if (read.exists) return { points: 0, xp: 0 };

    tx.create(db.doc(paths.ecoTipRead(uid, tipId)), {
      userId: uid,
      tipId,
      readAtEpochMillis: Date.now(),
    });
    return award(
      tx,
      uid,
      { points: Number(tip.get('rewardPoints') ?? 0), xp: Number(tip.get('rewardXp') ?? 0) },
      'ARTICLE_READ',
      tipId,
    );
  });
  return withStreak(uid, reward);
});

export const recordGameResult = onCall({ region: REGION }, async (request): Promise<RewardWithStreak> => {
  const uid = requireUser(request);
  const gameId = requireString(request.data, 'gameId');
  const score = Math.max(0, Math.floor(optionalNumber(request.data, 'score') ?? 0));
  const day = dayKey();

  const reward = await db.runTransaction(async (tx) => {
    const game = await tx.get(db.doc(paths.game(gameId)));
    const isKnown = game.exists ? game.get('isActive') !== false : LEGACY_GAMES.has(gameId);
    if (!isKnown) throw new HttpsError('invalid-argument', 'Unknown game');
    const rewardedToday = await readDailyCount(tx, uid, day, GAME_REWARDS_COUNTER);
    const maxPoints = Number(game.get('maxPoints') ?? GAME_MAX_POINTS);
    const points = Math.min(score, maxPoints);
    if (rewardedToday >= GAME_REWARDS_PER_DAY || points === 0) return { points: 0, xp: 0 };

    writeDailyCount(tx, uid, day, GAME_REWARDS_COUNTER, rewardedToday + 1);
    return award(tx, uid, { points, xp: points }, 'GAME_PLAYED', gameId);
  });
  return withStreak(uid, reward);
});
