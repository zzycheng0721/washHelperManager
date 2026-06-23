<template>
  <section>
    <PageHeader
      eyebrow="OVERVIEW"
      title="经营仪表盘"
      description="实时监控门店运营关键指标，掌握订单流转、会员资产与库存预警。"
    >
      <template #actions>
        <el-select v-model="rangeKey" style="width: 140px">
          <el-option label="今日" value="today" />
          <el-option label="本周" value="week" />
          <el-option label="本月" value="month" />
        </el-select>
        <el-button :icon="Refresh" round @click="refreshAll">刷新数据</el-button>
      </template>
    </PageHeader>

    <div class="stat-grid">
      <StatCard
        title="今日订单"
        :value="stats.ordersToday"
        :hint="rangeLabel"
        trend="+12.4%"
        :icon="Tickets"
      />
      <StatCard
        title="待处理订单"
        :value="stats.pendingOrders"
        hint="包含待洗 / 待取"
        trend="实时"
        :icon="Bell"
      />
      <StatCard
        title="今日营业额"
        :value="formatMoney(stats.revenueToday)"
        hint="含会员消费"
        trend="+8.2%"
        :icon="Coin"
      />
      <StatCard
        title="活跃会员"
        :value="stats.activeMembers"
        hint="近 30 天"
        trend="+3"
        :icon="UserFilled"
      />
    </div>

    <div class="split-grid" style="margin-top: 16px">
      <el-card shadow="never">
        <template #header>
          <div class="flex-between">
            <strong>最新订单动态</strong>
            <el-button text type="primary" @click="$router.push('/orders')">前往订单 →</el-button>
          </div>
        </template>
        <el-table :data="recentOrders" stripe v-loading="loadingOrders">
          <el-table-column prop="orderId" label="订单号" min-width="120" />
          <el-table-column prop="customerName" label="客户" min-width="100" />
          <el-table-column prop="service" label="服务" min-width="120" />
          <el-table-column label="金额" width="100" align="right">
            <template #default="{ row }">{{ formatMoney(row.totalPrice) }}</template>
          </el-table-column>
          <el-table-column label="状态" width="110">
            <template #default="{ row }">
              <el-tag :type="orderStatusTone(row.status)" effect="dark" round>
                {{ orderStatusLabel(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" min-width="160">
            <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
          </el-table-column>
        </el-table>
      </el-card>

      <div class="stack">
        <el-card shadow="never">
          <template #header>
            <div class="flex-between">
              <strong>库存预警</strong>
              <el-button text type="primary" @click="$router.push('/inventory')">前往库存 →</el-button>
            </div>
          </template>
          <el-empty v-if="!inventoryAlerts.length" description="暂无低库存物料" :image-size="80" />
          <div v-else class="stack">
            <div v-for="item in inventoryAlerts" :key="item.id" class="mini-card flex-between">
              <div>
                <strong>{{ item.name }}</strong>
                <p class="soft" style="margin: 4px 0 0">{{ item.category || '通用耗材' }}</p>
              </div>
              <div style="text-align: right">
                <strong style="color: var(--admin-accent)">
                  {{ item.available }} / {{ item.total }} {{ item.unit }}
                </strong>
                <p class="soft" style="margin: 4px 0 0">预警阈值 {{ item.alertThreshold }}</p>
              </div>
            </div>
          </div>
        </el-card>

        <el-card shadow="never">
          <template #header>
            <strong>快捷入口</strong>
          </template>
          <div class="quick-grid">
            <button v-for="entry in quickEntries" :key="entry.path" class="quick-cell" @click="$router.push(entry.path)">
              <el-icon :size="22"><component :is="entry.icon" /></el-icon>
              <span>{{ entry.label }}</span>
            </button>
          </div>
        </el-card>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import {
  Bell,
  Box,
  Coin,
  CreditCard,
  Document,
  Grid,
  List,
  Plus,
  Refresh,
  Tickets,
  UserFilled
} from '@element-plus/icons-vue';
import PageHeader from '@/components/PageHeader.vue';
import StatCard from '@/components/StatCard.vue';
import { ordersApi, inventoryApi, customersApi } from '@/api';
import { formatDateTime, formatMoney } from '@/utils/format';

const rangeKey = ref('today');
const rangeLabel = computed(() => ({ today: '今日', week: '本周', month: '本月' }[rangeKey.value]));

const stats = reactive({
  ordersToday: 0,
  pendingOrders: 0,
  revenueToday: 0,
  activeMembers: 0
});

const recentOrders = ref([]);
const inventoryAlerts = ref([]);
const loadingOrders = ref(false);

const quickEntries = [
  { path: '/orders', label: '新建订单', icon: Plus },
  { path: '/customers', label: '会员管理', icon: UserFilled },
  { path: '/wallet', label: '会员充值', icon: CreditCard },
  { path: '/services', label: '服务项目', icon: Grid },
  { path: '/inventory', label: '库存补给', icon: Box },
  { path: '/receipts', label: '小票模板', icon: Document }
];

function orderStatusLabel(status) {
  return {
    pending: '待处理',
    washing: '洗涤中',
    pickup: '待取',
    completed: '已完成',
    cancelled: '已取消'
  }[status] || status || '-';
}

function orderStatusTone(status) {
  return {
    pending: 'warning',
    washing: 'primary',
    pickup: 'info',
    completed: 'success',
    cancelled: 'danger'
  }[status] || 'info';
}

async function loadOrders() {
  loadingOrders.value = true;
  try {
    const result = await ordersApi.list({ page: 1, pageSize: 8 });
    recentOrders.value = result.items || [];
    stats.ordersToday = result.total || result.items?.length || 0;
    stats.pendingOrders = (result.items || []).filter((o) => ['pending', 'washing', 'pickup'].includes(o.status)).length;
    stats.revenueToday = (result.items || []).reduce((sum, o) => sum + Number(o.totalPrice || 0), 0);
  } catch (error) {
    recentOrders.value = [];
  } finally {
    loadingOrders.value = false;
  }
}

async function loadInventory() {
  try {
    const result = await inventoryApi.list({ page: 1, pageSize: 50 });
    const items = result.items || [];
    inventoryAlerts.value = items
      .filter((item) => Number(item.available) <= Number(item.alertThreshold || 0))
      .slice(0, 4);
  } catch (error) {
    inventoryAlerts.value = [];
  }
}

async function loadCustomers() {
  try {
    const result = await customersApi.list({ page: 1, pageSize: 1 });
    stats.activeMembers = result.total || 0;
  } catch (error) {
    stats.activeMembers = 0;
  }
}

function refreshAll() {
  loadOrders();
  loadInventory();
  loadCustomers();
}

onMounted(refreshAll);
</script>

<style scoped>
.quick-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.quick-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 76px;
  padding: 8px;
  border-radius: 14px;
  border: 1px solid var(--admin-border);
  background: rgba(255, 255, 255, 0.03);
  color: var(--admin-text);
  cursor: pointer;
  transition: all 0.2s ease;
}

.quick-cell:hover {
  border-color: var(--admin-accent);
  background: var(--admin-accent-soft);
  color: var(--admin-accent);
}
</style>