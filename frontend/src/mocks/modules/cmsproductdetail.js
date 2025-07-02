import productImage from "../../assets/product1.png";
import { rest } from "msw";

// 商品資料集中寫在外層，方便查找
const allProducts = [
  {
    id: 1,
    name: "2025全新口味｜檸檬煉乳冰棒6入組",
    price: 300,
    image: productImage,
    descriptionList: [
      "特選無皮油檸檬，清爽果酸香氣細緻純淨",
      "天然手作煉乳，口感濃郁",
      "全手工製作，封存真實果味",
      "完美搭配，為夏季添上一抹清涼",
      "自營工廠HACCP/ISO22000認證"
    ],
  },
  // 可擴充更多商品
  {
    id: 2,
    name: "草莓優格冰棒 6入組",
    price: 280,
    image: productImage,
    descriptionList: [
      "嚴選草莓果肉",
      "自然優格風味",
      "不添加色素香精",
    ],
  },
];

export const cmsproductdetailhandlers = [
  rest.get("/api/cmsproductdetail", (req, res, ctx) => {
    const idParam = req.url.searchParams.get("id");
    const productId = Number(idParam);

    const product = allProducts.find((item) => item.id === productId);

    if (!product) {
      return res(
        ctx.status(404),
        ctx.json({ message: "找不到該商品" })
      );
    }

    return res(
      ctx.status(200),
      ctx.json(product)
    );
  }),
];
