<template>
  <section>
    <PageHeader
      eyebrow="CUSTOMERS"
      title="客户管理"
      description="维护客户档案、会员等级与联系方式，沉淀长期复购资产。"
    >
      <template #actions>
        <el-button :icon="Refresh" round @click="loadCustomers">刷新</el-button>
        <el-button type="primary" :icon="Plus" round @click="openCreate">新建客户</el-button>
      </template>
    </PageHeader>

    <div class="stat-grid">
      <StatCard title="客户总数" :value="total" hint="含已删除" :icon="User" />
      <StatCard title="金卡会员" :value="memberCount('gold')" hint="高净值" tone="warning" :icon="Star" />
      <StatCard title="银卡会员" :value="memberCount('silver')" hint="忠实复购" :icon="Medal" />
      <StatCard title="普通客户" :value="memberCount('regular')" hint="待孵化" :icon="UserFilled" />
    </div>

    <el-card shadow="never" class="page-card">
      <div class="filter-grid">
        <el-input v-model="filters.search" placeholder="搜索姓名 / 手机号" clearable :prefix-icon="Search" @keyup.enter="loadCustomers" />
        <el-select v-model="filters.type" placeholder="会员类型" clearable>
          <el-option v-for="opt in memberOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
        </el-select>
        <el-select v-model="filters.pageSize">
          <el-option label="10 / 页" :value="10" />
          <el-option label="20 / 页" :value="20" />
          <el-option label="50 / 页" :value="50" />
        </el-select>
        <div style="display: flex; gap: 8px">
          <el-button type="primary" plain @click="loadCustomers">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="page-card">
      <el-table :data="customers" v-loading="loading" stripe>
        <el-table-column label="客户" min-width="220" fixed>
          <template #default="{ row }">
            <div style="display: flex; align-items: center; gap: 12px">
              <el-avatar :size="38" :src="row.avatarUrl">{{ initial(row.name) }}</el-avatar>
              <div>
                <strong>{{ row.name }}</strong>
                <p class="soft" style="margin: 4px 0 0">编号 {{ row.customerId || '—' }}</p>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系方式" min-width="140" />
        <el-table-column label="会员等级" width="120">
          <template #default="{ row }">
            <el-tag :type="memberTone(row.memberType)" effect="dark" round>{{ memberLabel(row.memberType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="200">
          <template #default="{ row }">
            <span class="soft">{{ row.notes || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="170">
          <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button text @click="recordContact(row)">记录联系</el-button>
            <el-button text type="danger" @click="removeCustomer(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <div class="empty-wrap">暂无客户数据</div>
        </template>
      </el-table>

      <div style="display: flex; justify-content: flex-end; margin-top: 16px">
        <el-pagination
          v-model:current-page="filters.page"
          :page-size="filters.pageSize"
          :total="total"
          layout="total, prev, pager, next, jumper"
          @current-change="loadCustomers"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑客户' : '新建客户'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="dialog-form">
        <div class="form-grid">
          <el-form-item label="客户姓名" prop="name">
            <el-input v-model="form.name" placeholder="如：李女士" />
          </el-form-item>
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="form.phone" placeholder="手机号" />
          </el-form-item>
          <el-form-item label="会员类型" prop="memberType">
            <el-select v-model="form.memberType" style="width: 100%">
              <el-option v-for="opt in memberOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="客户编号">
            <el-input v-model="form.customerId" placeholder="留空自动生成" />
          </el-form-item>
          <el-form-item label="备注" class="full-span">
            <el-input v-model="form.notes" type="textarea" :rows="2" placeholder="补充信息（取衣偏好、忌讳等）" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitCustomer">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { Medal, Plus, Refresh, Search, Star, User, UserFilled } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import PageHeader from '@/components/PageHeader.vue';
import StatCard from '@/components/StatCard.vue';
import { customersApi } from '@/api';
import { formatDateTime } from '@/utils/format';

const memberOptions = [
  { value: 'regular', label: '普通客户' },
  { value: 'silver', label: '银卡会员' },
  { value: 'gold', label: '金卡会员' }
];

const filters = reactive({ search: '', type: '', page: 1, pageSize: 20 });
const customers = ref([]);
const total = ref(0);
const loading = ref(false);

const dialogVisible = ref(false);
const submitting = ref(false);
const editing = ref(null);
const formRef = ref();
const form = reactive({ customerId: '', name: '', phone: '', memberType: 'regular', notes: '' });

const rules = {
  name: [{ required: true, message: '请输入客户姓名', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入有效手机号', trigger: 'blur' }]
};

const memberCount = (type) => customers.value.filter((c) => c.memberType === type).length;

function memberLabel(type) {
  return memberOptions.find((opt) => opt.value === type)?.label || type || '—';
}

function memberTone(type) {
  return { gold: 'warning', silver: 'info', regular: '' }[type] || '';
}

function initial(name) {
  return (name || '客').charAt(0);
}

async function loadCustomers() {
  loading.value = true;
  try {
    const result = await customersApi.list({
      page: filters.page,
      pageSize: filters.pageSize,
      type: filters.type || undefined,
      search: filters.search || undefined
    });
    customers.value = result.items || [];
    total.value = result.total || 0;
  } catch (error) {
    customers.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
}

function resetFilters() {
  filters.search = '';
  filters.type = '';
  filters.page = 1;
  loadCustomers();
}

function openCreate() {
  editing.value = null;
  Object.assign(form, { customerId: '', name: '', phone: '', memberType: 'regular', notes: '' });
  dialogVisible.value = true;
}

function openEdit(row) {
  editing.value = row;
  Object.assign(form, {
    customerId: row.customerId || '',
    name: row.name || '',
    phone: row.phone || '',
    memberType: row.memberType || 'regular',
    notes: row.notes || ''
  });
  dialogVisible.value = true;
}

async function submitCustomer() {
  await formRef.value.validate();
  submitting.value = true;
  try {
    if (editing.value) {
      await customersApi.update(editing.value.customerId || editing.value.id, { ...form });
      ElMessage.success('客户信息已更新');
    } else {
      await customersApi.create({ ...form });
      ElMessage.success('客户创建成功');
    }
    dialogVisible.value = false;
    loadCustomers();
  } finally {
    submitting.value = false;
  }
}

async function recordContact(row) {
  try {
    await customersApi.contact(row.customerId || row.id);
    ElMessage.success('已记录联系');
  } catch (error) {
    /* handled */
  }
}

async function removeCustomer(row) {
  await ElMessageBox.confirm(`确认删除客户「${row.name}」？该操作不可恢复。`, '提示', { type: 'warning' });
  await customersApi.remove(row.customerId || row.id);
  ElMessage.success('客户已删除');
  loadCustomers();
}

onMounted(loadCustomers);
</script>