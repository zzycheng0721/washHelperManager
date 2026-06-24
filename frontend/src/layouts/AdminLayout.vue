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
        text-color="#4b5563"
        active-text-color="#ff8f42"
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
        <p>当前门店</p>
        <strong>{{ appStore.shopName || `门店 ${appStore.currentShopId}` }}</strong>
        <span>ID · {{ appStore.currentShopId }}</span>

        <div v-if="!appStore.sidebarCollapsed" class="sidebar-user">
          <div class="sidebar-user-info">
            <el-avatar size="small" class="sidebar-avatar">{{ userInitial }}</el-avatar>
            <div class="sidebar-user-meta">
              <strong>{{ userStore.name || '未登录' }}</strong>
              <span>{{ roleLabel }}</span>
            </div>
          </div>
          <div class="sidebar-user-actions">
            <el-button size="small" round class="sidebar-action-btn" @click="goChangePassword">
              <el-icon><Key /></el-icon>
              <span>修改密码</span>
            </el-button>
            <el-button size="small" type="danger" plain round class="sidebar-action-btn" @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              <span>退出登录</span>
            </el-button>
          </div>
        </div>
      </div>
    </aside>

    <div class="admin-main">
      <header class="admin-topbar slim">
        <div class="topbar-left">
          <el-button text class="collapse-btn" @click="appStore.toggleSidebar">
            <el-icon>
              <component :is="appStore.sidebarCollapsed ? Expand : Fold" />
            </el-icon>
          </el-button>
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
import { Expand, Fold, Key, SwitchButton } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';
import { navigationItems } from '@/router/navigation';
import { useAppStore, useUserStore } from '@/store';

const route = useRoute();
const router = useRouter();
const appStore = useAppStore();
const userStore = useUserStore();

const activePath = computed(() => `/${String(route.name || '').replace(/^\//, '')}` || route.path || '/dashboard');

const userInitial = computed(() => {
  const source = userStore.name || 'W';
  return source.charAt(0).toUpperCase();
});

const roleLabel = computed(() => {
  const map = { admin: '门店管理员', cashier: '收银员', worker: '员工', staff: '员工' };
  return map[userStore.role] || userStore.role || '门店账号';
});

function handleSelect(path) {
  if (path && path !== route.path) {
    router.push(path);
  }
}

function goChangePassword() {
  if (route.path !== '/account/password') {
    router.push('/account/password');
  }
}

async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定退出当前登录吗？', '退出登录', {
      confirmButtonText: '退出',
      cancelButtonText: '取消',
      type: 'warning'
    });
  } catch (e) {
    return;
  }
  userStore.logout();
  router.replace('/login');
}
</script>

<style scoped>
.admin-topbar.slim {
  min-height: 56px;
  padding: 0 20px;
}

.sidebar-user {
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px dashed rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sidebar-user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.sidebar-avatar {
  background: linear-gradient(135deg, #ff9b4d 0%, #f4c25f 100%);
  color: #ffffff;
  font-weight: 700;
}

.sidebar-user-meta {
  display: flex;
  flex-direction: column;
  line-height: 1.25;
  min-width: 0;
}

.sidebar-user-meta strong {
  font-size: 14px;
  color: #1f2937;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sidebar-user-meta span {
  font-size: 12px;
  color: #94a3b8;
}

.sidebar-user-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}

.sidebar-user-actions .sidebar-action-btn {
  width: 100%;
  margin: 0 !important;
  padding: 8px 12px;
  height: 34px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  line-height: 1;
  box-sizing: border-box;
}

.sidebar-user-actions .sidebar-action-btn :deep(.el-icon) {
  margin: 0;
  font-size: 14px;
}

.sidebar-user-actions .sidebar-action-btn :deep(span) {
  margin: 0;
  line-height: 1;
}
</style>
