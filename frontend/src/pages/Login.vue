<template>
  <div class="login-shell">
    <div class="login-card">
      <div class="login-brand">
        <div class="login-mark">W</div>
        <div>
          <h1>WashHelper 后台</h1>
          <p>选择您要登录的门店并使用店长账号进入</p>
        </div>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        size="large"
        @submit.prevent="onSubmit"
      >
        <el-form-item label="选择门店" prop="shopId">
          <el-select
            v-model="form.shopId"
            placeholder="请选择登录门店"
            :loading="shopsLoading"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="shop in shops"
              :key="shop.id"
              :value="shop.id"
              :label="shop.name"
            >
              <div class="shop-option">
                <span class="shop-name">{{ shop.name }}</span>
                <span class="shop-meta">ID {{ shop.id }} · {{ shop.phone || '无电话' }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="店长用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="店长姓名或手机号"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item label="登录密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item label="图形验证码" prop="captcha">
          <div class="captcha-row">
            <el-input
              v-model="form.captcha"
              placeholder="请输入图中字符"
              :prefix-icon="Key"
              maxlength="6"
              clearable
              @keyup.enter="onSubmit"
            />
            <div class="captcha-img" :class="{ loading: captchaLoading }" @click="loadCaptcha" title="点击刷新验证码">
              <img v-if="captcha.image" :src="captcha.image" alt="验证码" />
              <span v-else class="captcha-placeholder">加载中…</span>
            </div>
          </div>
          <div class="captcha-hint">看不清？点击图片刷新</div>
        </el-form-item>

        <el-alert
          v-if="lockedMsg"
          :title="lockedMsg"
          type="error"
          show-icon
          :closable="false"
          style="margin-bottom: 12px"
        />

        <el-button
          type="primary"
          class="login-submit"
          :loading="loading"
          :disabled="!!locked"
          round
          size="large"
          @click="onSubmit"
        >
          {{ locked ? '账号已临时锁定' : '登 录' }}
        </el-button>
      </el-form>

      <div class="login-tip">
        <span>初始密码：wash123</span>
        <span>连续 5 次输入错误将临时锁定 15 分钟</span>
      </div>
    </div>

    <footer class="login-foot">
      <span>WashHelper · 洗衣店后台管理系统</span>
      <span>{{ year }} © 演示环境</span>
    </footer>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Lock, Key } from '@element-plus/icons-vue';
import { authApi } from '@/api';
import { useAppStore, useUserStore } from '@/store';

const router = useRouter();
const route = useRoute();
const appStore = useAppStore();
const userStore = useUserStore();

const formRef = ref(null);
const shops = ref([]);
const shopsLoading = ref(false);
const loading = ref(false);
const captchaLoading = ref(false);
const locked = ref(false);
const lockedMsg = ref('');
const year = new Date().getFullYear();

const captcha = reactive({
  token: '',
  image: ''
});

const form = reactive({
  shopId: null,
  username: '',
  password: '',
  captcha: ''
});

