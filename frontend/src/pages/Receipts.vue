<template>
  <section>
    <PageHeader
      eyebrow="RECEIPT"
      title="小票模板"
      description="配置门店小票内容、Logo 与打印纸宽，所有改动会同步给收银端打印。"
    >
      <template #actions>
        <el-button :icon="Refresh" round @click="loadTemplate">刷新</el-button>
        <el-button type="primary" :icon="Check" round :loading="saving" @click="save">保存模板</el-button>
      </template>
    </PageHeader>

    <div class="split-grid">
      <el-card shadow="never">
        <template #header>
          <strong>模板配置</strong>
        </template>
        <el-form label-position="top" :model="form" class="dialog-form">
          <div class="form-grid">
            <el-form-item label="头部标语" class="full-span">
              <el-input v-model="form.headerText" placeholder="如：欢迎光临 WashHelper 洗衣店" maxlength="50" show-word-limit />
            </el-form-item>
            <el-form-item label="尾部备注" class="full-span">
              <el-input v-model="form.footerText" type="textarea" :rows="2" placeholder="如：取衣请凭小票，谢谢光临！" />
            </el-form-item>
            <el-form-item label="门店 Logo 图片" class="full-span">
              <el-upload
                class="logo-uploader"
                :show-file-list="false"
                :before-upload="beforeLogoUpload"
                :http-request="handleLogoUpload"
                accept="image/png,image/jpeg,image/webp,image/svg+xml"
              >
                <div class="logo-uploader-trigger">
                  <img v-if="form.logoUrl" :src="resolveAsset(form.logoUrl)" class="logo-uploader-preview" alt="logo" />
                  <div v-else class="logo-uploader-empty">
                    <el-icon :size="26"><UploadFilled /></el-icon>
                    <span>点击上传 Logo</span>
                    <small>支持 PNG / JPG / WebP / SVG，建议 2MB 以内</small>
                  </div>
                  <div v-if="form.logoUrl" class="logo-uploader-mask">
                    <el-icon :size="22"><UploadFilled /></el-icon>
                    <span>更换图片</span>
                  </div>
                </div>
              </el-upload>
              <div v-if="form.logoUrl" class="logo-uploader-meta">
                <el-input v-model="form.logoUrl" size="small" readonly>
                  <template #append>
                    <el-button :icon="Delete" @click="clearLogo">移除</el-button>
                  </template>
                </el-input>
              </div>
            </el-form-item>
            <el-form-item label="纸张宽度">
              <el-select v-model="form.paperWidth" style="width: 100%">
                <el-option label="58mm（窄纸）" value="58mm" />
                <el-option label="80mm（宽纸）" value="80mm" />
              </el-select>
            </el-form-item>
            <el-form-item label="展示 Logo">
              <el-switch v-model="form.showLogo" />
            </el-form-item>
            <el-form-item label="展示客户姓名">
              <el-switch v-model="form.showCustomerName" />
            </el-form-item>
            <el-form-item label="展示洗护说明">
              <el-switch v-model="form.showWashInstructions" />
            </el-form-item>
          </div>
        </el-form>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <div class="flex-between">
            <strong>实时预览</strong>
            <el-tag round effect="dark" type="info">{{ form.paperWidth }}</el-tag>
          </div>
        </template>
        <div class="receipt-preview" :class="form.paperWidth === '80mm' ? 'wide' : 'narrow'">
          <div v-if="form.showLogo" class="receipt-logo">
            <img v-if="form.logoUrl" :src="resolveAsset(form.logoUrl)" alt="logo" />
            <div v-else class="logo-placeholder">LOGO</div>
          </div>
          <div class="receipt-title">{{ form.headerText || 'WashHelper 洗衣店' }}</div>
          <div class="receipt-divider" />
          <div class="receipt-row"><span>订单号</span><strong>W20250624001</strong></div>
          <div v-if="form.showCustomerName" class="receipt-row"><span>客户</span><strong>张小姐 / 138****8888</strong></div>
          <div class="receipt-row"><span>时间</span><strong>2025-06-24 16:32</strong></div>
          <div class="receipt-divider" />
          <div class="receipt-row item"><span>羽绒服干洗 ×1</span><strong>¥ 58.00</strong></div>
          <div class="receipt-row item"><span>真丝护理 ×2</span><strong>¥ 60.00</strong></div>
          <div class="receipt-row item"><span>会员折扣 (9折)</span><strong>-¥ 11.80</strong></div>
          <div class="receipt-divider" />
          <div class="receipt-row total"><span>合计</span><strong>¥ 106.20</strong></div>
          <div v-if="form.showWashInstructions" class="receipt-note">
            洗护提示：含羊绒材质，建议低温干洗，请勿暴晒。
          </div>
          <div v-if="form.footerText" class="receipt-footer">{{ form.footerText }}</div>
        </div>
      </el-card>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { Check, Delete, Refresh, UploadFilled } from '@element-plus/icons-vue';
