<template>
  <section>
    <PageHeader
      eyebrow="SERVICES"
      title="服务项目"
      description="维护洗护项目定价、预计完成时长与上下架状态，让收银开单更轻松。"
    >
      <template #actions>
        <el-button :icon="Refresh" round @click="loadServices">刷新</el-button>
        <el-button type="primary" :icon="Plus" round @click="openCreate">新增项目</el-button>
      </template>
    </PageHeader>

    <el-card shadow="never" class="page-card">
      <div class="filter-grid">
        <el-input v-model="filters.search" placeholder="搜索项目名称" clearable :prefix-icon="Search" />
        <el-select v-model="filters.category" placeholder="分类" clearable>
          <el-option v-for="cat in categoryOptions" :key="cat.value" :label="cat.label" :value="cat.value" />
        </el-select>
        <el-select v-model="filters.active" placeholder="上下架" clearable>
          <el-option label="全部" :value="''" />
          <el-option label="已上架" :value="true" />
          <el-option label="已下架" :value="false" />
        </el-select>
        <div></div>
      </div>
    </el-card>

    <div class="services-grid">
      <article
        v-for="item in filteredList"
        :key="item.id"
        class="service-card"
        :class="{ inactive: !item.active }"
      >
        <header>
          <el-tag :type="categoryTone(item.category)" effect="dark" round>{{ categoryLabel(item.category) }}</el-tag>
          <el-switch v-model="item.active" @change="(val) => toggleActive(item, val)" />
        </header>
        <h3>{{ item.name }}</h3>
        <p class="soft">{{ item.notes || '暂无说明' }}</p>
        <div class="service-meta">
          <div>
            <span class="soft">参考时长</span>
            <strong>{{ item.etaText || '—' }}</strong>
          </div>
          <div>
            <span class="soft">服务价格</span>
            <strong style="color: var(--admin-accent)">{{ formatMoney(item.price) }}</strong>
          </div>
        </div>
        <footer>
          <el-button text type="primary" @click="openEdit(item)">编辑</el-button>
          <el-button text type="danger" @click="removeService(item)">删除</el-button>
        </footer>
      </article>

      <div v-if="!loading && !filteredList.length" class="empty-wrap" style="grid-column: 1 / -1">
        暂无服务项目
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑服务项目' : '新增服务项目'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="dialog-form">
        <div class="form-grid">
          <el-form-item label="项目名称" prop="name">
            <el-input v-model="form.name" placeholder="如：羽绒服干洗" />
          </el-form-item>
          <el-form-item label="分类" prop="category">
            <el-select v-model="form.category" style="width: 100%">
              <el-option v-for="cat in categoryOptions" :key="cat.value" :label="cat.label" :value="cat.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="价格（元）" prop="price">
            <el-input-number v-model="form.price" :min="0" :precision="2" :step="1" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="参考时长">
            <el-input v-model="form.etaText" placeholder="如：1-2 天" />
          </el-form-item>
          <el-form-item label="是否上架">
            <el-switch v-model="form.active" />
          </el-form-item>
          <el-form-item label="备注 / 工艺说明" class="full-span">
            <el-input v-model="form.notes" type="textarea" :rows="2" placeholder="补充信息（适用面料、注意事项）" />
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
import { Plus, Refresh, Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import PageHeader from '@/components/PageHeader.vue';
import { serviceItemsApi } from '@/api';
import { formatMoney } from '@/utils/format';

const categoryOptions = [
  { value: 'dryClean', label: '干洗' },
  { value: 'wash', label: '水洗' },
  { value: 'care', label: '护理' },
  { value: 'shoes', label: '鞋包' },
  { value: 'other', label: '其他' }
];

const filters = reactive({ search: '', category: '', active: '' });
const list = ref([]);
const loading = ref(false);

const dialogVisible = ref(false);
const submitting = ref(false);
const editing = ref(null);
const formRef = ref();
const form = reactive({ name: '', category: 'dryClean', price: 0, etaText: '', active: true, notes: '' });

const rules = {
  name: [{ required: true, message: '请填写项目名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请填写价格', trigger: 'change' }]
};

const filteredList = computed(() => {
  return list.value.filter((item) => {
    if (filters.search && !`${item.name}`.includes(filters.search)) return false;
    if (filters.category && item.category !== filters.category) return false;
    if (filters.active !== '' && !!item.active !== !!filters.active) return false;
    return true;
  });
});

function categoryLabel(cat) {
  return categoryOptions.find((opt) => opt.value === cat)?.label || cat || '其他';
}

function categoryTone(cat) {
  return { dryClean: 'primary', wash: 'success', care: 'warning', shoes: 'info' }[cat] || '';
}

async function loadServices() {
  loading.value = true;
  try {
    const result = await serviceItemsApi.list({ page: 1, pageSize: 200 });
    list.value = result.items || [];
  } catch (error) {
    list.value = [];
  } finally {
    loading.value = false;
  }
}

function openCreate() {
  editing.value = null;
  Object.assign(form, { name: '', category: 'dryClean', price: 0, etaText: '', active: true, notes: '' });
  dialogVisible.value = true;
}

function openEdit(row) {
  editing.value = row;
  Object.assign(form, {
    name: row.name || '',
    category: row.category || 'dryClean',
    price: row.price || 0,
    etaText: row.etaText || '',
    active: row.active ?? true,
    notes: row.notes || ''
  });
  dialogVisible.value = true;
}

async function submit() {
  await formRef.value.validate();
  submitting.value = true;
  try {
    if (editing.value) {
      await serviceItemsApi.update(editing.value.id, form);
      ElMessage.success('项目已更新');
    } else {
      await serviceItemsApi.create(form);
      ElMessage.success('项目已新增');
    }
    dialogVisible.value = false;
    loadServices();
  } finally {
    submitting.value = false;
  }
}

async function toggleActive(row, val) {
  try {
    await serviceItemsApi.patch(row.id, { active: val });
    ElMessage.success(val ? '已上架' : '已下架');
  } catch (error) {
    row.active = !val;
  }
}

async function removeService(row) {
  await ElMessageBox.confirm(`确认删除项目「${row.name}」？`, '提示', { type: 'warning' });
  await serviceItemsApi.remove(row.id);
  ElMessage.success('项目已删除');
  loadServices();
}

onMounted(loadServices);
</script>

<style scoped>
.services-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.service-card {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 18px;
  border-radius: 18px;
  background: linear-gradient(180deg, rgba(18, 26, 46, 0.84), rgba(12, 18, 32, 0.92));
  border: 1px solid var(--admin-border);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.18);
  transition: transform 0.2s ease, border-color 0.2s ease;
}

.service-card:hover {
  transform: translateY(-2px);
  border-color: rgba(255, 143, 66, 0.4);
}

.service-card.inactive {
  opacity: 0.55;
}

.service-card header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.service-card h3 {
  margin: 4px 0 0;
  font-size: 18px;
}

.service-meta {
  display: flex;
  justify-content: space-between;
  padding: 12px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.04);
  margin-top: auto;
}

.service-card footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>