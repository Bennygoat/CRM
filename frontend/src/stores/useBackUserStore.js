import { create } from 'zustand';
import axiosInstance from '../api/axiosBackend';

// ✅ 安全解析 localStorage 中的 JSON
function getParsedStorage(key) {
  try {
    const raw = localStorage.getItem(key);
    if (raw && raw !== 'undefined') {
      return JSON.parse(raw);
    }
  } catch (e) {
    console.warn(`⚠️ 無法解析 localStorage 的 ${key}，已清除`);
    localStorage.removeItem(key);
  }
  return null;
}

// ✅ 初始化 localStorage 中的資料
const storedToken = localStorage.getItem('back_token');
const storedUser = getParsedStorage('back_user');

// ✅ 設定預設的 axios header
if (storedToken) {
  axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${storedToken}`;
}

const useBackUserStore = create((set) => ({
  backUser: storedUser,
  backToken: storedToken || null,
  isBackAuthenticated: !!storedToken,

  loginBackUser: async (credentials) => {
    try {
      const res = await axiosInstance.post('/user/auth/login', credentials);
      const { token, ...user } = res.data; 

      localStorage.setItem('back_token', token);
      localStorage.setItem('back_user', JSON.stringify(user));
      axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;

      set({ backUser: user, backToken: token, isBackAuthenticated: true });
      return user;
    } catch (error) {
      console.error('後台登入失敗:', error);
      throw error;
    }
  },

  logoutBackUser: () => {
    delete axiosInstance.defaults.headers.common['Authorization'];
    localStorage.removeItem('back_token');
    localStorage.removeItem('back_user');
    set({ backUser: null, backToken: null, isBackAuthenticated: false });
  },

  fetchBackProfile: async () => {
    try {
      const res = await axiosInstance.get('/backauth/profile');
      set({ backUser: res.data });
      localStorage.setItem('back_user', JSON.stringify(res.data));
    } catch (err) {
      console.error('後台使用者資訊取得失敗', err);
    }
  },
}));

export default useBackUserStore;
