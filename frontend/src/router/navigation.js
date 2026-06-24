import {
  Box,
  Cpu,
  CreditCard,
  Document,
  Grid,
  List,
  OfficeBuilding,
  Odometer,
  User,
  UserFilled
} from '@element-plus/icons-vue';

export const navigationItems = [
  { path: '/dashboard', label: '仪表盘', hint: '经营总览', icon: Odometer },
  { path: '/orders', label: '订单管理', hint: '流转跟踪', icon: List },
  { path: '/customers', label: '客户管理', hint: '会员维护', icon: User },
  { path: '/inventory', label: '库存管理', hint: '耗材预警', icon: Box },
  { path: '/employees', label: '员工管理', hint: '权限与排班', icon: UserFilled },
  { path: '/services', label: '服务项目', hint: '项目定价', icon: Grid },
  { path: '/shops', label: '门店管理', hint: '门店与营业时间', icon: OfficeBuilding },
  { path: '/wallet', label: '会员钱包', hint: '充值与流水', icon: CreditCard },
  { path: '/receipts', label: '小票模板', hint: '打印样式', icon: Document },
  { path: '/ai', label: 'AI 助手', hint: '智能建议', icon: Cpu }
];

export function getNavigationByPath(path) {
  return navigationItems.find((item) => item.path === path);
}
