<template>
  <section>
    <PageHeader
      eyebrow="WALLET"
      title="会员钱包"
      description="为会员发起充值、记录消费流水，掌握资金流转明细。"
    >
      <template #actions>
        <el-button :icon="Refresh" round @click="loadAll">刷新</el-button>
        <el-button type="primary" :icon="Plus" round @click="openRecharge">会员充值</el-button>
      </template>
    </PageHeader>

    <div class="stat-grid">
      <StatCard title="充值笔数" :value="rechargeCount" hint="最近 30 天" :icon="CreditCard" />
      <StatCard title="充值金额" :value="formatMoney(rechargeAmount)" hint="包含赠送" tone="warning" :icon="Coin" />
      <StatCard title="消费笔数" :value="consumeCount" hint="会员钱包扣款" :icon="ShoppingCart" />
      <StatCard title="消费金额" :value="formatMoney(consumeAmount)" hint="会员实付" :icon="Money" />
    </div>

    <div class="split-grid" style="margin-top: 16px">
      <el-card shadow="never">
        <template #header>
          <div class="flex-between">
            <strong>充值套餐</strong>
            <span class="soft">来自服务端配置</span>
          </div>
        </template>
        <el-empty v-if="!packages.length" description="暂无充值套餐" :image-size="80" />
        <div v-else class="package-grid">
          <article v-for="pkg in packages" :key="pkg.id || pkg.amount" class="package-card">
            <strong>{{ formatMoney(pkg.amount) }}</strong>
            <span>赠送 {{ formatMoney(pkg.bonus || 0) }}</span>
            <p class="soft">{{ pkg.notes || pkg.label || '优惠套餐' }}</p>
            <el-button text type="primary" @click="quickPickPackage(pkg)">选择此套餐</el-button>
          </article>
        </div>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <strong>交易记录</strong>
        </template>
        <el-table :data="transactions" v-loading="loading" stripe height="380">
          <el-table-column label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="row.type === 'recharge' ? 'success' : 'warning'" effect="dark" round>
                {{ row.type === 'recharge' ? '充值' : row.type === 'consume' ? '消费' : row.type }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="金额" width="120">
            <template #default="{ row }">
              <strong :style="{ color: row.type === 'recharge' ? '#55d39a' : 'var(--admin-accent)' }">
                {{ row.type === 'recharge' ? '+' : '-' }}{{ formatMoney(row.amount) }}
              </strong>
            </template>
          </el-table-column>
          <el-table-column prop="paymentMethod" label="渠道" width="100" />
          <el-table-column label="余额" width="110">
            <template #default="{ row }">{{ formatMoney(row.balanceAfter) }}</template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
          <el-table-column label="时间" min-width="160">
            <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <el-dialog v-model="dialogVisible" title="会员充值" width="540px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="dialog-form">
        <div class="form-grid">
          <el-form-item label="会员 ID / 编号" prop="customerId" class="full-span">
            <el-input v-model="form.customerId" placeholder="输入客户编号或主键" />
          </el-form-item>
          <el-form-item label="充值金额（元）" prop="amount">
            <el-input-number v-model="form.amount" :min="0" :precision="2" :step="50" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="赠送金额">
            <el-input-number v-model="form.bonus" :min="0" :precision="2" :step="10" controls-position="right" style="width: 100%" />
          </el-form-item>
          <el-form-item label="支付方式">
            <el-select v-model="form.paymentMethod" style="width: 100%">
              <el-option label="现金" value="cash" />
              <el-option label="微信" value="wechat" />
              <el-option label="支付宝" value="alipay" />
              <el-option label="银行卡" value="card" />
            </el-select>
          </el-form-item>
          <el-form-item label="实到金额">
            <el-input :model-value="formatMoney(Number(form.amount || 0) + Number(form.bonus || 0))" disabled />
          </el-form-item>
          <el-form-item label="备注" class="full-span">
            <el-input v-model="form.remark" placeholder="如：节日活动充值" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitRecharge">确认充值</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { Coin, CreditCard, Money, Plus, Refresh, ShoppingCart } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import PageHeader from '@/components/PageHeader.vue';
import StatCard from '@/components/StatCard.vue';
import { walletApi } from '@/api';
import { formatDateTime, formatMoney } from '@/utils/format';

const packages = ref([]);
const transactions = ref([]);
const loading = ref(false);

const dialogVisible = ref(false);
const submitting = ref(false);
const formRef = ref();
const form = reactive({
  customerId: '',
  amount: 100,
  bonus: 0,
  paymentMethod: 'wechat',
  remark: ''
});

const rules = {
  customerId: [{ required: true, message: '请输入会员 ID', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入充值金额', trigger: 'change' }]
};

const rechargeCount = computed(() => transactions.value.filter((t) => t.type === 'recharge').length);
const rechargeAmount = computed(() => transactions.value.filter((t) => t.type === 'recharge').reduce((s, t) => s + Number(t.amount || 0), 0));
const consumeCount = computed(() => transactions.value.filter((t) => t.type === 'consume').length);
const consumeAmount = computed(() => transactions.value.filter((t) => t.type === 'consume').reduce((s, t) => s + Number(t.amount || 0), 0));

async function loadAll() {
  loading.value = true;
  try {
    const [pkgRes, txRes] = await Promise.allSettled([
      walletApi.getRechargePackages(),
      walletApi.listTransactions({ page: 1, pageSize: 100 })
    ]);
    if (pkgRes.status === 'fulfilled') {
      const data = pkgRes.value;
      packages.value = Array.isArray(data) ? data : data?.items || data?.packages || [];
    }
    if (txRes.status === 'fulfilled') {
      transactions.value = txRes.value.items || [];
    }
  } finally {
    loading.value = false;
  }
}

function openRecharge() {
  Object.assign(form, { customerId: '', amount: 100, bonus: 0, paymentMethod: 'wechat', remark: '' });
  dialogVisible.value = true;
}

function quickPickPackage(pkg) {
  form.amount = Number(pkg.amount || 0);
  form.bonus = Number(pkg.bonus || 0);
  dialogVisible.value = true;
}

async function submitRecharge() {
  await formRef.value.validate();
  submitting.value = true;
  try {
    await walletApi.recharge(form.customerId, {
      amount: form.amount,
      bonus: form.bonus,
      paymentMethod: form.paymentMethod,
      remark: form.remark
    });
    ElMessage.success('充值成功');
    dialogVisible.value = false;
    loadAll();
  } finally {
    submitting.value = false;
  }
}

onMounted(loadAll);
</script>

<style scoped>
.package-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 12px;
}

.package-card {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
  padding: 14px;
  border-radius: 14px;
  background: rgba(255, 143, 66, 0.08);
  border: 1px solid rgba(255, 143, 66, 0.22);
}

.package-card strong {
  font-size: 22px;
  color: var(--admin-accent);
}

.package-card span {
  color: var(--admin-muted);
  font-size: 13px;
}
</style>