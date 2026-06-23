<template>
  <section>
    <PageHeader
      eyebrow="EMPLOYEES"
      title="员工管理"
      description="管理门店员工账号、角色与登录状态，保障收银、收衣等核心岗位有序运转。"
    >
      <template #actions>
        <el-button :icon="Refresh" round @click="loadEmployees">刷新</el-button>
        <el-button type="primary" :icon="Plus" round @click="openCreate">添加员工</el-button>
      </template>
    </PageHeader>

    <div class="stat-grid">
      <StatCard title="员工总数" :value="total" hint="含管理员" :icon="UserFilled" />
      <StatCard title="管理员" :value="roleCount('admin')" hint="拥有完整权限" tone="warning" :icon="Setting" />
      <StatCard title="收银员" :value="roleCount('cashier')" hint="收银开单" :icon="CreditCard" />
      <StatCard title="洗涤工" :value="roleCount('worker')" hint="洗护处理" :icon="Brush" />
    </div>

    <el-card shadow="never" class="page-card">
      <div class="filter-grid">
        <el-input v-model="filters.search" placeholder="搜索姓名 / 电话" clearable :prefix-icon="Search" />
        <el-select v-model="filters.role" placeholder="角色筛选" clearable>
          <el-option v-for="opt in roleOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
        </el-select>
        <el-select v-model="filters.status" placeholder="状态" clearable>
          <el-option label="启用" value="active" />
          <el-option label="停用" value="inactive" />
        </el-select>
        <div></div>
      </div>
    </el-card>

    <el-card shadow="never" class="page-card">
      <el-table :data="filteredList" v-loading="loading" stripe>
        <el-table-column label="员工" min-width="220" fixed>
          <template #default="{ row }">
            <div style="display: flex; align-items: center; gap: 12px">
              <el-avatar :size="38" :src="row.avatarUrl">{{ initial(row.name) }}</el-avatar>
              <div>
                <strong>{{ row.name }}</strong>
                <p class="soft" style="margin: 4px 0 0">{{ row.phone || '—' }}</p>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="140">
          <template #default="{ row }">
            <el-tag :type="roleTone(row.role)" effect="dark" round>{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 'active'"
              @change="(val) => toggleStatus(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="最近登录" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.lastLoginAt, '尚未登录') }}</template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="170">
          <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button text @click="resetPassword(row)">重置密码</el-button>
            <el-button text type="danger" @click="removeEmployee(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <div class="empty-wrap">暂无员工数据</div>
        </template>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑员工' : '新增员工'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="dialog-form">
        <div class="form-grid">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="form.name" placeholder="如：小王" />
          </el-form-item>
          <el-form-item label="电话">
            <el-input v-model="form.phone" placeholder="手机号" />
          </el-form-item>
          <el-form-item label="角色" prop="role">
            <el-select v-model="form.role" style="width: 100%">
              <el-option v-for="opt in roleOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status" style="width: 100%">
              <el-option label="启用" value="active" />
              <el-option label="停用" value="inactive" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="!editing" label="初始密码" prop="password" class="full-span">
            <el-input v-model="form.password" type="password" placeholder="至少 6 位" show-password />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { Brush, CreditCard, Plus, Refresh, Search, Setting, UserFilled } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import PageHeader from '@/components/PageHeader.vue';
import StatCard from '@/components/StatCard.vue';
import { employeesApi } from '@/api';
import { formatDateTime } from '@/utils/format';

const roleOptions = [
  { value: 'admin', label: '管理员' },
  { value: 'cashier', label: '收银员' },
  { value: 'worker', label: '洗涤工' },
  { value: 'staff', label: '普通员工' }
];

const filters = reactive({ search: '', role: '', status: '' });
const list = ref([]);
const loading = ref(false);
const total = ref(0);

const dialogVisible = ref(false);
const submitting = ref(false);
const editing = ref(null);
const formRef = ref();
const form = reactive({ name: '', phone: '', role: 'staff', status: 'active', password: '' });

const rules = {
  name: [{ required: true, message: '请填写姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  password: [{ required: true, message: '请填写初始密码', trigger: 'blur', min: 6 }]
};

const filteredList = computed(() => {
  return list.value.filter((item) => {
    if (filters.search) {
      const text = `${item.name || ''}${item.phone || ''}`;
      if (!text.includes(filters.search)) return false;
    }
    if (filters.role && item.role !== filters.role) return false;
    if (filters.status && item.status !== filters.status) return false;
    return true;
  });
});

function roleCount(role) {
  return list.value.filter((i) => i.role === role).length;
}

function roleLabel(role) {
  return roleOptions.find((opt) => opt.value === role)?.label || role || '员工';
}

function roleTone(role) {
  return { admin: 'warning', cashier: 'primary', worker: 'success' }[role] || 'info';
}

function initial(name) {
  return (name || '员').charAt(0);
}

async function loadEmployees() {
  loading.value = true;
  try {
    const result = await employeesApi.list({ page: 1, pageSize: 200 });
    list.value = result.items || [];
    total.value = result.total || list.value.length;
  } catch (error) {
    list.value = [];
  } finally {
    loading.value = false;
  }
}

function openCreate() {
  editing.value = null;
  Object.assign(form, { name: '', phone: '', role: 'staff', status: 'active', password: '' });
  dialogVisible.value = true;
}

function openEdit(row) {
  editing.value = row;
  Object.assign(form, {
    name: row.name || '',
    phone: row.phone || '',
    role: row.role || 'staff',
    status: row.status || 'active',
    password: ''
  });
  dialogVisible.value = true;
}

async function submit() {
  await formRef.value.validate();
  submitting.value = true;
  try {
    if (editing.value) {
      await employeesApi.update(editing.value.id, {
        name: form.name,
        phone: form.phone,
        role: form.role,
        status: form.status
      });
      ElMessage.success('员工信息已更新');
    } else {
      await employeesApi.create({ ...form });
      ElMessage.success('员工已创建');
    }
    dialogVisible.value = false;
    loadEmployees();
  } finally {
    submitting.value = false;
  }
}

async function toggleStatus(row, val) {
  const status = val ? 'active' : 'inactive';
  await employeesApi.patch(row.id, { status });
  row.status = status;
  ElMessage.success(`已${val ? '启用' : '停用'}「${row.name}」`);
}

async function resetPassword(row) {
  const { value } = await ElMessageBox.prompt(`为「${row.name}」设置新密码`, '重置密码', {
    inputType: 'password',
    inputPattern: /.{6,}/,
    inputErrorMessage: '密码至少 6 位'
  });
  await employeesApi.resetPassword(row.id, value);
  ElMessage.success('密码已重置');
}

async function removeEmployee(row) {
  await ElMessageBox.confirm(`确认删除员工「${row.name}」？`, '提示', { type: 'warning' });
  await employeesApi.remove(row.id);
  ElMessage.success('员工已删除');
  loadEmployees();
}

onMounted(loadEmployees);
</script>