const rules = {
  shopId: [{ required: true, message: '请选择门店', trigger: 'change' }],
  username: [{ required: true, message: '请输入店长用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captcha: [{ required: true, message: '请输入图形验证码', trigger: 'blur' }]
};

async function loadShops() {
  shopsLoading.value = true;
  try {
    const data = await authApi.shops();
    shops.value = Array.isArray(data) ? data : data?.items || [];
    if (!form.shopId && shops.value.length) {
      form.shopId = shops.value[0].id;
    }
  } catch (e) {
    ElMessage.error('门店列表加载失败');
  } finally {
    shopsLoading.value = false;
  }
}

async function loadCaptcha() {
  if (captchaLoading.value) return;
  captchaLoading.value = true;
  form.captcha = '';
  try {
    const data = await authApi.captcha();
    captcha.token = data?.token || '';
    captcha.image = data?.image || '';
  } catch (e) {
    captcha.token = '';
    captcha.image = '';
  } finally {
    captchaLoading.value = false;
  }
}

function applyCaptchaFromResponse(payload) {
  const next = payload?.data?.captcha;
  if (next?.token && next?.image) {
    captcha.token = next.token;
    captcha.image = next.image;
    form.captcha = '';
  } else {
    loadCaptcha();
  }
}

async function onSubmit() {
  if (locked.value) return;
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
  } catch (e) {
    return;
  }
  loading.value = true;
  try {
    const result = await authApi.login({
      shopId: form.shopId,
      username: form.username.trim(),
      password: form.password,
      captchaToken: captcha.token,
      captcha: form.captcha.trim()
    });

    if (result?.ok === false) {
      const message = result.message || '登录失败';
      ElMessage.error(message);
      applyCaptchaFromResponse(result);
      if (/锁定/.test(message)) {
        locked.value = true;
        lockedMsg.value = message;
        setTimeout(() => {
          locked.value = false;
          lockedMsg.value = '';
        }, 60_000);
      }
      return;
    }

    const data = result?.data ?? result ?? {};
    if (data.apiKey) appStore.setApiKey(data.apiKey);
    appStore.setShopId(data.shopId ?? form.shopId);
    appStore.setShopName(data.shopName || '');
    userStore.setUser({
      id: data?.employee?.id,
      name: data?.employee?.name,
      role: data?.employee?.role,
      phone: data?.employee?.phone,
      shopId: data?.shopId ?? form.shopId,
      shopName: data?.shopName
    });
    ElMessage.success('登录成功');
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/dashboard';
    router.replace(redirect);
  } catch (e) {
    loadCaptcha();
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  loadShops();
  loadCaptcha();
});
</script>

<style scoped>
.login-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 16px;
  background:
    radial-gradient(circle at 12% 18%, rgba(255, 143, 66, 0.18), transparent 32%),
    radial-gradient(circle at 88% 8%, rgba(91, 132, 255, 0.14), transparent 26%),
    radial-gradient(circle at 50% 92%, rgba(85, 211, 154, 0.10), transparent 32%),
    linear-gradient(180deg, #f4f6fb 0%, #eef2f9 60%, #ffffff 100%);
}

.login-card {
  width: 100%;
  max-width: 440px;
  padding: 36px 32px 28px;
  background: #ffffff;
  border-radius: 22px;
  border: 1px solid rgba(15, 23, 42, 0.06);
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.08);
}

.login-brand {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 24px;
}

.login-mark {
  display: grid;
  place-items: center;
  width: 56px;
  height: 56px;
  border-radius: 18px;
  background: linear-gradient(135deg, #ff9b4d 0%, #f4c25f 100%);
  color: #ffffff;
  font-weight: 800;
  font-size: 24px;
  box-shadow: 0 14px 30px rgba(255, 143, 66, 0.30);
}

.login-brand h1 {
  margin: 0 0 4px;
  font-size: 22px;
  color: #1f2937;
}

.login-brand p {
  margin: 0;
  font-size: 13px;
  color: #6b7280;
}

.login-submit {
  width: 100%;
  margin-top: 4px;
  height: 44px;
  font-size: 16px;
  letter-spacing: 0.04em;
}

.login-tip {
  margin-top: 18px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
  color: #94a3b8;
  text-align: center;
}

.shop-option {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}

.shop-name {
  font-weight: 600;
  color: #1f2937;
}

.shop-meta {
  font-size: 12px;
  color: #94a3b8;
}

.login-foot {
  margin-top: 24px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
  font-size: 12px;
  color: #94a3b8;
}

.captcha-row {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.captcha-row :deep(.el-input) {
  flex: 1;
}

.captcha-img {
  flex: 0 0 130px;
  width: 130px;
  height: 44px;
  display: grid;
  place-items: center;
  border-radius: 10px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  background: #f8fafc;
  cursor: pointer;
  overflow: hidden;
  transition: opacity 0.2s ease;
  user-select: none;
}

.captcha-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.captcha-img.loading {
  opacity: 0.5;
}

.captcha-placeholder {
  font-size: 12px;
  color: #94a3b8;
}

.captcha-hint {
  margin-top: 6px;
  font-size: 12px;
  color: #94a3b8;
}
</style>