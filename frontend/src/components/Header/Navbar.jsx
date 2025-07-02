import { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import useCartStore from "../../stores/cartStore";
import CartModal from "./CartModel";
import { FaUserCircle, FaShoppingCart, FaBars, FaTimes } from "react-icons/fa";
import logo from "../../assets/logo3.png";

const Navbar = () => {
  const location = useLocation();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const { toggleCart, getTotalQuantity } = useCartStore();
  const cartItemCount = getTotalQuantity();

  const toggleMobileMenu = () => setIsMobileMenuOpen((prev) => !prev);
  const closeMobileMenu = () => setIsMobileMenuOpen(false);

  return (
    <>
      <nav className="
        fixed top-4 left-1/2 transform -translate-x-1/2 z-50 
        w-[85.2%] max-w-8xl px-6 py-3 
        bg-white/30 backdrop-blur-md 
        border border-white/20 shadow-md 
        rounded-full transition-all duration-300
      ">
        <div className="flex justify-between items-center">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-2">
            <img src={logo} alt="Logo" className="h-9 w-auto object-contain" />
          </Link>

          {/* 桌面導覽連結 */}
          <div className="hidden md:flex items-center space-x-6 text-base font-medium text-gray-800">
            {[
              { to: "/store", label: "精選商店" },
              { to: "/about", label: "關於良野" },
              { to: "/news", label: "最新消息" },
              { to: "/contact", label: "聯絡我們" },
            ].map(({ to, label }) => (
              <Link
                key={to}
                to={to}
                className="relative group transition-colors duration-300"
              >
                <span className="hover:text-gray-600">{label}</span>
                <span className="absolute left-0 -bottom-1 h-[2px] w-0 bg-logo-lightBlue transition-all duration-300 group-hover:w-full"></span>
              </Link>
            ))}

            <Link to="/login" className="text-gray-700 text-2xl hover:text-logo-blue">
              <FaUserCircle />
            </Link>

            <button onClick={toggleCart} className="relative">
              <FaShoppingCart className="text-gray-700 text-2xl hover:text-logo-blue" />
              {cartItemCount > 0 && (
                <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs font-bold h-5 w-5 flex items-center justify-center rounded-full shadow ring-2 ring-white">
                  {cartItemCount > 99 ? "99+" : cartItemCount}
                </span>
              )}
            </button>
          </div>

          {/* 手機漢堡選單 */}
          <div className="md:hidden">
            <button onClick={toggleMobileMenu}>
              <FaBars className="text-2xl" />
            </button>
          </div>
        </div>
      </nav>

      {/* 手機選單（滑出 + 遮罩） */}
      <div
        className={`fixed inset-0 z-[45] transition-all duration-300 md:hidden ${isMobileMenuOpen ? "visible opacity-100" : "invisible opacity-0"
          }`}
      >
        {/* 遮罩背景 */}
        <div
          className={`absolute inset-0 bg-black transition-opacity duration-300 ${isMobileMenuOpen ? "bg-opacity-40" : "bg-opacity-0"
            }`}
          onClick={closeMobileMenu}
        ></div>

        {/* 側邊滑出選單 */}
        <div
          className={`absolute top-0 left-0 h-full w-72 bg-white/60 backdrop-blur-md border-r border-white/20 text-gray-800 transform transition-transform duration-300 ease-in-out rounded-tr-2xl rounded-br-2xl shadow-lg ${isMobileMenuOpen ? "translate-x-0" : "-translate-x-full"
            }`}
        >
          <div className="flex justify-between items-center px-4 py-4 border-b border-gray-300">
            <span className="text-lg font-bold">良野選單</span>
            <button onClick={closeMobileMenu}>
              <FaTimes className="text-2xl" />
            </button>
          </div>

          <div className="px-4 py-4 space-y-4">
            <Link to="/store" onClick={closeMobileMenu} className="block hover:text-logo-lightBlue">精選商店</Link>
            <Link to="/about" onClick={closeMobileMenu} className="block hover:text-logo-lightBlue">關於良野</Link>
            <Link to="/news" onClick={closeMobileMenu} className="block hover:text-logo-lightBlue">最新消息</Link>
            <Link to="/contact" onClick={closeMobileMenu} className="block hover:text-logo-lightBlue">聯絡我們</Link>
            <Link to="/login" onClick={closeMobileMenu} className="flex hover:text-logo-lightBlue items-center">
              <FaUserCircle className="mr-2" /> 會員登入
            </Link>
            <button
              onClick={() => {
                closeMobileMenu();
                toggleCart();
              }}
              className="flex hover:text-logo-lightBlue items-center"
            >
              <FaShoppingCart className="mr-2" /> 購物車
              {cartItemCount > 0 && (
                <span className="ml-2 bg-logo-lightBlue text-white text-xs px-2 py-1 rounded-full">
                  {cartItemCount}
                </span>
              )}
            </button>
          </div>
        </div>
      </div>

      {/* 購物車彈窗 */}
      <CartModal />
    </>
  );
};

export default Navbar;
