import { createRouter, createWebHashHistory } from 'vue-router';
import { getNavigationByPath } from './navigation';
import AdminLayout from '@/layouts/AdminLayout.vue';
import Dashboard from '@/pages/Dashboard.vue';
import Orders from '@/pages/Orders.vue';
import Customers from '@/pages/Customers.vue';
import Inventory from '@/pages/Inventory.vue';
import Employees from '@/pages/Employees.vue';
import Services from '@/pages/Services.vue';
import Shops from '@/pages/Shops.vue';
import Wallet from '@/pages/Wallet.vue';
import Receipts from '@/pages/Receipts.vue';
import AI from '@/pages/AI.vue';
import Login from '@/pages/Login.vue';
import ChangePassword from '@/pages/ChangePassword.vue';
import { useUserStore } from '@/store';

const routes = [
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: { title: '登录', public: true }
  },
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/',
    component: AdminLayout,
    children: [
      { path: 'dashboard', name: 'dashboard', component: Dashboard, meta: { title: '仪表盘' } },
      { path: 'orders', name: 'orders', component: Orders, meta: { title: '订单管理' } },
      { path: 'customers', name: 'customers', component: Customers, meta: { title: '客户管理' } },
      { path: 'inventory', name: 'inventory', component: Inventory, meta: { title: '库存管理' } },
      { path: 'employees', name: 'employees', component: Employees, meta: { title: '员工管理' } },
      { path: 'services', name: 'services', component: Services, meta: { title: '服务项目' } },
      { path: 'shops', name: 'shops', component: Shops, meta: { title: '门店管理' } },
      { path: 'wallet', name: 'wallet', component: Wallet, meta: { title: '会员钱包' } },
      { path: 'receipts', name: 'receipts', component: Receipts, meta: { title: '小票模板' } },
      { path: 'ai', name: 'ai', component: AI, meta: { title: 'AI 助手' } },
      { path: 'account/password', name: 'change-password', component: ChangePassword, meta: { title: '修改密码' } }
    ]
  }
];

const router = createRouter({
  history: createWebHashHistory('/admin/'),
  routes,
  scrollBehavior() {
    return { top: 0 };
  }
});

router.beforeEach((to) => {
  const userStore = useUserStore();
  if (to.meta?.public) {
    if (to.name === 'login' && userStore.isAuthenticated) {
      return { path: '/dashboard' };
    }
    return true;
  }
  if (!userStore.isAuthenticated) {
    return { path: '/login', query: to.fullPath && to.fullPath !== '/' ? { redirect: to.fullPath } : {} };
  }
  return true;
});

router.afterEach((to) => {
  const navigation = getNavigationByPath(`/${String(to.name || '').replace(/^\//, '')}`);
  const pageTitle = to.meta?.title || navigation?.label || 'WashHelper';
  document.title = `${pageTitle} | WashHelper`;
});

export default router;
