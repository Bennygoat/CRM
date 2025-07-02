import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "../api/axiosFrontend";
import ProductCart from "../components/Product/ProductCart";

function Product() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchProductDetail = async () => {
      try {
        const response = await axios.get(`/products/details/${id}`);

        const rawData = response.data;
        const formattedProduct = {
          id: rawData.productId,
          name: rawData.name,
          price: rawData.price,
          image: rawData.imageUrls?.[0] || "",
          descriptionList: rawData.description
            ? rawData.description.split("\n") // ← 根據你後端格式改成你需要的分割方式
            : [],
        };

        setProduct(formattedProduct);
        console.log("取得商品資料成功", formattedProduct);
      } catch (err) {
        console.error("取得商品失敗", err);
        setError("無法載入商品資料");
      } finally {
        setLoading(false);
      }
    };

    fetchProductDetail();
  }, [id]);


  if (loading) return <p className="p-10">載入中...</p>;
  if (error) return <p className="p-10 text-red-500">{error}</p>;

  return (
    <ProductCart product={product} />
  );
}

export default Product;
