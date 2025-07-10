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

// firebase新增
const handleGoogleLogin = async () => {
  try {
    const result = await signInWithPopup(auth, googleProvider);
    const user = result.user;
    const idToken = await user.getIdToken();
    console.log(idToken);

    const response = await axios.post("/customer/auth/firebase-login", {
      provider: "google",
      token: idToken,
    });

    const { token, account, customerName } = response.data;

    login({ token, account, customerName }); // 存到你自己的 store
    navigate("/User");

  } catch (err) {
    console.error("Google 登入失敗", err);
    setError("Google 登入失敗，請稍後再試");
  }
};

const handleFacebookLogin = async () => {
  try {
    const result = await signInWithPopup(auth, facebookProvider);
    const user = result.user;
    const idToken = await user.getIdToken();

    const response = await axios.post("/customer/auth/firebase-login", {
      provider: "facebook",
      token: idToken,
    });

    const { token, account, customerName } = response.data;

    login({ token, account, customerName });
    navigate("/User");

  } catch (err) {
    console.error("Facebook 登入失敗", err);
    setError("Facebook 登入失敗，請稍後再試");
  }
};
//

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

        <div className="mb-4 text-sm text-blue-600 cursor-pointer">
          忘記密碼？
        </div>

        <button
          type="submit"
          className="w-full bg-sky-500 text-white py-2 rounded font-bold hover:bg-sky-600"
        >
          開始購物吧！
        </button>

        {/*          firebase新增 */}
        <div className="mt-6 flex flex-col gap-3">
          <button
            onClick={handleGoogleLogin}
            className="bg-white border border-gray-300 py-2 rounded hover:bg-gray-50"
          >
            使用 Google 登入
          </button>

          <button
            onClick={handleFacebookLogin}
            className="bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
          >
            使用 Facebook 登入
          </button>
        </div>
        {/*          */}

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
