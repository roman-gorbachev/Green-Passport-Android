import assert from 'node:assert/strict';
import { test } from 'node:test';
import { auditRecord, changedFields } from './auditDiff';

test('nested maps are compared by value', () => {
  assert.deepEqual(changedFields({ titles: { ru: 'А', en: 'A' } }, { titles: { en: 'A', ru: 'А' } }), []);
  assert.deepEqual(changedFields({ titles: { ru: 'А' } }, { titles: { ru: 'Б' } }), ['titles']);
});

test('an editor save is attributed to its author', () => {
  assert.deepEqual(
    auditRecord(
      { title: 'А', updatedBy: 'old', updatedAtEpochMillis: 1 },
      { title: 'Б', isActive: false, updatedBy: 'editor', updatedAtEpochMillis: 2 },
    ),
    { action: 'update', by: 'editor', changedFields: ['isActive', 'title'] },
  );
});

test('server counters and unchanged saves are not logged', () => {
  assert.equal(auditRecord({ issuedCount: 1, updatedBy: 'e', updatedAtEpochMillis: 1 }, { issuedCount: 2, updatedBy: 'e', updatedAtEpochMillis: 1 }), null);
  assert.equal(auditRecord({ title: 'А', updatedAtEpochMillis: 1 }, { title: 'А', updatedAtEpochMillis: 2 }), null);
});

test('unstamped writes come from the system and deletes have no known author', () => {
  assert.deepEqual(auditRecord(undefined, { title: 'А' }), { action: 'create', by: 'system', changedFields: ['title'] });
  assert.deepEqual(
    auditRecord({ title: 'А', updatedBy: 'e', updatedAtEpochMillis: 1 }, { title: 'Б', updatedBy: 'e', updatedAtEpochMillis: 1 }),
    { action: 'update', by: 'system', changedFields: ['title'] },
  );
  assert.deepEqual(auditRecord({ title: 'А' }, undefined), { action: 'delete', by: 'unknown', changedFields: ['title'] });
});
