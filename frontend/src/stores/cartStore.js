import { create } from 'zustand';
import axiosInstance from '../api/axiosFrontend';

const useCartStore = create((set, get) => ({
  // 狀態
  isCartOpen: false,
  items: [],

  // UI 控制
  openCart: () => set({ isCartOpen: true }),
  closeCart: () => set({ isCartOpen: false }),
  toggleCart: () => set((state) => ({ isCartOpen: !state.isCartOpen })),

  // 加入商品到本地 state
  addItem: (product, quantity = 1) =>
    set((state) => {
      const existingItem = state.items.find((item) => item.id === product.id);

      if (existingItem) {
        return {
          items: state.items.map((item) =>
            item.id === product.id
              ? { ...item, quantity: item.quantity + quantity }
              : item
          ),
        };
      } else {
        return {
          items: [...state.items, { ...product, quantity }],
        };
      }
    }),

  // 從本地 state 移除商品
  removeItem: (productId) =>
    set((state) => ({
      items: state.items.filter((item) => item.id !== productId),
    })),

  // 更新商品數量至本地 state
  updateQuantity: (productId, quantity) =>
    set((state) => {
      if (quantity <= 0) {
        return {
          items: state.items.filter((item) => item.id !== productId),
        };
      }

      return {
        items: state.items.map((item) =>
          item.id === productId ? { ...item, quantity } : item
        ),
      };
    }),

  // 清空購物車本地資料
  clearCart: () => set({ items: [] }),

  // 計算總數量
  getTotalQuantity: () => {
    const { items } = get();
    return Array.isArray(items)
      ? items.reduce((total, item) => total + (item.quantity || 0), 0)
      : 0;
  },

  // 計算總金額
  getTotalPrice: () => {
    const { items } = get();
    return Array.isArray(items)
      ? items.reduce((total, item) => total + (item.price || 0) * (item.quantity || 0), 0)
      : 0;
  },

  // ===== 🔽 後端 API 相關功能 🔽 =====

  // 新增商品到後端購物車
  addItemToServer: async (productId, quantity = 1) => {
    try {
      const response = await axiosInstance.post('/cart/items/more', [
        { productid: productId, quantity },
      ]);
      return response.data;
    } catch (error) {
      console.error('新增商品到購物車失敗', error);
      throw error;
    }
  },

  // 更新數量至後端
  updateItemQuantityOnServer: async (cartDetailId, quantity) => {
    try {
      const response = await axiosInstance.put(`/cart/items/${cartDetailId}`, {
        quantity,
      });
      return response.data;
    } catch (error) {
      console.error('更新商品數量失敗', error);
      throw error;
    }
  },

  // 從後端刪除單一商品
  deleteItemFromServer: async (cartDetailId) => {
    try {
      const response = await axiosInstance.delete(`/cart/items/${cartDetailId}`);
      return response.data;
    } catch (error) {
      console.error('刪除商品失敗', error);
      throw error;
    }
  },

  // 從後端清空購物車
  clearCartFromServer: async () => {
    try {
      const response = await axiosInstance.delete('/cart/delete');
      set({ items: [] }); // 同步清空本地資料
      return response.data;
    } catch (error) {
      console.error('清空購物車失敗', error);
      throw error;
    }
  },

  // 從後端取得購物車資料，並轉換格式
  fetchCartFromServer: async () => {
    try {
      const response = await axiosInstance.get('/cart/check');
      const { cartdetails } = response.data;

      if (Array.isArray(cartdetails)) {
        const formattedItems = cartdetails.map((item) => ({
          id: item.productid,
          name: item.productname,
          image: item.productimgurl,
          price: item.unitprice,
          quantity: item.quantity,
          cartDetailId: item.cartdetailid,
        }));

        set({ items: formattedItems });
        console.log('購物車資料已更新', formattedItems);
      } else {
        console.warn('cartdetails 格式錯誤');
        set({ items: [] });
      }
    } catch (error) {
      console.error('取得購物車資料失敗', error);
      set({ items: [] });
    }
  },
}));

export default useCartStore;
