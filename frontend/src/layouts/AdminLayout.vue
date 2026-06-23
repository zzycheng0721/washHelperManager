<template>
  <div class="admin-layout">
    <aside class="admin-sidebar" :class="{ collapsed: appStore.sidebarCollapsed }">
      <div class="brand">
        <div class="brand-mark">W</div>
        <div v-if="!appStore.sidebarCollapsed" class="brand-copy">
          <strong>WashHelper</strong>
          <span>洗衣店后台管理</span>
        </div>
      </div>

      <el-menu
        class="admin-menu"
        :default-active="activePath"
        :collapse="appStore.sidebarCollapsed"
        :collapse-transition="false"
        background-color="transparent"
        text-color="rgba(237, 242, 255, 0.78)"
        active-text-color="#ff9b4d"
        @select="handleSelect"
      >
        <el-menu-item v-for="item in navigationItems" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>
            <span class="menu-title">{{ item.label }}</span>
            <small class="menu-hint">{{ item.hint }}</small>
          </template>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-foot">
        <p>门店 ID</p>
        <strong>{{ appStore.currentShopId }}</strong>
        <span>{{ userStore.shopName }}</span>
      </div>
    </aside>

    <div class="admin-main">
      <header class="admin-topbar">
        <div class="topbar-left">
          <el-button text class="collapse-btn" @click="appStore.toggleSidebar">
            <el-icon>
              <component :is="appStore.sidebarCollapsed ? Expand : Fold" />
            </el-icon>
          </el-button>
          <div>
            <p class="topbar-kicker">当前页面</p>
            <h2>{{ currentTitle }}</h2>
          </div>
        </div>

        <div class="topbar-right">
          <el-tag effect="dark" type="warning" round>
            {{ appStore.apiKeyLabel }}
          </el-tag>
          <el-dropdown trigger="click">
            <el-button round>
              <el-avatar size="small">{{ userInitial }}</el-avatar>
              <span class="user-name">{{ userStore.name }}</span>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="refreshPage">刷新页面</el-dropdown-item>
                <el-dropdown-item @click="syncShopId">同步门店 ID</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="admin-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Expand, Fold } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';
import { navigationItems } from '@/router/navigation';
import { useAppStore, useUserStore } from '@/store';

const route = useRoute();
const router = useRouter();
const appStore = useAppStore();
const userStore = useUserStore();

const activePath = computed(() => `/${String(route.name || '').replace(/^\//, '')}` || route.path || '/dashboard');

const currentTitle = computed(() => {
  return route.meta?.title || navigationItems.find((item) => item.path === route.path)?.label || '仪表盘';
});

const userInitial = computed(() => {
  const source = userStore.name || 'W';
  return source.charAt(0).toUpperCase();
});

function handleSelect(path) {
  if (path && path !== route.path) {
    router.push(path);
  }
}

function refreshPage() {
  appStore.touchRefresh();
  window.location.reload();
}

async function syncShopId() {
  const { value } = await ElMessageBox.prompt('请输入当前门店 ID', '同步门店', {
    confirmButtonText: '保存',
    cancelButtonText: '取消',
    inputPattern: /^\d+$/,
    inputErrorMessage: '门店 ID 必须是数字'
  });
  appStore.setShopId(value);
}
</script>
