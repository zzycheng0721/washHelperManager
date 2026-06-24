<template>
  <section>
    <PageHeader
      eyebrow="ACCOUNT"
      title="修改登录密码"
      description="为当前登录的店长账号更新密码，更新后需重新登录。"
    />

    <div class="split-grid">
      <el-card shadow="never">
        <template #header>
          <div class="flex-between">
            <strong>账号信息</strong>
            <el-tag effect="plain" round>{{ userStore.role || '管理员' }}</el-tag>
          </div>
        </template>
        <ul class="info-list">
          <li>
            <span class="info-label">登录门店</span>
            <span class="info-value">{{ appStore.shopName || `门店 ${appStore.currentShopId}` }}</span>
          </li>
          <li>
            <span class="info-label">店长姓名</span>
            <span class="info-value">{{ userStore.name || '—' }}</span>
          </li>
          <li>
            <span class="info-label">手机号</span>
            <span class="info-value">{{ userStore.phone || '—' }}</span>
          </li>
          <li>
            <span class="info-label">员工 ID</span>
            <span class="info-value">{{ userStore.id || '—' }}</span>
          </li>
        </ul>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <strong>修改密码</strong>
        </template>
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          class="dialog-form"
        >
          <el-form-item label="原密码" prop="oldPassword">
            <el-input
              v-model="form.oldPassword"
              type="password"
              placeholder="请输入当前密码"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="form.newPassword"
              type="password"
              placeholder="至少 6 位"
              :prefix-icon="Key"
              show-password
            />
          </el-form-item>
          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              :prefix-icon="Key"
              show-password
              @keyup.enter="onSubmit"
            />
          </el-form-item>
          <el-button
            type="primary"
            round
            size="large"
            :loading="loading"
            style="width: 100%"
            @click="onSubmit"
          >
            提交修改
          </el-button>
        </el-form>
      </el-card>
    </div>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Lock, Key } from '@element-plus/icons-vue';
import PageHeader from '@/components/PageHeader.vue';
import { authApi } from '@/api';
import { useAppStore, useUserStore } from '@/store';

const router = useRouter();
const appStore = useAppStore();
const userStore = useUserStore();

const formRef = ref(null);
const loading = ref(false);

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const rules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码至少 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value !== form.newPassword) {
          callback(new Error('两次输入的新密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

async function onSubmit() {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
  } catch (e) {
    return;
  }
  if (!userStore.id) {
    ElMessage.error('未识别到登录账号，请重新登录');
    return;
  }
  loading.value = true;
  try {
    await authApi.changePassword({
      shopId: appStore.currentShopId,
      employeeId: userStore.id,
      oldPassword: form.oldPassword,
      newPassword: form.newPassword
    });
    await ElMessageBox.alert('密码修改成功，请重新登录。', '提示', { confirmButtonText: '去登录' });
    userStore.logout();
    router.replace('/login');
  } catch (e) {
    // request 拦截器已经弹错误
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.info-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-list li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 14px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid rgba(15, 23, 42, 0.06);
}

.info-label {
  color: #6b7280;
  font-size: 13px;
}

.info-value {
  color: #1f2937;
  font-weight: 600;
}
</style>
