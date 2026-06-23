<template>
  <section>
    <PageHeader
      eyebrow="AI ASSISTANT"
      title="AI 智能助手"
      description="基于门店运营数据生成洞察、撰写营销话术与解答业务问题。"
    >
      <template #actions>
        <el-button :icon="Refresh" round @click="ping">检测服务</el-button>
      </template>
    </PageHeader>

    <div class="ai-layout">
      <el-card shadow="never" class="ai-chat">
        <template #header>
          <div class="flex-between">
            <strong>对话助手</strong>
            <el-tag :type="serviceOk ? 'success' : 'info'" effect="dark" round>
              {{ serviceOk ? '服务在线' : '本地模拟' }}
            </el-tag>
          </div>
        </template>

        <div ref="threadEl" class="thread">
          <div v-for="(msg, idx) in messages" :key="idx" :class="['bubble', msg.role]">
            <el-avatar :size="32" :class="msg.role">
              <component :is="msg.role === 'assistant' ? Cpu : User" />
            </el-avatar>
            <div class="bubble-body">
              <span class="bubble-name">{{ msg.role === 'assistant' ? 'AI 助手' : '我' }}</span>
              <p>{{ msg.content }}</p>
            </div>
          </div>
          <div v-if="thinking" class="bubble assistant">
            <el-avatar :size="32" class="assistant"><component :is="Cpu" /></el-avatar>
            <div class="bubble-body">
              <span class="bubble-name">AI 助手</span>
              <p class="soft">正在思考中…</p>
            </div>
          </div>
        </div>

        <div class="composer">
          <el-input
            v-model="draft"
            type="textarea"
            :rows="2"
            placeholder="问点什么吧，例如：今天该重点推哪些会员套餐？"
            resize="none"
            @keydown.enter.exact.prevent="sendMessage"
          />
          <el-button type="primary" :icon="Promotion" :loading="thinking" @click="sendMessage">发送</el-button>
        </div>
      </el-card>

      <div class="stack">
        <el-card shadow="never">
          <template #header>
            <strong>灵感清单</strong>
          </template>
          <div class="stack">
            <button v-for="(item, idx) in prompts" :key="idx" class="prompt-cell" @click="pickPrompt(item)">
              <span>{{ item.title }}</span>
              <small>{{ item.desc }}</small>
            </button>
          </div>
        </el-card>

        <el-card shadow="never">
          <template #header>
            <strong>能力说明</strong>
          </template>
          <ul class="capability">
            <li>📈 解读经营数据，定位营业趋势异常。</li>
            <li>💬 生成会员营销话术、节日活动文案。</li>
            <li>🧴 提供洗护工艺建议与禁忌提醒。</li>
            <li>🛠 配合 API：<code>/api/ai</code></li>
          </ul>
        </el-card>
      </div>
    </div>
  </section>
</template>

<script setup>
import { nextTick, onMounted, ref } from 'vue';
import { Cpu, Promotion, Refresh, User } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import PageHeader from '@/components/PageHeader.vue';
import { aiApi } from '@/api';

const messages = ref([
  { role: 'assistant', content: '你好，我是 WashHelper AI 助手。我可以帮你解读经营数据、撰写营销话术。' }
]);

const prompts = [
  { title: '复盘今日营业', desc: '根据订单和会员消费给出 3 条洞察' },
  { title: '撰写会员充值文案', desc: '面向金卡会员的节日营销短信' },
  { title: '羽绒服洗护说明', desc: '生成可直接打印在小票背面的提示' },
  { title: '低库存补货建议', desc: '基于当前预警物料给出补货优先级' }
];

const draft = ref('');
const thinking = ref(false);
const serviceOk = ref(false);
const threadEl = ref();

function scrollToBottom() {
  nextTick(() => {
    if (threadEl.value) threadEl.value.scrollTop = threadEl.value.scrollHeight;
  });
}

async function ping() {
  try {
    await aiApi.chat();
    serviceOk.value = true;
    ElMessage.success('AI 服务在线');
  } catch (error) {
    serviceOk.value = false;
  }
}

function pickPrompt(item) {
  draft.value = item.title + '：' + item.desc;
}

function mockReply(question) {
  if (question.includes('库存') || question.includes('补货')) {
    return '建议优先补货：洗涤剂 / 包装袋 / 标签纸；这三项当前可用量低于预警阈值，预计 2 天内售罄。';
  }
  if (question.includes('会员') || question.includes('营销')) {
    return '可推送如下话术：「尊敬的金卡会员，端午专属优惠：充 500 送 80，仅限 6 月 24 - 26 日，回复 1 立即办理。」';
  }
  if (question.includes('羽绒') || question.includes('洗护')) {
    return '羽绒服建议：①请勿暴晒，避免布面氧化；②单独干洗，不与深色衣物混洗；③取衣后请尽快悬挂通风，恢复蓬松度。';
  }
  return '已收到你的问题，建议结合最近 7 天订单数据与会员复购行为进行交叉分析，给出明确动作清单。';
}

async function sendMessage() {
  const text = draft.value.trim();
  if (!text) return;
  messages.value.push({ role: 'user', content: text });
  draft.value = '';
  thinking.value = true;
  scrollToBottom();
  try {
    let reply;
    try {
      const res = await aiApi.chat();
      serviceOk.value = true;
      reply = res?.reply || res?.message || mockReply(text);
    } catch (error) {
      serviceOk.value = false;
      reply = mockReply(text);
    }
    await new Promise((resolve) => setTimeout(resolve, 350));
    messages.value.push({ role: 'assistant', content: reply });
    scrollToBottom();
  } finally {
    thinking.value = false;
  }
}

onMounted(ping);
</script>

<style scoped>
.ai-layout {
  display: grid;
  grid-template-columns: 1.4fr 0.6fr;
  gap: 16px;
  margin-top: 16px;
}

.ai-chat {
  display: flex;
  flex-direction: column;
  min-height: 540px;
}

.thread {
  height: 420px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 4px 4px 12px;
}

.bubble {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.bubble.user {
  flex-direction: row-reverse;
  text-align: right;
}

.bubble-body {
  max-width: 70%;
  padding: 10px 14px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.05);
}

.bubble.user .bubble-body {
  background: linear-gradient(135deg, rgba(255, 143, 66, 0.22), rgba(255, 196, 112, 0.1));
}

.bubble-name {
  display: block;
  font-size: 12px;
  color: var(--admin-muted);
  margin-bottom: 4px;
}

.bubble-body p {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
}

.el-avatar.assistant {
  background: rgba(91, 132, 255, 0.25);
  color: #92b5ff;
}

.el-avatar.user {
  background: rgba(255, 143, 66, 0.25);
  color: var(--admin-accent);
}

.composer {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  margin-top: 12px;
}

.composer .el-textarea {
  flex: 1;
}

.prompt-cell {
  text-align: left;
  padding: 12px 14px;
  border-radius: 12px;
  border: 1px solid var(--admin-border);
  background: rgba(255, 255, 255, 0.04);
  color: var(--admin-text);
  cursor: pointer;
  transition: all 0.2s ease;
}

.prompt-cell:hover {
  border-color: var(--admin-accent);
  color: var(--admin-accent);
}

.prompt-cell span {
  display: block;
  font-weight: 600;
}

.prompt-cell small {
  color: var(--admin-muted);
}

.capability {
  margin: 0;
  padding-left: 18px;
  color: var(--admin-text);
  line-height: 1.9;
}

.capability code {
  padding: 2px 6px;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.06);
  font-size: 12px;
}

@media (max-width: 1280px) {
  .ai-layout {
    grid-template-columns: 1fr;
  }
}
</style>