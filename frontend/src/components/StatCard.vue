<template>
  <article class="stat-card" :class="toneClass">
    <div class="stat-card-top">
      <div class="stat-icon">
        <component :is="icon" v-if="icon" />
        <slot v-else name="icon" />
      </div>
      <span class="stat-hint">{{ hint }}</span>
    </div>
    <strong class="stat-value">{{ value }}</strong>
    <div class="stat-footer">
      <span class="stat-title">{{ title }}</span>
      <span v-if="trend" class="stat-trend">{{ trend }}</span>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  value: {
    type: [String, Number],
    required: true
  },
  hint: {
    type: String,
    default: ''
  },
  trend: {
    type: String,
    default: ''
  },
  tone: {
    type: String,
    default: 'primary'
  },
  icon: {
    type: [Object, Function],
    default: null
  }
});

const toneClass = computed(() => `tone-${props.tone}`);
</script>
