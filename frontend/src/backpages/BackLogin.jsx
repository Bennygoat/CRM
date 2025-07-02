import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import useBackUserStore from "../stores/useBackUserStore";
import backgroundImage from "../assets/signbackground.jpg";

function BackLogin() {
  const navigate = useNavigate();
  const { loginBackUser } = useBackUserStore();

  useEffect(() => {
    document.body.classList.remove("cursor-frontend");
  }, []);

  const [account, setAccount] = useState("admin");
  const [password, setPassword] = useState("Admin123!@#");
  const [rememberMe, setRememberMe] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!account || !password) {
      alert("請輸入帳號與密碼");
      return;
    }

    try {
      // ✅ 改為等待 loginBackUser 回傳 user 物件
      const user = await loginBackUser({ account, password });

      if (rememberMe) {
        localStorage.setItem("rememberedAccount", account);
      }

      const role = user?.roleName?.toLowerCase();
      console.log("登入成功，角色:", role);
      if (role === "admin") navigate("/users/management");
      else if (role === "editor") navigate("/crm/dashboard");
      else if (role === "manager") navigate("/erp/dashboard");
      else navigate("/");
    } catch (err) {
      console.error("登入失敗:", err);
      alert("登入失敗，請確認帳號與密碼");
    }
  };

  return (
    <div
      className="relative w-full h-screen bg-cover bg-center bg-no-repeat"
      style={{ backgroundImage: `url(${backgroundImage})` }}
    >
      <div className="absolute inset-0 flex justify-center items-center">
        <div className="bg-black/100 p-10 rounded-2xl shadow-2xl w-[90%] max-w-md">
          <h2 className="text-3xl font-bold text-center mb-6 text-white">
            後台登入
          </h2>

          <form className="flex flex-col space-y-4" onSubmit={handleSubmit}>
            <input
              type="text"
              placeholder="帳號"
              value={account}
              onChange={(e) => setAccount(e.target.value)}
              className="px-4 py-2 rounded-lg bg-white border border-gray-300 text-gray-800 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
            <input
              type="password"
              placeholder="密碼"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="px-4 py-2 rounded-lg bg-white border border-gray-300 text-gray-800 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-400"
            />

            <div className="flex items-center justify-between text-sm text-gray-400">
              <label className="flex items-center">
                <input
                  type="checkbox"
                  checked={rememberMe}
                  onChange={() => setRememberMe(!rememberMe)}
                  className="mr-2"
                />
                記住帳號
              </label>
              <a href="#" className="text-blue-600 hover:underline">
                忘記密碼？
              </a>
            </div>

            <button
              type="submit"
              className="bg-gray-600 hover:bg-gray-700 text-white font-semibold py-2 rounded-lg transition duration-200"
            >
              登入
            </button>
          </form>

          <div className="text-center text-sm text-gray-600 mt-6">
            {" "}
            <Link to="/" className="text-blue-600 hover:underline">
              返回前台
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

export default BackLogin;
