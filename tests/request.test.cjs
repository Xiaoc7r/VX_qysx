const { test } = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');

function fixture(initial = {}) {
  const storage = new Map(Object.entries(initial));
  const requests = [], redirects = [];
  const context = { module: { exports: {} }, wx: {
    getStorageSync: key => storage.get(key),
    removeStorageSync: key => storage.delete(key),
    clearStorageSync: () => storage.clear(),
    request: input => requests.push(input),
    reLaunch: input => redirects.push(input),
    showToast() {},
  } };
  vm.runInNewContext(fs.readFileSync(path.join(__dirname, '../utils/request.js'), 'utf8'), context);
  return { request: context.module.exports, storage, requests, redirects };
}

test('expired login removes only authentication data', async () => {
  const app = fixture({ token: 'old', userId: '1', userType: 'student', draft: 'keep me' });
  const pending = app.request({ url: '/api/test' });
  app.requests[0].success({ statusCode: 401 });
  await assert.rejects(pending);
  assert.equal(app.storage.get('draft'), 'keep me');
  assert.equal(app.storage.has('token'), false);
  assert.equal(app.storage.has('userId'), false);
  assert.equal(app.storage.has('userType'), false);
  assert.equal(app.redirects.length, 1);
});

test('late 401 from an old request cannot erase a fresh login', async () => {
  const app = fixture({ token: 'old' });
  const pending = app.request({ url: '/api/test' });
  app.storage.set('token', 'new');
  app.requests[0].success({ statusCode: 401 });
  await assert.rejects(pending);
  assert.equal(app.storage.get('token'), 'new');
  assert.equal(app.redirects.length, 0);
});

test('concurrent unauthorized responses redirect only once', async () => {
  const app = fixture({ token: 'old' });
  const pending = [app.request({ url: '/a' }), app.request({ url: '/b' })];
  app.requests.forEach(request => request.success({ statusCode: 401 }));
  await Promise.all(pending.map(value => assert.rejects(value)));
  assert.equal(app.redirects.length, 1);
});

test('successful requests still resolve with the original response', async () => {
  const app = fixture({ token: 'valid' });
  const pending = app.request({ url: '/a' });
  const response = { statusCode: 200, data: { code: 200, data: 'ok' } };
  app.requests[0].success(response);
  assert.equal(await pending, response);
  assert.equal(app.requests[0].header.Authorization, 'Bearer valid');
});
