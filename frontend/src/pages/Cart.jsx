import { useEffect, useState } from "react";
import useCartStore from "../stores/cartStore";
import axios from "../api/axiosFrontend";
import { useNavigate } from "react-router-dom";

function Cart() {
  const {
    items,
    fetchCartFromServer,
    updateItemQuantityOnServer,
    removeItemFromServer,
    getTotalPrice,
  } = useCartStore();

  const navigate = useNavigate();
  const [paymentMethod, setPaymentMethod] = useState("CASH_ON_DELIVERY");
  const [city, setCity] = useState("台北市");
  const [address, setAddress] = useState("");
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState("");
  const [successMsg, setSuccessMsg] = useState("");

  useEffect(() => {
    const fetchCart = async () => {
      try {
        await fetchCartFromServer();
      } catch (err) {
        setErrorMsg("載入購物車失敗，請稍後再試");
      } finally {
        setLoading(false);
      }
    };
    fetchCart();
  }, []);

  const handleDecrease = async (item) => {
    const newQty = item.quantity - 1;
    if (newQty <= 0) return;

    try {
      await updateItemQuantityOnServer(item.cartDetailId, newQty);
      fetchCartFromServer();
    } catch {
      setErrorMsg("更新數量失敗");
    }
  };

  const handleIncrease = async (item) => {
    const newQty = item.quantity + 1;

    try {
      await updateItemQuantityOnServer(item.cartDetailId, newQty);
      fetchCartFromServer();
    } catch {
      setErrorMsg("更新數量失敗");
    }
  };

  const handleCheckout = async () => {
    if (!address) {
      setErrorMsg("請填寫送貨地址");
      return;
    }

    try {
      const res = await axios.post("/orders/create", {
        address: city + address,
        paymentMethod,
      });

      const order = res.data;
      console.log("訂單建立成功", order);

      if (paymentMethod === "ONLINE_PAYMENT") {
        try {
          const payRes = await axios.get(
            `/payments/order/${order.orderid}`
          );
          console.log("取得付款連結", payRes.data);

          const { ecpayUrl, aioCheckoutDto } = payRes.data;

          const form = document.createElement("form");
          form.method = "POST";
          form.action = ecpayUrl;
          form.target = "_blank";

          for (const key in aioCheckoutDto) {
            const input = document.createElement("input");
            input.type = "hidden";
            input.name = key;
            input.value = aioCheckoutDto[key];
            form.appendChild(input);
          }

          document.body.appendChild(form);
          form.submit();
          await fetchCartFromServer();
          navigate("/");
        } catch (err) {
          console.error("取得綠界連結失敗", err);
          setErrorMsg("無法跳轉綠界付款，請稍後再試");
        }
      } else {
        await fetchCartFromServer();
        navigate("/");
      }
    } catch (err) {
      console.error("建立訂單失敗", err);
      setErrorMsg("建立訂單失敗，請稍後再試");
    }
  };

  const subtotal = getTotalPrice();
  const total = subtotal;

  if (loading) {
    return <div className="text-center py-10">載入中...</div>;
  }

  return (
    <div className="max-w-5xl mx-auto p-4 space-y-8">
      {errorMsg && (
        <div className="bg-red-100 text-red-700 p-2 rounded text-sm">
          {errorMsg}
        </div>
      )}
      {successMsg && (
        <div className="bg-green-100 text-green-700 p-2 rounded text-sm">
          {successMsg}
        </div>
      )}

      {/* 購物車項目 */}
      <div className="border rounded-md p-4">
        <h2 className="text-lg font-bold mb-4">購物車（{items.length} 件）</h2>
        {items.map((item) => (
          <div
            key={item.id}
            className="flex items-center justify-between border-b pb-4 mb-4"
          >
            <div className="flex items-center space-x-4">
              <img
                src={item.image}
                alt={item.name}
                className="w-20 h-20 object-cover"
              />
              <div>
                <p className="font-medium">{item.name}</p>
                <p className="text-sm text-gray-500">NT${item.price}</p>
              </div>
            </div>
            <div className="flex items-center space-x-2">
              <button
                onClick={() => handleDecrease(item)}
                className="px-2 py-1 border rounded hover:bg-gray-100"
              >
                −
              </button>
              <span>{item.quantity}</span>
              <button
                onClick={() => handleIncrease(item)}
                className="px-2 py-1 border rounded hover:bg-gray-100"
              >
                ＋
              </button>
            </div>
            <p className="font-bold">NT${item.price * item.quantity}</p>
          </div>
        ))}
      </div>

      {/* 訂單資訊 */}
      <div className="grid md:grid-cols-2 gap-6">
        <div>
          <h3 className="font-semibold mb-2">送貨與付款</h3>
          <div className="space-y-4">
            <div>
              <label className="block mb-1 text-sm">城市/縣市</label>
              <select
                className="w-full border p-2"
                value={city}
                onChange={(e) => setCity(e.target.value)}
              >
                <option>台北市</option>
                <option>新北市</option>
                <option>台中市</option>
                <option>高雄市</option>
                <option>台南市</option>
              </select>
            </div>
            <div>
              <label className="block mb-1 text-sm">詳細地址</label>
              <input
                type="text"
                value={address}
                onChange={(e) => setAddress(e.target.value)}
                placeholder="例如：中山區南京東路3段20號"
                className="w-full border p-2"
              />
            </div>
            <div>
              <label className="block mb-1 text-sm">付款方式</label>
              <select
                className="w-full border p-2"
                value={paymentMethod}
                onChange={(e) => setPaymentMethod(e.target.value)}
              >
                <option value="CASH_ON_DELIVERY">貨到付款</option>
                <option value="ONLINE_PAYMENT">線上支付</option>
              </select>
            </div>
          </div>
        </div>

        <div className="border p-4 rounded-md">
          <h3 className="font-semibold mb-4">訂單資訊</h3>
          <div className="space-y-2 text-sm">
            <div className="flex justify-between">
              <span>小計</span>
              <span>NT${subtotal}</span>
            </div>
            <div className="border-t mt-2 pt-2 flex justify-between font-bold text-lg">
              <span>合計</span>
              <span>NT${total}</span>
            </div>
          </div>
          <button
            onClick={handleCheckout}
            className="mt-4 w-full bg-green-600 text-white py-2 rounded hover:bg-green-700 transition"
          >
            前往結帳
          </button>
        </div>
      </div>
    </div>
  );
}

export default Cart;
