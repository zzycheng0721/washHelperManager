import axios from 'axios';
import { ElMessage } from 'element-plus';

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
});

const API_KEY_STORAGE_KEY = 'washhelper.apiKey';
const SHOP_ID_STORAGE_KEY = 'washhelper.shopId';
const DEFAULT_API_KEY = 'your-secret-api-key-here';

function safeStorageGet(key, fallback = '') {
  if (typeof window === 'undefined' || !window.localStorage) {
    return fallback;
  }
  return window.localStorage.getItem(key) || fallback;
}

request.interceptors.request.use((config) => {
  const headers = config.headers || {};
  const apiKey = safeStorageGet(API_KEY_STORAGE_KEY, DEFAULT_API_KEY);
  const shopId = safeStorageGet(SHOP_ID_STORAGE_KEY, '');

  headers['X-API-Key'] = apiKey;
  if (shopId) {
    headers['X-Shop-Id'] = shopId;
  }

  config.headers = headers;
  return config;
});

request.interceptors.response.use(
  (response) => {
    const body = response.data;

    if (body && typeof body === 'object' && body.ok === false) {
      const message = body.message || '请求失败';
      ElMessage.error(message);
      return Promise.reject(new Error(message));
    }

    return body;
  },
  (error) => {
    const message =
      error?.response?.data?.message ||
      error?.response?.data?.error ||
      error?.message ||
      '网络请求失败';
    ElMessage.error(message);
    return Promise.reject(error);
  }
);

export default request;
