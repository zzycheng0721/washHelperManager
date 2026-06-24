import { defineStore } from 'pinia';

const STORAGE = {
  apiKey: 'washhelper.apiKey',
  shopId: 'washhelper.shopId',
  shopName: 'washhelper.shopName',
  sidebarCollapsed: 'washhelper.sidebarCollapsed',
  user: 'washhelper.user',
  loggedIn: 'washhelper.loggedIn'
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

function removeStorage(key) {
  if (typeof window === 'undefined' || !window.localStorage) {
    return;
  }
  window.localStorage.removeItem(key);
}

function readUser() {
  const raw = readStorage(STORAGE.user, '');
  if (!raw) return null;
  try {
    return JSON.parse(raw);
  } catch (e) {
    return null;
  }
}

export const useAppStore = defineStore('app', {
  state: () => ({
    sidebarCollapsed: readStorage(STORAGE.sidebarCollapsed, '0') === '1',
    apiKey: readStorage(STORAGE.apiKey, DEFAULT_API_KEY),
    shopId: readStorage(STORAGE.shopId, '1'),
    shopName: readStorage(STORAGE.shopName, ''),
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
    setShopName(value) {
      this.shopName = value || '';
      writeStorage(STORAGE.shopName, this.shopName);
    },
    touchRefresh() {
      this.lastRefreshAt = new Date().toISOString();
    }
  }
});

export const useUserStore = defineStore('user', {
  state: () => {
    const cached = readUser();
    return {
      id: cached?.id || null,
      name: cached?.name || '',
      role: cached?.role || '',
      shopId: cached?.shopId || null,
      shopName: cached?.shopName || '',
      phone: cached?.phone || '',
      loggedIn: readStorage(STORAGE.loggedIn, '0') === '1'
    };
  },
  getters: {
    isAuthenticated: (state) => state.loggedIn && !!state.id
  },
  actions: {
    setUser(payload) {
      this.id = payload?.id ?? null;
      this.name = payload?.name ?? '';
      this.role = payload?.role ?? '';
      this.shopId = payload?.shopId ?? null;
      this.shopName = payload?.shopName ?? '';
      this.phone = payload?.phone ?? '';
      this.loggedIn = true;
      writeStorage(STORAGE.user, JSON.stringify({
        id: this.id,
        name: this.name,
        role: this.role,
        shopId: this.shopId,
        shopName: this.shopName,
        phone: this.phone
      }));
      writeStorage(STORAGE.loggedIn, '1');
    },
    updateProfile(profile) {
      Object.assign(this, profile || {});
      writeStorage(STORAGE.user, JSON.stringify({
        id: this.id,
        name: this.name,
        role: this.role,
        shopId: this.shopId,
        shopName: this.shopName,
        phone: this.phone
      }));
    },
    logout() {
      this.id = null;
      this.name = '';
      this.role = '';
      this.shopId = null;
      this.shopName = '';
      this.phone = '';
      this.loggedIn = false;
      removeStorage(STORAGE.user);
      removeStorage(STORAGE.loggedIn);
    }
  }
});
