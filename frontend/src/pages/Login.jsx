import { useState, useEffect } from "react";
import { FaEye, FaEyeSlash, FaFacebook } from "react-icons/fa";
import { Link, useNavigate } from "react-router-dom";
import useUserStore from "../stores/userStore";

function Login() {
  const [account, setAccount] = useState("kevin_lin88");
  const [password, setPassword] = useState("MyStr0ngP@sswd#1");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const login = useUserStore((state) => state.login);
  const isAuthenticated = useUserStore((state) => state.isAuthenticated);

  useEffect(() => {
    if (isAuthenticated) {
      navigate("/User", { replace: true });
    }
  }, [isAuthenticated, navigate]);

  const handleLogin = async (e) => {
    e.preventDefault();
    setError(null);

    try {     
      await login({ account, password });
      navigate("/User");
    } catch (err) {
      setError("登入失敗，請確認帳號密碼");
    }
  };

  return (
    <div className="max-w-md mx-auto bg-white p-8 rounded shadow-md">
      <h2 className="text-2xl font-bold mb-6">登入</h2>

      <form onSubmit={handleLogin}>
        <div className="mb-4">
          <input
            type="text"
            placeholder="電郵或手機號碼"
            value={account}
            onChange={(e) => setAccount(e.target.value)}
            className="w-full border-b border-gray-300 py-2 focus:outline-none"
            required
          />
        </div>

        <div className="mb-2 relative">
          <input
            type={showPassword ? "text" : "password"}
            placeholder="密碼"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full border-b border-gray-300 py-2 focus:outline-none"
            required
          />
          <div
            className="absolute right-2 top-2 cursor-pointer"
            onClick={() => setShowPassword(!showPassword)}
          >
            {showPassword ? <FaEye /> : <FaEyeSlash />}
          </div>
        </div>

        {error && <p className="text-red-500 text-sm mb-4">{error}</p>}

        <div className="mb-4 text-sm text-blue-600 cursor-pointer">忘記密碼？</div>

        <button
          type="submit"
          className="w-full bg-orange-500 text-white py-2 rounded font-bold hover:bg-orange-600"
        >
          開始購物吧！
        </button>
      </form>

      <div className="text-center mt-10">
        <p className="text-lg font-bold">還不是會員？</p>
        <Link to="/SignFlow">
          <button className="mt-2 px-6 py-2 border border-orange-500 text-orange-500 font-semibold rounded hover:bg-orange-50">
            註冊會員
          </button>
        </Link>
      </div>
    </div>
  );
}

export default Login;
