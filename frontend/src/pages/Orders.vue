<template>
  <section>
    <PageHeader
      eyebrow="ORDERS"
      title="订单管理"
      description="跟踪洗涤订单流转，支持快速建单、状态推进与小票打印。"
    >
      <template #actions>
        <el-button :icon="Refresh" round @click="loadOrders">刷新</el-button>
        <el-button type="primary" :icon="Plus" round @click="openCreate">新建订单</el-button>
      </template>
    </PageHeader>

    <el-card shadow="never" class="page-card">
      <div class="filter-grid">
        <el-input v-model="filters.search" placeholder="搜索订单号 / 客户姓名 / 电话" clearable :prefix-icon="Search" @keyup.enter="loadOrders" />
        <el-select v-model="filters.status" placeholder="状态筛选" clearable>
          <el-option v-for="opt in statusOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
        </el-select>
        <el-select v-model="filters.pageSize" placeholder="每页条数">
          <el-option label="10 / 页" :value="10" />
          <el-option label="20 / 页" :value="20" />
          <el-option label="50 / 页" :value="50" />
        </el-select>
        <div style="display: flex; gap: 8px">
          <el-button type="primary" plain @click="loadOrders">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="page-card">
      <el-table :data="orders" v-loading="loading" stripe>
        <el-table-column prop="orderId" label="订单号" min-width="140" fixed />
        <el-table-column label="客户" min-width="160">
          <template #default="{ row }">
            <div>
              <strong>{{ row.customerName || '散客' }}</strong>
              <p class="soft" style="margin: 4px 0 0">{{ row.customerPhone || '—' }}</p>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="service" label="服务" min-width="120" />
        <el-table-column label="数量" width="80" align="center">
          <template #default="{ row }">{{ row.quantity || 1 }}</template>
        </el-table-column>
        <el-table-column label="金额" width="110" align="right">
          <template #default="{ row }">
            <strong style="color: var(--admin-accent)">{{ formatMoney(row.totalPrice) }}</strong>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusTone(row.status)" effect="dark" round>{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="170">
          <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="advanceStatus(row)" :disabled="row.status === 'completed' || row.status === 'cancelled'">
              推进状态
            </el-button>
            <el-button text @click="printOrder(row)">打印</el-button>
            <el-button text type="danger" @click="removeOrder(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <div class="empty-wrap">暂无订单，点击右上角新建一笔吧</div>
        </template>
      </el-table>

      <div style="display: flex; justify-content: flex-end; margin-top: 16px">
        <el-pagination
          v-model:current-page="filters.page"
          :page-size="filters.pageSize"
          :total="total"
          layout="total, prev, pager, next, jumper"
          @current-change="loadOrders"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新建订单" width="640px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="dialog-form">
        <div class="form-grid">
          <el-form-item label="客户姓名" prop="customerName">
            <el-input v-model="form.customerName" placeholder="如：张小姐" />
          </el-form-item>
          <el-form-item label="联系电话" prop="customerPhone">
            <el-input v-model="form.customerPhone" placeholder="手机号" />
          </el-form-item>
          <el-form-item label="会员类型">
            <el-select v-model="form.customerMemberType" placeholder="选择会员类型" clearable>
              <el-option label="普通客户" value="regular" />
              <el-option label="银卡会员" value="silver" />
              <el-option label="金卡会员" value="gold" />
            </el-select>
          </el-form-item>
          <el-form-item label="服务项目" prop="service">
            <el-input v-model="form.service" placeholder="如：毛呢大衣干洗" />
          </el-form-item>
          <el-form-item label="服务类型">
            <el-select v-model="form.serviceType" placeholder="如：干洗 / 水洗">
              <el-option label="干洗" value="dryClean" />
              <el-option label="水洗" value="wash" />
              <el-option label="护理" value="care" />
              <el-option label="鞋包" value="shoes" />
            </el-select>
          </el-form-item>
          <el-form-item label="数量" prop="quantity">
            <el-input-number v-model="form.quantity" :min="1" :max="999" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="单价（元）" prop="unitPrice">
            <el-input-number v-model="form.unitPrice" :min="0" :precision="2" :step="1" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="合计金额">
            <el-input :model-value="computedTotal" disabled />
          </el-form-item>
          <el-form-item label="特殊需求" class="full-span">
            <el-input v-model="form.specialReqs" type="textarea" :rows="2" placeholder="如：袖口有顽固污渍" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitOrder">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { Plus, Refresh, Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import PageHeader from '@/components/PageHeader.vue';