import request from '@/api/request';
import { ElMessage } from 'element-plus';
import PageHeader from '@/components/PageHeader.vue';
import { receiptTemplatesApi } from '@/api';

const form = reactive({
  headerText: 'WashHelper 洗衣店',
  footerText: '谢谢光临，欢迎再次惠顾！',
  logoUrl: '',
  paperWidth: '58mm',
  showLogo: true,
  showCustomerName: true,
  showWashInstructions: false
});

const saving = ref(false);

async function loadTemplate() {
  try {
    const data = await receiptTemplatesApi.get();
    Object.assign(form, data || {});
  } catch (error) {
    /* allow defaults */
  }
}

async function save() {
  saving.value = true;
  try {
    await receiptTemplatesApi.update({ ...form });
    ElMessage.success('模板已保存');
  } finally {
    saving.value = false;
  }
}

function resolveAsset(url) {
  if (!url) return '';
  if (/^(https?:|data:|blob:)/i.test(url)) return url;
  return url.startsWith('/') ? url : '/' + url;
}

function beforeLogoUpload(file) {
  const ok = /image\/(png|jpeg|jpg|webp|svg\+xml)/.test(file.type);
  if (!ok) {
    ElMessage.error('只能上传 PNG / JPG / WebP / SVG 格式的图片');
    return false;
  }
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小请控制在 2MB 以内');
    return false;
  }
  return true;
}

async function handleLogoUpload(option) {
  const formData = new FormData();
  formData.append('file', option.file);
  try {
    const resp = await request.post('/shop/logo/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    const data = resp?.data ?? resp ?? {};
    const url = data.logoUrl || data.url || data.fileUrl || data.path;
    if (!url) {
      ElMessage.error('上传失败：返回数据缺少 URL');
      option.onError?.(new Error('missing url'));
      return;
    }
    form.logoUrl = url;
    ElMessage.success('Logo 上传成功');
    option.onSuccess?.(data);
  } catch (e) {
    option.onError?.(e);
  }
}

function clearLogo() {
  form.logoUrl = '';
}

onMounted(loadTemplate);
</script>

<style scoped>
.receipt-preview {
  margin: 0 auto;
  padding: 18px 20px;
  background: #fdfaf2;
  color: #1f2937;
  border-radius: 8px;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.45);
  font-family: 'Menlo', 'Consolas', monospace;
  font-size: 13px;
  line-height: 1.6;
}

.receipt-preview.narrow {
  width: 240px;
}

.receipt-preview.wide {
  width: 320px;
}

.receipt-logo {
  display: flex;
  justify-content: center;
  margin-bottom: 6px;
}

.receipt-logo img {
  max-height: 50px;
  max-width: 80%;
}

.logo-placeholder {
  width: 60px;
  height: 30px;
  display: grid;
  place-items: center;
  border: 1px dashed #94a3b8;
  border-radius: 6px;
  color: #94a3b8;
  font-size: 12px;
}

.receipt-title {
  text-align: center;
  font-weight: 700;
  font-size: 15px;
  margin-bottom: 6px;
}

.receipt-divider {
  border-top: 1px dashed #cbd5e1;
  margin: 8px 0;
}

.receipt-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.receipt-row.item {
  font-size: 12px;
}

.receipt-row.total {
  font-size: 15px;
  font-weight: 700;
}

.receipt-note {
  margin-top: 8px;
  font-size: 11px;
  color: #475569;
}

.receipt-footer {
  margin-top: 10px;
  text-align: center;
  font-size: 12px;
  color: #475569;
}

.logo-uploader {
  width: 100%;
  display: block;
}

.logo-uploader :deep(.el-upload) {
  width: 100%;
  display: block;
}

.logo-uploader-trigger {
  position: relative;
  width: 100%;
  min-height: 132px;
  border: 1.5px dashed rgba(15, 23, 42, 0.18);
  border-radius: 12px;
  background: #fbfbfd;
  cursor: pointer;
  transition: border-color 0.2s ease, background 0.2s ease;
  overflow: hidden;
  display: grid;
  place-items: center;
  padding: 12px;
}

.logo-uploader-trigger:hover {
  border-color: var(--admin-accent);
  background: #fff7f0;
}

.logo-uploader-preview {
  max-width: 100%;
  max-height: 110px;
  object-fit: contain;
  display: block;
}

.logo-uploader-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #94a3b8;
  text-align: center;
}

.logo-uploader-empty span {
  font-size: 14px;
  color: #4b5563;
  font-weight: 600;
}

.logo-uploader-empty small {
  font-size: 12px;
  color: #94a3b8;
}

.logo-uploader-mask {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 4px;
  background: rgba(15, 23, 42, 0.55);
  color: #ffffff;
  opacity: 0;
  transition: opacity 0.2s ease;
  font-size: 13px;
}

.logo-uploader-trigger:hover .logo-uploader-mask {
  opacity: 1;
}

.logo-uploader-meta {
  margin-top: 10px;
}
</style>