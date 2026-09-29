import { HttpsError, onCall } from 'firebase-functions/v2/https';
import { GAME_MAX_POINTS, GAME_REWARDS_PER_DAY, REGION } from './config';
import { dayKey } from './dates';
import { db, paths } from './db';
import { optionalNumber, requireString, requireUser } from './guards';
import { award, readDailyCount, RewardResult, writeDailyCount } from './rewards';

const GAME_REWARDS_COUNTER = 'gameRewards';
const KNOWN_GAMES = new Set(['eco_puzzle', 'waste_sorting', 'eco_maze', 'eco_quiz']);

export const recordTipRead = onCall({ region: REGION }, async (request): Promise<RewardResult> => {
  const uid = requireUser(request);
  const tipId = requireString(request.data, 'tipId');

  return db.runTransaction(async (tx) => {
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
});

export const recordGameResult = onCall({ region: REGION }, async (request): Promise<RewardResult> => {
  const uid = requireUser(request);
  const gameId = requireString(request.data, 'gameId');
  const score = Math.max(0, Math.floor(optionalNumber(request.data, 'score') ?? 0));
  if (!KNOWN_GAMES.has(gameId)) throw new HttpsError('invalid-argument', 'Unknown game');
  const day = dayKey();

  return db.runTransaction(async (tx) => {
    const rewardedToday = await readDailyCount(tx, uid, day, GAME_REWARDS_COUNTER);
    const points = Math.min(score, GAME_MAX_POINTS);
    if (rewardedToday >= GAME_REWARDS_PER_DAY || points === 0) return { points: 0, xp: 0 };

    writeDailyCount(tx, uid, day, GAME_REWARDS_COUNTER, rewardedToday + 1);
    return award(tx, uid, { points, xp: points }, 'GAME_PLAYED', gameId);
  });
});
