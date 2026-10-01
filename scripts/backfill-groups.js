const { initializeApp, cert } = require('firebase-admin/app');
const { getFirestore } = require('firebase-admin/firestore');
const { randomInt } = require('crypto');
const path = require('path');

const INVITE_CODE_ALPHABET = 'ABCDEFGHJKMNPQRSTUVWXYZ23456789';
const INVITE_CODE_LENGTH = 6;

const args = process.argv.slice(2);
const serviceAccountPath = args.find((arg) => !arg.startsWith('--')) || path.join(__dirname, 'service-account.json');
const dryRun = args.includes('--dry-run');

initializeApp({
  credential: cert(require(serviceAccountPath)),
});

const db = getFirestore();
const groups = db.collection('apps').doc('greenpassport').collection('groups');

function generateInviteCode() {
  let code = '';
  for (let index = 0; index < INVITE_CODE_LENGTH; index += 1) {
    code += INVITE_CODE_ALPHABET[randomInt(INVITE_CODE_ALPHABET.length)];
  }
  return code;
}

async function main() {
  const snapshot = await groups.get();
  const usedCodes = new Set(snapshot.docs.map((group) => group.get('inviteCode')).filter(Boolean));
  const batch = db.batch();
  let updated = 0;
  for (const group of snapshot.docs) {
    const update = {};
    if (!group.get('inviteCode')) {
      let code = generateInviteCode();
      while (usedCodes.has(code)) code = generateInviteCode();
      usedCodes.add(code);
      update.inviteCode = code;
    }
    if (!group.get('ownerId')) {
      update.ownerId = (group.get('memberIds') || [])[0] || null;
    }
    if (!group.get('createdAtEpochMillis')) {
      update.createdAtEpochMillis = group.createTime.toMillis();
    }
    if (Object.keys(update).length > 0) {
      console.log(`${group.id}: ${JSON.stringify(update)}`);
      batch.update(group.ref, update);
      updated += 1;
    }
  }
  if (updated === 0) {
    console.log('All groups are already up to date.');
    return;
  }
  if (dryRun) {
    console.log(`Dry run: ${updated} group(s) would be updated.`);
    return;
  }
  await batch.commit();
  console.log(`Updated ${updated} group(s).`);
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
