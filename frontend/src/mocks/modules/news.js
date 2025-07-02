// src/mocks/modules/news.js
import { rest } from 'msw';

const mockNews = [
  {
    id: 'news1',
    imageUrl: '/images/new1.png',
    date: '2025/02/14',
    title: '一枝冰棒，一份心意｜哈根良野攜手國北教大實小...',
    content: [
      '三月底，國北教大實小與幼兒園發起公益義賣，良野捐出1,500枝冰棒...',
      '總收入捐給朗島國小購置英語教材，將希望送入偏鄉...',
      '孩子們設計標語、參與互動活動，實現自主公益...',
      '良野感謝師生們的支持，持續推動真實水果、純植製作理念。'
    ]
  },
  {
    id: 'news2',
    imageUrl: '/images/new2.jpg',
    date: '2025/07/04',
    title: '哈根良野進駐蔬食8便利店｜推廣無動物美味選擇',
    content: [
      '良野品牌正式進駐蔬食8便利商店，提供純植物基冰品與餅乾...',
      '這代表無動物產品逐漸成為生活日常選項...'
    ]
  },
  {
    id: 'news3',
    imageUrl: '/images/new3.jpg',
    date: '2025/07/15',
    title: '純植物基冰淇淋｜良野義式冰品成環保飲食新寵',
    content: [
      '良野冰淇淋採用純植物配方，口感細緻且不含乳製品...',
      '創辦人活躍參與市集活動，推廣永續飲食理念...'
    ]
  },
];

export const newsHandlers = [
  // 所有新聞列表
  rest.get('/api/news', (req, res, ctx) => {
    return res(ctx.status(200), ctx.json(mockNews));
  }),

  // 單篇新聞 by ID
  rest.get('/api/news/:id', (req, res, ctx) => {
    const { id } = req.params;
    const article = mockNews.find((item) => item.id === id);

    if (article) {
      return res(ctx.status(200), ctx.json(article));
    } else {
      return res(
        ctx.status(404),
        ctx.json({ error: `News with id "${id}" not found.` })
      );
    }
  }),
];
