export function formatMoney(value, fallback = '¥0.00') {
  const num = Number(value);
  if (!Number.isFinite(num)) return fallback;
  return `¥${num.toFixed(2)}`;
}

export function formatNumber(value, fallback = '-') {
  const num = Number(value);
  if (!Number.isFinite(num)) return fallback;
  return num.toLocaleString('zh-CN');
}

export function formatDateTime(value, fallback = '-') {
  if (!value) return fallback;
  const date = value instanceof Date ? value : new Date(value);
  if (Number.isNaN(date.getTime())) return fallback;
  const pad = (n) => String(n).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

export function formatDate(value, fallback = '-') {
  if (!value) return fallback;
  const date = value instanceof Date ? value : new Date(value);
  if (Number.isNaN(date.getTime())) return fallback;
  const pad = (n) => String(n).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
}

export function pickFirst(target, keys, fallback = '') {
  if (!target) return fallback;
  for (const key of keys) {
    const value = target[key];
    if (value !== undefined && value !== null && value !== '') {
      return value;
    }
  }
  return fallback;
}