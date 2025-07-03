import React, { useState, useEffect } from "react";
import axios from "../../api/axiosFrontend"; // 請確認這是你的 axios 實例路徑

function PersonalInfo() {
  const [form, setForm] = useState({
    customerName: "",
    email: "",
    birthday: "",
    address: "",
    recipient: "",
    recipientPhone: "",
    country: "",
    city: "",
    district: "",
  });

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const res = await axios.get("/customer/profile");
        const { customerName, email, birthday, address } = res.data;
        setForm((prev) => ({
          ...prev,
          customerName,
          email,
          birthday,
          address,
        }));
      } catch (err) {
        console.error("取得會員資料失敗:", err);
      }
    };

    fetchProfile();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleCancel = () => {
    window.location.reload();
  };

  const handleSave = () => {
    console.log("儲存資料：", form);
    alert("已儲存變更");
  };

  return (
    <div className="grid md:grid-cols-2 gap-6 text-sm text-gray-800">
      {/* 左：會員資料 */}
      <div className="bg-white rounded-2xl p-6 shadow border border-gray-100">
        <h2 className="text-lg font-semibold mb-4">會員資料</h2>

        {/* 姓名 */}
        <div className="mb-4">
          <label className="block mb-1 text-gray-600 font-medium">姓名</label>
          <input
            name="customerName"
            value={form.customerName}
            onChange={handleChange}
            className="w-full border border-gray-200 rounded-xl px-4 py-2"
          />
        </div>

        {/* 電郵 */}
        <div className="mb-4">
          <label className="block mb-1 text-gray-600 font-medium">電郵</label>
          <input
            name="email"
            value={form.email}
            onChange={handleChange}
            className="w-full border border-gray-200 rounded-xl px-4 py-2"
          />
        </div>

        {/* 生日 */}
        <div className="mb-4">
          <label className="block mb-1 text-gray-600 font-medium">生日日期</label>
          <input
            type="date"
            name="birthday"
            value={form.birthday}
            onChange={handleChange}
            className="w-full border border-gray-200 rounded-xl px-4 py-2"
          />
        </div>

        {/* 地址 */}
        <div className="mb-4">
          <label className="block mb-1 text-gray-600 font-medium">地址</label>
          <input
            name="address"
            value={form.address}
            onChange={handleChange}
            className="w-full border border-gray-200 rounded-xl px-4 py-2"
            placeholder="請輸入詳細地址"
          />
        </div>

        {/* 密碼連結 */}
        <div className="mb-4">
          <label className="block mb-1 text-gray-600 font-medium">密碼</label>
          <a href="#" className="text-blue-600 hover:underline">
            設定新的密碼
          </a>
        </div>

        {/* 刪除帳號 */}
        <div className="mt-6">
          <button
            onClick={() => {
              const confirmed = window.confirm("確定要刪除帳號嗎？此動作將無法復原！");
              if (confirmed) {
                console.log("帳號已刪除");
                alert("帳號已刪除");
              }
            }}
            className="text-red-500 border border-red-200 hover:bg-red-50 px-5 py-2 rounded-xl transition"
          >
            刪除帳號
          </button>
        </div>
      </div>

      {/* 右：送貨與付款資料 */}
      <div className="bg-white rounded-2xl p-6 shadow border border-gray-100">
        <h2 className="text-lg font-semibold mb-4">送貨與付款資料</h2>

        {/* 收件人 */}
        <div className="mb-4">
          <label className="block mb-1 text-gray-600 font-medium">收件人</label>
          <input
            name="recipient"
            value={form.recipient}
            onChange={handleChange}
            className="w-full border border-gray-200 rounded-xl px-4 py-2"
            placeholder="請輸入收件人姓名"
          />
        </div>

        {/* 收件人電話 */}
        <div className="mb-4">
          <label className="block mb-1 text-gray-600 font-medium">收件人電話號碼</label>
          <input
            name="recipientPhone"
            value={form.recipientPhone}
            onChange={handleChange}
            className="w-full border border-gray-200 rounded-xl px-4 py-2"
            placeholder="0912 345 678"
          />
        </div>

        {/* 國家 */}
        <div className="mb-4">
          <label className="block mb-1 text-gray-600 font-medium">送貨地點</label>
          <select
            name="country"
            value={form.country}
            onChange={handleChange}
            className="w-full border border-gray-200 rounded-xl px-4 py-2"
          >
            <option value="">請選擇</option>
            <option value="台灣">台灣</option>
            <option value="日本">日本</option>
            <option value="美國">美國</option>
          </select>
        </div>

        {/* 城市 */}
        <div className="mb-4">
          <label className="block mb-1 text-gray-600 font-medium">城市 / 縣</label>
          <select
            name="city"
            value={form.city}
            onChange={handleChange}
            className="w-full border border-gray-200 rounded-xl px-4 py-2"
          >
            <option value="">請選擇</option>
            <option value="台北市">台北市</option>
            <option value="新北市">新北市</option>
            <option value="高雄市">高雄市</option>
          </select>
        </div>

        {/* 地區 */}
        <div className="mb-4">
          <label className="block mb-1 text-gray-600 font-medium">地區</label>
          <select
            name="district"
            value={form.district}
            onChange={handleChange}
            className="w-full border border-gray-200 rounded-xl px-4 py-2"
          >
            <option value="">請選擇</option>
            <option value="信義區">信義區</option>
            <option value="中山區">中山區</option>
            <option value="三重區">三重區</option>
          </select>
        </div>
      </div>

      {/* 儲存與取消按鈕 */}
      <div className="col-span-2 flex justify-end gap-4 mt-4">
        <button
          onClick={handleCancel}
          className="px-6 py-2 border border-gray-300 rounded-xl text-gray-700 hover:bg-gray-100 transition"
        >
          取消
        </button>
        <button
          onClick={handleSave}
          className="px-6 py-2 bg-logo-tan text-white rounded-xl hover:opacity-90 transition"
        >
          儲存變更
        </button>
      </div>
    </div>
  );
}

export default PersonalInfo;
