<template>
  <section>
    <PageHeader
      eyebrow="INVENTORY"
      title="库存管理"
      description="掌握洗护耗材库存量与预警阈值，确保门店连续运营。"
    >
      <template #actions>
        <el-button :icon="Refresh" round @click="loadInventory">刷新</el-button>
        <el-button type="primary" :icon="Plus" round @click="openCreate">新增物料</el-button>
      </template>
    </PageHeader>

    <div class="stat-grid">
      <StatCard title="物料种类" :value="total" hint="全店统计" :icon="Box" />
      <StatCard title="预警物料" :value="alertCount" hint="需补货" tone="warning" :icon="Warning" />
      <StatCard title="总剩余量" :value="totalAvailable" hint="所有单位累计" :icon="Files" />
      <StatCard title="平均库存率" :value="avgRate" hint="可用 / 总量" :icon="Histogram" />
    </div>

    <el-card shadow="never" class="page-card">
      <div class="filter-grid">
        <el-input v-model="filters.search" placeholder="搜索物料名称" clearable :prefix-icon="Search" @keyup.enter="loadInventory" />
        <el-select v-model="filters.category" placeholder="类别筛选" clearable>
          <el-option v-for="cat in categoryOptions" :key="cat" :label="cat" :value="cat" />
        </el-select>
        <el-select v-model="filters.alertOnly" placeholder="仅看预警" clearable>
          <el-option label="全部" :value="''" />
          <el-option label="仅预警" value="alert" />
        </el-select>
        <div style="display: flex; gap: 8px">
          <el-button type="primary" plain @click="loadInventory">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="page-card">
      <el-table :data="filteredList" v-loading="loading" stripe>
        <el-table-column prop="name" label="物料名称" min-width="160" fixed />
        <el-table-column prop="category" label="类别" width="120" />
        <el-table-column label="库存进度" min-width="220">
          <template #default="{ row }">
            <el-progress
              :percentage="progress(row)"
              :status="row.available <= row.alertThreshold ? 'exception' : 'success'"
              :stroke-width="10"
            />
            <p class="soft" style="margin: 4px 0 0">
              可用 {{ row.available }} / 总量 {{ row.total }} {{ row.unit }}
            </p>
          </template>
        </el-table-column>
        <el-table-column label="预警阈值" width="120" align="center">
          <template #default="{ row }">{{ row.alertThreshold }} {{ row.unit }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.available <= row.alertThreshold ? 'danger' : 'success'" effect="dark" round>
              {{ row.available <= row.alertThreshold ? '需补货' : '充足' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="更新时间" min-width="170">
          <template #default="{ row }">{{ formatDateTime(row.updatedAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button text @click="quickRestock(row)">快速入库</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <div class="empty-wrap">暂无库存数据</div>
        </template>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑物料' : '新增物料'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="dialog-form">
        <div class="form-grid">
          <el-form-item label="物料名称" prop="name">
            <el-input v-model="form.name" placeholder="如：高级洗涤剂" />
          </el-form-item>
          <el-form-item label="类别">
            <el-select v-model="form.category" placeholder="选择类别" allow-create filterable>
              <el-option v-for="cat in categoryOptions" :key="cat" :label="cat" :value="cat" />
            </el-select>
          </el-form-item>
          <el-form-item label="可用数量" prop="available">
            <el-input-number v-model="form.available" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="总量" prop="total">
            <el-input-number v-model="form.total" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="计量单位">
            <el-input v-model="form.unit" placeholder="瓶 / 件 / kg" />
          </el-form-item>
          <el-form-item label="预警阈值">
            <el-input-number v-model="form.alertThreshold" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="备注" class="full-span">
            <el-input v-model="form.notes" type="textarea" :rows="2" placeholder="补充信息" />
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
import { Box, Files, Histogram, Plus, Refresh, Search, Warning } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import PageHeader from '@/components/PageHeader.vue';
import StatCard from '@/components/StatCard.vue';
import { inventoryApi } from '@/api';
import { formatDateTime, formatNumber } from '@/utils/format';

const categoryOptions = ['洗涤耗材', '包装耗材', '配件', '设备'];

const filters = reactive({ search: '', category: '', alertOnly: '' });
const list = ref([]);
const loading = ref(false);
const total = ref(0);

const dialogVisible = ref(false);
const submitting = ref(false);
const editing = ref(null);
const formRef = ref();
const form = reactive({
  name: '',
  category: '洗涤耗材',
  available: 0,
  total: 0,
  unit: '瓶',
  alertThreshold: 0,
  notes: ''
});

const rules = {
  name: [{ required: true, message: '请填写物料名称', trigger: 'blur' }],
  available: [{ required: true, message: '请填写可用数量', trigger: 'change' }],
  total: [{ required: true, message: '请填写总量', trigger: 'change' }]
};

const filteredList = computed(() => {
  return list.value.filter((item) => {
    if (filters.search && !`${item.name}`.includes(filters.search)) return false;
    if (filters.category && item.category !== filters.category) return false;
    if (filters.alertOnly === 'alert' && Number(item.available) > Number(item.alertThreshold || 0)) return false;
    return true;
  });
});

const alertCount = computed(() => list.value.filter((i) => Number(i.available) <= Number(i.alertThreshold || 0)).length);
const totalAvailable = computed(() => formatNumber(list.value.reduce((s, i) => s + Number(i.available || 0), 0)));
const avgRate = computed(() => {
  if (!list.value.length) return '0%';
  const ratio = list.value.reduce((s, i) => s + (Number(i.total || 0) ? Number(i.available) / Number(i.total) : 0), 0) / list.value.length;
  return `${Math.round(ratio * 100)}%`;
});

function progress(row) {
  if (!row.total) return 0;
  return Math.min(100, Math.round((Number(row.available || 0) / Number(row.total)) * 100));
}

async function loadInventory() {
  loading.value = true;
  try {
    const result = await inventoryApi.list({ page: 1, pageSize: 200 });
    list.value = result.items || [];
    total.value = result.total || list.value.length;
  } catch (error) {
    list.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
}

function resetFilters() {
  filters.search = '';
  filters.category = '';
  filters.alertOnly = '';
}

function openCreate() {
  editing.value = null;
  Object.assign(form, { name: '', category: '洗涤耗材', available: 0, total: 0, unit: '瓶', alertThreshold: 0, notes: '' });
  dialogVisible.value = true;
}

function openEdit(row) {
  editing.value = row;
  Object.assign(form, {
    name: row.name || '',
    category: row.category || '洗涤耗材',
    available: row.available || 0,
    total: row.total || 0,
    unit: row.unit || '瓶',
    alertThreshold: row.alertThreshold || 0,
    notes: row.notes || ''
  });
  dialogVisible.value = true;
}

async function submit() {
  await formRef.value.validate();
  submitting.value = true;
  try {
    if (editing.value) {
      await inventoryApi.update(editing.value.id, form);
      ElMessage.success('物料已更新');
    } else {
      await inventoryApi.create(form);
      ElMessage.success('物料已新增');
    }
    dialogVisible.value = false;
    loadInventory();
  } finally {
    submitting.value = false;
  }
}

async function quickRestock(row) {
  const { value } = await ElMessageBox.prompt(`为「${row.name}」入库多少 ${row.unit}？`, '快速入库', {
    inputPattern: /^[1-9]\d*$/,
    inputErrorMessage: '请输入正整数'
  });
  const add = Number(value);
  await inventoryApi.update(row.id, {
    ...row,
    available: Number(row.available || 0) + add,
    total: Number(row.total || 0) + add
  });
  ElMessage.success(`已入库 ${add} ${row.unit}`);
  loadInventory();
}

onMounted(loadInventory);
</script>