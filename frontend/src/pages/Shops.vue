<template>
  <section>
    <PageHeader
      eyebrow="SHOPS"
      title="门店管理"
      description="维护门店基本信息、营业状态与每日营业时段。"
    >
      <template #actions>
        <el-button :icon="Refresh" round @click="loadAll">刷新</el-button>
        <el-button type="primary" :icon="Check" round :loading="saving" @click="saveAll">保存设置</el-button>
      </template>
    </PageHeader>

    <div class="split-grid">
      <el-card shadow="never">
        <template #header>
          <div class="flex-between">
            <strong>门店基础信息</strong>
            <el-tag :type="info.paused ? 'danger' : 'success'" effect="dark" round>
              {{ info.paused ? '暂停营业' : '正常营业' }}
            </el-tag>
          </div>
        </template>
        <el-form label-position="top" :model="info" class="dialog-form">
          <div class="form-grid">
            <el-form-item label="门店名称">
              <el-input v-model="info.name" placeholder="如：WashHelper 中关村店" />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="info.phone" placeholder="客服 / 门店电话" />
            </el-form-item>
            <el-form-item label="门店地址" class="full-span">
              <el-input v-model="info.address" placeholder="详细地址" />
            </el-form-item>
            <el-form-item label="纬度 Latitude">
              <el-input v-model.number="info.latitude" placeholder="如 39.9042" />
            </el-form-item>
            <el-form-item label="经度 Longitude">
              <el-input v-model.number="info.longitude" placeholder="如 116.4074" />
            </el-form-item>
            <el-form-item label="Logo URL" class="full-span">
              <el-input v-model="info.logoUrl" placeholder="https://..." />
            </el-form-item>
            <el-form-item label="暂停营业">
              <el-switch v-model="info.paused" />
            </el-form-item>
            <el-form-item label="暂停原因">
              <el-input v-model="info.pauseReason" placeholder="如：设备保养" :disabled="!info.paused" />
            </el-form-item>
          </div>
        </el-form>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <strong>营业时间</strong>
        </template>
        <div class="hour-list">
          <div v-for="day in hours" :key="day.weekday" class="hour-row">
            <span class="weekday">{{ weekdayLabel(day.weekday) }}</span>
            <el-switch v-model="day.open" />
            <el-time-picker
              v-model="day.openTime"
              format="HH:mm"
              value-format="HH:mm:ss"
              :disabled="!day.open"
              placeholder="开门"
            />
            <span class="dash">—</span>
            <el-time-picker
              v-model="day.closeTime"
              format="HH:mm"
              value-format="HH:mm:ss"
              :disabled="!day.open"
              placeholder="关门"
            />
          </div>
        </div>
        <div class="soft" style="margin-top: 12px">
          周一至周日的营业时段会同步小程序前端展示。
        </div>
      </el-card>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { Check, Refresh } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import PageHeader from '@/components/PageHeader.vue';
import { shopApi } from '@/api';

const info = reactive({
  name: '',
  phone: '',
  address: '',
  logoUrl: '',
  latitude: null,
  longitude: null,
  paused: false,
  pauseReason: ''
});

const hours = ref(buildDefaultHours());
const saving = ref(false);

function buildDefaultHours() {
  return Array.from({ length: 7 }, (_, idx) => ({
    weekday: idx,
    openTime: '09:00:00',
    closeTime: '21:00:00',
    open: true
  }));
}

function weekdayLabel(weekday) {
  return ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'][weekday] || `Day ${weekday}`;
}

async function loadAll() {
  try {
    const remoteInfo = await shopApi.getInfo();
    Object.assign(info, remoteInfo || {});
  } catch (error) {
    /* allow blank */
  }
  try {
    const remoteHours = await shopApi.getHours();
    const items = Array.isArray(remoteHours) ? remoteHours : remoteHours?.items || [];
    if (items.length) {
      const map = new Map(items.map((row) => [row.weekday, row]));
      hours.value = buildDefaultHours().map((day) => {
        const found = map.get(day.weekday);
        return found ? { ...day, ...found, open: found.open ?? found.isOpen ?? true } : day;
      });
    }
  } catch (error) {
    /* allow defaults */
  }
}

async function saveAll() {
  saving.value = true;
  try {
    await shopApi.updateInfo({ ...info });
    await shopApi.updateHours({ items: hours.value });
    ElMessage.success('门店设置已保存');
  } finally {
    saving.value = false;
  }
}

onMounted(loadAll);
</script>

<style scoped>
.hour-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.hour-row {
  display: grid;
  grid-template-columns: 64px 60px 1fr 16px 1fr;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid var(--admin-border);
}

.weekday {
  font-weight: 600;
}

.dash {
  text-align: center;
  color: var(--admin-muted);
}
</style>