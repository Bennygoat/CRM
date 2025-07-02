import { rest } from "msw";
import product063001 from "../../assets/0630/063001.png";
import product063002 from "../../assets/0630/063002.png";
import product063003 from "../../assets/0630/063003.png";
import product063004 from "../../assets/0630/063004.png";
import product063005 from "../../assets/0630/063005.png";
import product063006 from "../../assets/0630/063006.png";
import product063007 from "../../assets/0630/063007.png";
import product063008 from "../../assets/0630/063008.png";
import product063009 from "../../assets/0630/063009.png";
import product063010 from "../../assets/0630/063010.png";

import Img1 from '../../assets/popsicle/010626.jpg';
import Img2 from '../../assets/popsicle/020626.jpg';
import Img3 from '../../assets/popsicle/030626.jpg';
import Img4 from '../../assets/popsicle/040626.jpg';
import Img5 from '../../assets/popsicle/050626.jpg';
import Img6 from '../../assets/popsicle/060626.jpg';
import Img7 from '../../assets/popsicle/070626.jpg';
import Img8 from '../../assets/popsicle/080626.jpg';
import Img9 from '../../assets/popsicle/090626.jpg';
import Img10 from '../../assets/popsicle/100626.jpg';


export const productHandlers = [
  rest.get("/api/cmsproducts", (req, res, ctx) => {
    return res(
      ctx.status(200),
      ctx.json([
        {
          category: "輕包裝豆乳雪糕",
          products: [
            {
              id: 1,
              imageSrc: Img1,
              title: "良野頂級熟韻(巧克力)豆乳雪糕",
              description: "絲滑濃郁，口感厚實，讓你一口上癮。",
              price: "NT$65",
            },
            {
              id: 2,
              imageSrc: Img2,
              title: "良野特級日式(抹茶)豆乳雪糕",
              description: "茶香清幽，清爽不甜膩，日系經典感。",
              price: "NT$65",
            },
            {
              id: 3,
              imageSrc: Img3,
              title: "良野莓好生活(草莓)豆乳雪糕",
              description: "酸甜平衡恰到好處，果香滿滿入口即化。",
              price: "NT$65",
            },
            {
              id: 4,
              imageSrc: Img4,
              title: "良野濃醇(花生)豆乳雪糕",
              description: "花生香氣自然濃醇，充滿懷舊的安心感。",
              price: "NT$60",
            },
            {
              id: 5,
              imageSrc: Img5,
              title: "良野超越經典(香草)豆乳雪糕",
              description: "經典奶香中帶豆乳清爽，簡單卻耐吃。",
              price: "NT$60",
            },
            {
              id: 6,
              imageSrc: Img6,
              title: "良野大花之吻(玫瑰)豆乳雪糕",
              description: "玫瑰香氣優雅綻放，讓人想慢慢品味。",
              price: "NT$60",
            },
            {
              id: 7,
              imageSrc: Img7,
              title: "良野法式(焦糖佐脆餅)豆乳雪糕",
              description: "焦糖與脆餅層次豐富，法式點心風格濃厚。",
              price: "NT$60",
            },
            {
              id: 8,
              imageSrc: Img8,
              title: "良野濃(黑芝麻)豆乳雪糕",
              description: "黑芝麻香濃不苦，濃中帶細膩，尾韻迷人。",
              price: "NT$60",
            },
            {
              id: 9,
              imageSrc: Img9,
              title: "良野藍色狂想曲(藍莓)豆乳雪糕",
              description: "藍莓果粒微酸微甜，清爽又帶點活潑感。",
              price: "NT$60",
            },
            {
              id: 10,
              imageSrc: Img10,
              title: "良野轉轉(OREO)豆乳雪糕",
              description: "OREO碎片脆香四溢，吃得到童年的快樂。",
              price: "NT$65",
            },
          ],
        },
        {
          category: "經典杯裝口味",
          products: [
            {
              id: 11,
              imageSrc: product063001,
              title: "超越經典香草豆乳冰淇淋",
              description: "奶香柔和、綿密順口，經典耐吃無負擔。",
              price: "NT$50",
            },
            {
              id: 12,
              imageSrc: product063002,
              title: "藍色狂想曲(藍莓)豆乳冰淇淋",
              description: "藍莓果香自然奔放，酸甜剛好不卡喉。",
              price: "NT$50",
            },
            {
              id: 13,
              imageSrc: product063003,
              title: "莓好生活豆乳冰淇淋",
              description: "微酸莓果融合豆乳清香，滑順中藏驚喜。",
              price: "NT$55",
            },
            {
              id: 14,
              imageSrc: product063004,
              title: "濃醇花生豆乳冰淇淋",
              description: "花生醇厚濃實，每口都像現炒香氣炸裂。",
              price: "NT$50",
            },
            {
              id: 15,
              imageSrc: product063005,
              title: "百香果雪酪冰淇淋",
              description: "百香果果酸清新開胃，是夏天的救星。",
              price: "NT$50",
            },
            {
              id: 16,
              imageSrc: product063006,
              title: "大花之吻(玫瑰)豆乳冰淇淋",
              description: "花香清新優雅，尾韻有種回甘的溫柔。",
              price: "NT$50",
            },
            {
              id: 17,
              imageSrc: product063007,
              title: "法式焦糖佐脆餅豆乳冰淇淋",
              description: "焦糖甜香與脆餅交織，一秒變身巴黎甜點。",
              price: "NT$50",
            },
            {
              id: 18,
              imageSrc: product063008,
              title: "特級日式抹茶豆乳冰淇淋",
              description: "茶韻內斂，尾味回甘，抹茶控首選。",
              price: "NT$55",
            },
            {
              id: 19,
              imageSrc: product063009,
              title: "頂級熟韻可可豆乳冰淇淋",
              description: "可可味醇厚大人感，尾韻低調不膩口。",
              price: "NT$55",
            },
            {
              id: 20,
              imageSrc: product063010,
              title: "轉轉OERO豆乳冰淇淋",
              description: "香濃豆乳結合巧克力餅乾，口感層次豐富。",
              price: "NT$55",
            },
          ],
        },
      ])
    );
  }),
];
