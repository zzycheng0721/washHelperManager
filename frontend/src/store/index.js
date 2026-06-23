import { defineStore } from 'pinia';

const STORAGE = {
  apiKey: 'washhelper.apiKey',
  shopId: 'washhelper.shopId',
  sidebarCollapsed: 'washhelper.sidebarCollapsed'
};

const DEFAULT_API_KEY = 'your-secret-api-key-here';

function readStorage(key, fallback = '') {
  if (typeof window === 'undefined' || !window.localStorage) {
    return fallback;
  }
  return window.localStorage.getItem(key) || fallback;
}

function writeStorage(key, value) {
  if (typeof window === 'undefined' || !window.localStorage) {
    return;
  }
  window.localStorage.setItem(key, value);
}

export const useAppStore = defineStore('app', {
  state: () => ({
    sidebarCollapsed: readStorage(STORAGE.sidebarCollapsed, '0') === '1',
    apiKey: readStorage(STORAGE.apiKey, DEFAULT_API_KEY),
    shopId: readStorage(STORAGE.shopId, '1'),
    lastRefreshAt: ''
  }),
  getters: {
    apiKeyLabel: (state) => (state.apiKey === DEFAULT_API_KEY ? '默认密钥' : '已配置'),
    currentShopId: (state) => Number(state.shopId) || 1
  },
  actions: {
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed;
      writeStorage(STORAGE.sidebarCollapsed, this.sidebarCollapsed ? '1' : '0');
    },
    setApiKey(value) {
      this.apiKey = value?.trim() || DEFAULT_API_KEY;
      writeStorage(STORAGE.apiKey, this.apiKey);
    },
    setShopId(value) {
      this.shopId = String(value || '1');
      writeStorage(STORAGE.shopId, this.shopId);
    },
    touchRefresh() {
      this.lastRefreshAt = new Date().toISOString();
    }
  }
});

export const useUserStore = defineStore('user', {
  state: () => ({
    name: '门店管理员',
    role: '总部管理员',
    shopName: 'WashHelper 总部'
  }),
  actions: {
    updateProfile(profile) {
      Object.assign(this, profile || {});
    }
  }
});
