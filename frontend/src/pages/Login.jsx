import { useState, useEffect } from "react";
import { FaEye, FaEyeSlash, FaFacebook, FaGoogle } from "react-icons/fa";
import { Link, useNavigate } from "react-router-dom";
import { auth, googleProvider, facebookProvider } from "../firebase";
import { signInWithPopup } from "firebase/auth";
import useUserStore from "../stores/userStore";
import axios from "../api/axiosFrontend";

function Login() {
  const [account, setAccount] = useState("");
  const [password, setPassword] = useState("");
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

  const handleGoogleLogin = async () => {
    setError(null);
    try {
      const result = await signInWithPopup(auth, googleProvider);
      const user = result.user;

      console.log("Google 登入成功:", user);

      const payload = {
        email: user.email,
        account: user.email,
        customerName: user.displayName || "Google使用者",
        password: user.uid + "@G",
      };

      try {
        await login({ account: user.email, password: user.uid + "@G" });
        navigate("/User");
      } catch (err) {
        const errorMessage =
          typeof err === "string"
            ? err // 後端直接回錯誤字串
            : err.response?.data?.message || err.message || "未知錯誤";

        console.error("登入失敗:", errorMessage);

        if (
          errorMessage.toLowerCase().includes("帳號") &&
          errorMessage.toLowerCase().includes("密碼")
        ) {
          console.log("檢測到帳號密碼錯誤，自動註冊...");

          try {
            const response = await axios.post("/customer/register", payload);
            console.log("註冊成功:", response.data);

            await login({ account: user.email, password: user.uid + "@G" });
            navigate("/User");
          } catch (registerError) {
            console.error(
              "自動註冊失敗:",
              registerError.response?.data || registerError.message
            );
            setError("自動註冊失敗，請稍後再試");
          }
        } else {
          setError("Google 登入失敗，請稍後再試");
        }
      }
    } catch (err) {
      console.error("Google 登入失敗 (Firebase):", err);
      setError("Google 登入失敗，請稍後再試");
    }
  };

  const handleFacebookLogin = async () => {
    setError(null);
    try {
      const result = await signInWithPopup(auth, facebookProvider);
      const user = result.user;

      console.log("Facebook 登入成功:", user);

      const payload = {
        email: user.email,
        account: user.email,
        customerName: user.displayName || "Facebook使用者",
        password: user.uid,
      };

      try {
        await login({ account: user.email, password: user.uid });
        navigate("/User");
      } catch (err) {
        const errorMessage = err.response?.data?.message || "";
        console.error("登入失敗:", errorMessage);

        if (errorMessage.includes("帳號密碼錯誤")) {
          console.log("帳號不存在，自動註冊...");

          try {
            const response = await axios.post("/customer/register", payload);
            console.log("註冊成功:", response.data);

            await login({ account: user.email, password: user.uid });
            navigate("/User");
          } catch (registerError) {
            console.error(
              "自動註冊失敗:",
              registerError.response?.data || registerError.message
            );
            setError("自動註冊失敗，請稍後再試");
          }
        } else {
          setError("Facebook 登入失敗，請稍後再試");
        }
      }
    } catch (err) {
      console.error("Facebook 登入失敗 (Firebase):", err);
      setError("Facebook 登入失敗，請稍後再試");
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

        <div className="mb-4 text-sm text-blue-600 cursor-pointer">
          忘記密碼？
        </div>

        <button
          type="submit"
          className="w-full bg-sky-500 text-white py-2 rounded font-bold hover:bg-sky-600"
        >
          開始購物吧！
        </button>
      </form>

      <div className="my-6 flex justify-center gap-6">
        <button
          onClick={handleGoogleLogin}
          className="bg-red-500 hover:bg-red-600 text-white p-3 rounded-full"
          title="使用 Google 登入"
          type="button"
        >
          <FaGoogle className="text-xl" />
        </button>

        <button
          onClick={handleFacebookLogin}
          className="bg-blue-600 hover:bg-blue-700 text-white p-3 rounded-full"
          title="使用 Facebook 登入"
          type="button"
        >
          <FaFacebook className="text-xl" />
        </button>
      </div>

      <div className="text-center mt-10">
        <p className="text-lg font-bold">還不是會員？</p>
        <Link to="/SignFlow">
          <button className="mt-2 px-6 py-2 border border-sky-500 text-sky-500 font-semibold rounded hover:bg-sky-50">
            註冊會員
          </button>
        </Link>
      </div>
    </div>
  );
}

export default Login;
