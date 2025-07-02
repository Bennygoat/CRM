import { useEffect, useState } from "react";
import { FaTimes, FaPlus, FaMinus, FaShoppingCart } from "react-icons/fa";
import useCartStore from "../../stores/cartStore";
import { Link } from "react-router-dom";

const CartModal = () => {
  const {
    isCartOpen,
    closeCart,
    items,
    updateQuantity,
    removeItem,
    getTotalPrice,
    getTotalQuantity,
    updateItemQuantityOnServer,
    deleteItemFromServer,
    clearCartFromServer,
    fetchCartFromServer,
  } = useCartStore();

  const [shouldRender, setShouldRender] = useState(false);
  const [animateIn, setAnimateIn] = useState(false);
  const totalPrice = getTotalPrice();
  const totalQuantity = getTotalQuantity();

  // 初始載入購物車
  useEffect(() => {
    fetchCartFromServer();
  }, []);

  useEffect(() => {
    if (isCartOpen) {
      useCartStore.getState().fetchCartFromServer();
    }
  }, [isCartOpen]);

  // 控制動畫與顯示
  useEffect(() => {
    if (isCartOpen) {
      setShouldRender(true);
      const timer = setTimeout(() => {
        requestAnimationFrame(() => setAnimateIn(true));
      }, 100);
      return () => clearTimeout(timer);
    } else {
      setAnimateIn(false);
      const timeout = setTimeout(() => setShouldRender(false), 300);
      return () => clearTimeout(timeout);
    }
  }, [isCartOpen]);

  if (!shouldRender) return null;

  return (
    <>
      {/* 背景遮罩 */}
      <div
        className={`
          fixed inset-0 bg-black bg-opacity-50 z-[55] transition-opacity duration-300
          ${animateIn ? "opacity-40" : "opacity-0"}
        `}
        onClick={closeCart}
      />

      {/* 購物車視窗 */}
      <div
        className={`
          fixed z-[60] bg-white shadow-xl flex flex-col
          transition-all duration-300 ease-out
          ${animateIn ? "opacity-100" : "opacity-0"}

          md:top-30 md:right-0 md:mr-[-4px] md:w-96 md:max-h-[80vh] md:rounded-lg md:border md:border-gray-200
          md:transform ${animateIn ? "md:translate-x-0" : "md:translate-x-full"}

          max-md:top-0 max-md:mt-[-0px] max-md:left-0 max-md:ml-[-8px] max-md:w-72 max-md:max-w-[85vw] max-md:h-full
          max-md:transform ${
            animateIn ? "max-md:translate-x-0" : "max-md:-translate-x-full"
          }
        `}
      >
        {/* 標題列 */}
        <div className="flex justify-between items-center p-4 border-b border-gray-200 bg-logo-blue text-white md:bg-white md:text-gray-800 md:rounded-t-lg">
          <div className="flex items-center space-x-2">
            <FaShoppingCart className="text-lg" />
            <h3 className="font-semibold text-lg">購物車</h3>
            {totalQuantity > 0 && (
              <span className="bg-logo-lightBlue text-white text-xs px-2 py-1 rounded-full">
                {totalQuantity}
              </span>
            )}
          </div>
          <button
            onClick={closeCart}
            className="p-1 hover:bg-logo-lightBlue/20 rounded transition-colors md:hover:bg-gray-100 md:text-gray-600"
          >
            <FaTimes className="text-lg" />
          </button>
        </div>

        {/* 購物車內容 */}
        <div className="flex-1 overflow-y-auto p-4 cart-scroll">
          {items.length === 0 ? (
            <div className="text-center py-8 text-gray-500">
              <FaShoppingCart className="mx-auto text-4xl mb-4 text-gray-300" />
              <p>購物車是空的</p>
              <p className="text-sm mt-2">快去選購您喜歡的商品吧！</p>
            </div>
          ) : (
            <>
              {/* 清空購物車按鈕 */}
              <div className="flex justify-between items-center mb-4">
                <span className="text-sm text-gray-600">
                  共 {totalQuantity} 件商品
                </span>
                <button
                  onClick={async () => {
                    if (window.confirm("確定要清空購物車嗎？")) {
                      await clearCartFromServer();
                    }
                  }}
                  className="text-xs text-red-500 hover:text-red-700 transition-colors"
                >
                  清空購物車
                </button>
              </div>

              {/* 商品列表 */}
              <div className="space-y-4">
                {items.map((item) => (
                  <div
                    key={item.id}
                    className="flex items-center space-x-3 p-3 border border-gray-200 rounded-lg hover:shadow-md transition-shadow"
                  >
                    {/* 商品圖片 */}
                    <div className="w-16 h-16 bg-gray-200 rounded-lg flex items-center justify-center overflow-hidden">
                      {item.image ? (
                        <img
                          src={item.image}
                          alt={item.name}
                          className="w-full h-full object-cover"
                        />
                      ) : (
                        <span className="text-gray-400 text-xs">無圖片</span>
                      )}
                    </div>

                    {/* 商品資訊 */}
                    <div className="flex-1 min-w-0">
                      <h4 className="font-medium text-sm truncate">
                        {item.name}
                      </h4>
                      <p className="text-logo-blue font-semibold">
                        ${item.price}
                      </p>

                      {/* 數量控制 */}
                      <div className="flex items-center mt-2 space-x-2">
                        <button
                          onClick={() => {
                            if (item.cartDetailId) {
                              updateItemQuantityOnServer(
                                item.cartDetailId,
                                item.quantity - 1
                              );
                            }
                            updateQuantity(item.id, item.quantity - 1);
                          }}
                          className="w-6 h-6 flex items-center justify-center bg-gray-200 hover:bg-gray-300 rounded transition-colors"
                        >
                          <FaMinus className="text-xs" />
                        </button>
                        <span className="w-8 text-center text-sm font-medium">
                          {item.quantity}
                        </span>
                        <button
                          onClick={() => {
                            if (item.cartDetailId) {
                              updateItemQuantityOnServer(
                                item.cartDetailId,
                                item.quantity + 1
                              );
                            }
                            updateQuantity(item.id, item.quantity + 1);
                          }}
                          className="w-6 h-6 flex items-center justify-center bg-logo-blue hover:bg-logo-blue/90 text-white rounded transition-colors"
                        >
                          <FaPlus className="text-xs" />
                        </button>
                      </div>
                    </div>

                    {/* 移除按鈕 */}
                    <button
                      onClick={() => {
                        if (item.cartDetailId) {
                          deleteItemFromServer(item.cartDetailId);
                        }
                        removeItem(item.id);
                      }}
                      className="text-red-500 hover:text-red-700 p-1 transition-colors"
                    >
                      <FaTimes />
                    </button>
                  </div>
                ))}
              </div>
            </>
          )}
        </div>

        {/* 結算區域 */}
        {items.length > 0 && (
          <div className="border-t border-gray-200 p-4 bg-gray-50 md:rounded-b-lg">
            <div className="flex justify-between items-center mb-4">
              <span className="font-semibold text-lg">總計：</span>
              <span className="font-bold text-xl text-logo-blue">
                ${totalPrice.toFixed(2)}
              </span>
            </div>
            <Link to="/cart" onClick={closeCart}>
              <button className="w-full bg-logo-blue hover:bg-logo-blue/90 text-white font-semibold py-3 px-4 rounded-lg transition-colors">
                前往結帳
              </button>
            </Link>
          </div>
        )}
      </div>
    </>
  );
};

export default CartModal;
