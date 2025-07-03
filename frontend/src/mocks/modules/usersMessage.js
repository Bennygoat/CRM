// src/mocks/handlers.js
import { rest } from 'msw';
import dayjs from 'dayjs';

export const usersMessagehandlers = [
  rest.get('/api/customer/message/list', (req, res, ctx) => {
    const now = dayjs();
    const mockMessages = [
      {
        messageId: 1,
        questionTitle: '忘記密碼該怎麼辦？',
        isResolved: true,
        createdAt: now.subtract(10, 'day').toISOString(),
        lastReplyTime: now.subtract(9, 'day').toISOString(),
        lastReplyContent: '請點選登入頁面的「忘記密碼」連結重設密碼。',
        customerId: 101,
      },
      {
        messageId: 2,
        questionTitle: '無法收到驗證信',
        isResolved: false,
        createdAt: now.subtract(5, 'day').toISOString(),
        lastReplyTime: null,
        lastReplyContent: null,
        customerId: 102,
      },
      {
        messageId: 3,
        questionTitle: '怎麼更改訂單地址？',
        isResolved: true,
        createdAt: now.subtract(7, 'day').toISOString(),
        lastReplyTime: now.subtract(6, 'day').toISOString(),
        lastReplyContent: '您好，已協助您更改地址，請至會員中心查看。',
        customerId: 103,
      },
      {
        messageId: 4,
        questionTitle: '付款完成但訂單狀態未更新',
        isResolved: false,
        createdAt: now.subtract(3, 'day').toISOString(),
        lastReplyTime: now.subtract(1, 'day').toISOString(),
        lastReplyContent: '我們已收到款項，訂單狀態稍後會更新，請稍等。',
        customerId: 104,
      },
      {
        messageId: 5,
        questionTitle: '如何申請退貨？',
        isResolved: true,
        createdAt: now.subtract(12, 'day').toISOString(),
        lastReplyTime: now.subtract(11, 'day').toISOString(),
        lastReplyContent: '請至訂單詳情頁點選「申請退貨」，並填寫原因。',
        customerId: 105,
      },
      {
        messageId: 6,
        questionTitle: '我可以更改付款方式嗎？',
        isResolved: false,
        createdAt: now.subtract(2, 'day').toISOString(),
        lastReplyTime: null,
        lastReplyContent: null,
        customerId: 106,
      },
      {
        messageId: 7,
        questionTitle: '折扣碼無法使用',
        isResolved: true,
        createdAt: now.subtract(8, 'day').toISOString(),
        lastReplyTime: now.subtract(7, 'day').toISOString(),
        lastReplyContent: '您好，該折扣碼已過期，請參考目前的優惠活動。',
        customerId: 107,
      },
      {
        messageId: 8,
        questionTitle: '商品包裝破損怎麼辦？',
        isResolved: true,
        createdAt: now.subtract(14, 'day').toISOString(),
        lastReplyTime: now.subtract(13, 'day').toISOString(),
        lastReplyContent: '抱歉造成不便，我們將補寄新商品給您。',
        customerId: 108,
      },
      {
        messageId: 9,
        questionTitle: '發票可以改寄其他地址嗎？',
        isResolved: false,
        createdAt: now.subtract(4, 'day').toISOString(),
        lastReplyTime: now.subtract(2, 'day').toISOString(),
        lastReplyContent: '您好，我們正在協助處理中，請稍候。',
        customerId: 109,
      },
      {
        messageId: 10,
        questionTitle: '為什麼無法登入會員中心？',
        isResolved: true,
        createdAt: now.subtract(6, 'day').toISOString(),
        lastReplyTime: now.subtract(5, 'day').toISOString(),
        lastReplyContent: '已重設您的帳號密碼，請使用新密碼登入。',
        customerId: 110,
      },
    ];

    return res(
      ctx.status(200),
      ctx.json({
        content: mockMessages,
        totalElements: mockMessages.length,
      })
    );
  }),
];
