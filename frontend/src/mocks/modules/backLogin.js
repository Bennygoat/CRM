// src/mocks/modules/backLoginHandler.js
import { rest } from 'msw';

export const backLoginHandlers = [
  // 登入
  rest.post('/api/user/auth/login', async (req, res, ctx) => {
    const { account, password } = await req.json();

    // 模擬三種角色
    let role;
    if (account === 'admin' && password === 'Admin123!@#') role = 'admin';
    else if (account === 'editor' && password === 'editor123') role = 'editor';
    else if (account === 'manager' && password === 'manager123') role = 'manager';
    else {
      return res(
        ctx.status(401),
        ctx.json({ message: '帳號或密碼錯誤' })
      );
    }

    return res(
      ctx.status(200),
      ctx.json({
        token: 'fake-back-jwt-token',
        user: { id: 100, name: '後台使用者', account, role },
      })
    );
  }),

  // 取得個人資料
  rest.get('/api/backauth/profile', (req, res, ctx) => {
    const authHeader = req.headers.get('Authorization');

    if (authHeader === 'Bearer fake-back-jwt-token') {
      return res(
        ctx.status(200),
        ctx.json({
          id: 100,
          name: '後台使用者',
          account: 'admin',
          role: 'admin',
        })
      );
    }

    return res(
      ctx.status(401),
      ctx.json({ message: '未授權' })
    );
  }),
];