import { ordersApi } from '@/api';
import { formatDateTime, formatMoney } from '@/utils/format';

const statusOptions = [
  { value: 'pending', label: '待处理' },
  { value: 'washing', label: '洗涤中' },
  { value: 'pickup', label: '待取' },
  { value: 'completed', label: '已完成' },
  { value: 'cancelled', label: '已取消' }
];

const statusFlow = ['pending', 'washing', 'pickup', 'completed'];

const filters = reactive({ search: '', status: '', page: 1, pageSize: 20 });
const orders = ref([]);
const total = ref(0);
const loading = ref(false);

const dialogVisible = ref(false);
const submitting = ref(false);
const formRef = ref();
const form = reactive({
  customerName: '',
  customerPhone: '',
  customerMemberType: 'regular',
  service: '',
  serviceType: 'dryClean',
  specialReqs: '',
  quantity: 1,
  unitPrice: 0
});

const rules = {
  customerName: [{ required: true, message: '请填写客户姓名', trigger: 'blur' }],
  service: [{ required: true, message: '请填写服务项目', trigger: 'blur' }],
  quantity: [{ required: true, message: '请填写数量', trigger: 'change' }],
  unitPrice: [{ required: true, message: '请填写单价', trigger: 'change' }]
};

const computedTotal = computed(() => formatMoney(Number(form.quantity || 0) * Number(form.unitPrice || 0)));

function statusLabel(status) {
  return statusOptions.find((opt) => opt.value === status)?.label || status || '-';
}

function statusTone(status) {
  return {
    pending: 'warning',
    washing: 'primary',
    pickup: 'info',
    completed: 'success',
    cancelled: 'danger'
  }[status] || 'info';
}

async function loadOrders() {
  loading.value = true;
  try {
    const result = await ordersApi.list({
      page: filters.page,
      pageSize: filters.pageSize,
      status: filters.status || undefined,
      search: filters.search || undefined
    });
    orders.value = result.items || [];
    total.value = result.total || 0;
  } catch (error) {
    orders.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
}

function resetFilters() {
  filters.search = '';
  filters.status = '';
  filters.page = 1;
  loadOrders();
}

function openCreate() {
  Object.assign(form, {
    customerName: '',
    customerPhone: '',
    customerMemberType: 'regular',
    service: '',
    serviceType: 'dryClean',
    specialReqs: '',
    quantity: 1,
    unitPrice: 0
  });
  dialogVisible.value = true;
}

async function submitOrder() {
  await formRef.value.validate();
  submitting.value = true;
  try {
    await ordersApi.create({
      ...form,
      totalPrice: Number(form.quantity || 0) * Number(form.unitPrice || 0),
      status: 'pending'
    });
    ElMessage.success('订单已创建');
    dialogVisible.value = false;
    loadOrders();
  } finally {
    submitting.value = false;
  }
}

async function advanceStatus(row) {
  const idx = statusFlow.indexOf(row.status);
  const next = idx >= 0 && idx < statusFlow.length - 1 ? statusFlow[idx + 1] : 'completed';
  await ordersApi.updateStatus(row.id, next);
  ElMessage.success(`已推进到「${statusLabel(next)}」`);
  loadOrders();
}

async function printOrder(row) {
  await ordersApi.print(row.id);
  ElMessage.success('打印任务已发送');
}

async function removeOrder(row) {
  await ElMessageBox.confirm(`确认删除订单 ${row.orderId}？`, '提示', { type: 'warning' });
  await ordersApi.remove(row.id);
  ElMessage.success('订单已删除');
  loadOrders();
}

onMounted(loadOrders);
</script>