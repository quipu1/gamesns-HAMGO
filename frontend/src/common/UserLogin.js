import http from '@/util/http-common';

async function login(uid) {
  let res = await http.post('/auth/login', uid);

  return res.status;
}

export { login };
