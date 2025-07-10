import { create } from 'zustand';
import axiosInstance from '../api/axiosFrontend';

// 從 localStorage 初始化
const storedToken = localStorage.getItem('token');
const storedUser = localStorage.getItem('user');

// 設定初始 axios header
if (storedToken) {
  axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${storedToken}`;
}

const setTokenHeader = (token) => {
  axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;
};

const clearTokenHeader = () => {
  delete axiosInstance.defaults.headers.common['Authorization'];
};

const useUserStore = create((set) => ({
  user: storedUser ? JSON.parse(storedUser) : null,
  token: storedToken || null,
  isAuthenticated: !!storedToken,

  login: async (credentials) => {
    try {
      const response = await axiosInstance.post('/customer/auth/login', credentials);
      const {
        token,
        account,
        customerName,
        email,
        birthday,
        address,
      } = response.data;

      const user = { account, customerName, email, birthday, address };

      // 儲存 token 和 user
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));

      // 設定 header
      setTokenHeader(token);

      // 更新狀態
      set({ user, token, isAuthenticated: true });
    } catch (error) {
      console.error('Login failed:', error);
      throw error.response?.data?.message || '登入失敗';
    }
  },

  // 新增: 儲存 Firebase 登入後的 token 和 user
  saveFirebaseLogin: ({ token, account, customerName, email, birthday, address }) => {
    const user = { account, customerName, email, birthday, address };

    // 儲存 token 和 user
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));

    // 設定 header
    setTokenHeader(token);

    // 更新狀態
    set({ user, token, isAuthenticated: true });
  },

  logout: () => {
    clearTokenHeader();
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    set({ user: null, token: null, isAuthenticated: false });
  },

  fetchProfile: async () => {
    try {
      const response = await axiosInstance.get('/customer/auth/profile');
      const {
        account,
        customerName,
        email,
        birthday,
        address,
      } = response.data;

      const user = { account, customerName, email, birthday, address };
      set({ user });
      localStorage.setItem('user', JSON.stringify(user));
    } catch (error) {
      console.error('Fetch profile failed:', error);
    }
  },
}));

export default useUserStore;
