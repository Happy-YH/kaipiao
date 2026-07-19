<template>
  <div class="dashboard">
    <div class="page-header">
      <div class="ph-left">
        <h1 class="ph-title">工作台</h1>
        <p class="ph-desc">欢迎回来，{{ username }} · {{ currentDate }}</p>
      </div>
      <div class="ph-right">
        <div class="date-range">
          <button class="dr-btn" :class="{ active: dateRange === 'today' }" @click="dateRange = 'today'">今日</button>
          <button class="dr-btn" :class="{ active: dateRange === 'week' }" @click="dateRange = 'week'">本周</button>
          <button class="dr-btn" :class="{ active: dateRange === 'month' }" @click="dateRange = 'month'">本月</button>
          <button class="dr-btn" :class="{ active: dateRange === 'quarter' }" @click="dateRange = 'quarter'">本季度</button>
        </div>
      </div>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="sc-left">
          <div class="sc-icon blue">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><path d="m9 12 2 2 4-4"/></svg>
          </div>
        </div>
        <div class="sc-right">
          <div class="sc-value">¥ {{ formatNumber(stats.blueAmount) }}</div>
          <div class="sc-label">本月开票金额</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="sc-left">
          <div class="sc-icon green">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/><line x1="16" x2="8" y1="13" y2="13"/><line x1="16" x2="8" y1="17" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
          </div>
        </div>
        <div class="sc-right">
          <div class="sc-value">{{ stats.invoiceCount }}</div>
          <div class="sc-label">发票数量</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="sc-left">
          <div class="sc-icon red">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/></svg>
          </div>
        </div>
        <div class="sc-right">
          <div class="sc-value">¥ {{ formatNumber(stats.redAmount) }}</div>
          <div class="sc-label">红冲金额</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="sc-left">
          <div class="sc-icon orange">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>
          </div>
        </div>
        <div class="sc-right">
          <div class="sc-value">{{ stats.pendingReview }}</div>
          <div class="sc-label">待审核</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="sc-left">
          <div class="sc-icon purple">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
          </div>
        </div>
        <div class="sc-right">
          <div class="sc-value">{{ stats.failed }}</div>
          <div class="sc-label">开票失败</div>
        </div>
      </div>
    </div>

    <div class="content-grid">
      <div class="card chart-card">
        <div class="card-header">
          <div class="ch-left">
            <h3 class="ch-title">开票趋势</h3>
            <span class="ch-sub">近7天开票金额统计</span>
          </div>
          <div class="ch-right">
            <button class="ch-btn" :class="{ active: chartType === 'amount' }" @click="chartType = 'amount'">金额</button>
            <button class="ch-btn" :class="{ active: chartType === 'count' }" @click="chartType = 'count'">数量</button>
          </div>
        </div>
        <div class="chart-area">
          <div class="chart-bars">
            <div v-for="(item, index) in chartData" :key="index" class="bar-item">
              <div class="bar-wrapper">
                <div class="bar-fill" :style="{ height: item.percent + '%' }"></div>
              </div>
              <div class="bar-label">{{ item.label }}</div>
              <div class="bar-value">{{ chartType === 'amount' ? '¥' + formatNumber(item.value) : item.value + '张' }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="card todo-card">
        <div class="card-header">
          <div class="ch-left">
            <h3 class="ch-title">待办事项</h3>
            <span class="ch-sub">{{ todos.filter(t => !t.completed).length }} 项待处理</span>
          </div>
          <button class="ch-action">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" x2="12" y1="5" y2="19"/><line x1="5" x2="19" y1="12" y2="12"/></svg>
            新增
          </button>
        </div>
        <div class="todo-list">
          <div v-for="(todo, index) in todos" :key="index" class="todo-item" :class="{ completed: todo.completed }">
            <input type="checkbox" class="todo-checkbox" v-model="todo.completed">
            <div class="todo-content">
              <div class="todo-title">{{ todo.title }}</div>
              <div class="todo-meta">
                <span class="todo-tag" :class="todo.tag">{{ todo.tagText }}</span>
                <span class="todo-time">{{ todo.time }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="card recent-card">
        <div class="card-header">
          <div class="ch-left">
            <h3 class="ch-title">最近开票</h3>
            <span class="ch-sub">最近7条开票记录</span>
          </div>
          <button class="ch-action" @click="$router.push('/invoice-list')">
            查看全部
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
          </button>
        </div>
        <div class="recent-list">
          <div v-for="(item, index) in recentInvoices" :key="index" class="recent-item">
            <div class="ri-left">
              <div class="ri-icon" :class="item.status === '已开票' ? 'success' : 'pending'">
                <svg v-if="item.status === '已开票'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
                <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
              </div>
              <div class="ri-info">
                <div class="ri-number">{{ item.invoiceNo }}</div>
                <div class="ri-customer">{{ item.customerName }}</div>
              </div>
            </div>
            <div class="ri-right">
              <div class="ri-amount">¥ {{ formatNumber(item.amount) }}</div>
              <div class="ri-status" :class="item.status === '已开票' ? 'success' : 'pending'">{{ item.status }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="card customer-card">
        <div class="card-header">
          <div class="ch-left">
            <h3 class="ch-title">重点客户</h3>
            <span class="ch-sub">开票金额排名前5</span>
          </div>
        </div>
        <div class="customer-list">
          <div v-for="(item, index) in topCustomers" :key="index" class="customer-item">
            <div class="ci-rank" :class="getRankClass(index)">{{ index + 1 }}</div>
            <div class="ci-avatar">{{ item.name.charAt(0) }}</div>
            <div class="ci-info">
              <div class="ci-name">{{ item.name }}</div>
              <div class="ci-count">{{ item.invoiceCount }} 张发票</div>
            </div>
            <div class="ci-amount">¥ {{ formatNumber(item.amount) }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Dashboard',
  data() {
    return {
      username: localStorage.getItem('username') || '管理员',
      dateRange: 'month',
      chartType: 'amount',
      currentDate: '',
      stats: {
        blueAmount: 0,
        invoiceCount: 0,
        redAmount: 0,
        pendingReview: 0,
        failed: 0
      },
      chartData: [],
      todos: [
        { title: '审核红字确认单 #RC20260318001', tag: 'urgent', tagText: '紧急', time: '2小时前', completed: false },
        { title: '处理开票失败单据（3笔）', tag: 'warning', tagText: '待处理', time: '昨天', completed: false },
        { title: '确认月度开票统计报表', tag: 'normal', tagText: '普通', time: '2天前', completed: false },
        { title: '更新税率配置（6% → 5%）', tag: 'normal', tagText: '普通', time: '3天前', completed: true },
        { title: '归档一季度发票数据', tag: 'normal', tagText: '普通', time: '1周前', completed: true }
      ],
      recentInvoices: [
        { invoiceNo: 'FP202603180012', customerName: '北京科技有限公司', amount: 125000, status: '已开票' },
        { invoiceNo: 'FP202603180011', customerName: '上海投资集团', amount: 89000, status: '已开票' },
        { invoiceNo: 'FP202603180010', customerName: '深圳金融服务公司', amount: 234000, status: '已开票' },
        { invoiceNo: 'FP202603180009', customerName: '广州贸易有限公司', amount: 67000, status: '处理中' },
        { invoiceNo: 'FP202603180008', customerName: '杭州科技有限公司', amount: 156000, status: '已开票' },
        { invoiceNo: 'FP202603180007', customerName: '成都投资集团', amount: 98000, status: '已开票' },
        { invoiceNo: 'FP202603180006', customerName: '武汉金融服务公司', amount: 178000, status: '处理中' }
      ],
      topCustomers: [
        { name: '北京科技有限公司', amount: 3580000, invoiceCount: 45 },
        { name: '上海投资集团', amount: 2890000, invoiceCount: 38 },
        { name: '深圳金融服务公司', amount: 2150000, invoiceCount: 32 },
        { name: '广州贸易有限公司', amount: 1860000, invoiceCount: 28 },
        { name: '杭州科技有限公司', amount: 1450000, invoiceCount: 22 }
      ]
    }
  },
  watch: {
    dateRange() {
      this.loadDashboard()
      this.loadMonthlyTrend()
    }
  },
  mounted() {
    const now = new Date()
    const year = now.getFullYear()
    const month = String(now.getMonth() + 1).padStart(2, '0')
    const day = String(now.getDate()).padStart(2, '0')
    const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    const weekDay = weekDays[now.getDay()]
    this.currentDate = `${year}年${month}月${day}日 ${weekDay}`
    this.loadDashboard()
    this.loadMonthlyTrend()
  },
  methods: {
    loadDashboard() {
      this.$http.get('/statistics/dashboard', { params: { dateRange: this.dateRange } }).then(res => {
        const data = res.data
        this.stats.blueAmount = data.blueAmount || 0
        this.stats.invoiceCount = data.invoiceCount || 0
        this.stats.redAmount = data.redAmount || 0
        this.stats.pendingReview = data.pendingReview || 0
        this.stats.failed = data.failed || 0
      }).catch(() => {})
    },
    loadMonthlyTrend() {
      this.$http.get('/statistics/monthly-trend', { params: { dateRange: this.dateRange } }).then(res => {
        const list = res.data || []
        const maxValue = Math.max(...list.map(item => item.value), 1)
        this.chartData = list.map(item => ({
          label: item.label,
          value: item.value,
          percent: Math.round((item.value / maxValue) * 100)
        }))
      }).catch(() => {})
    },
    formatNumber(num) {
      if (num >= 100000000) {
        return (num / 100000000).toFixed(2) + '亿'
      } else if (num >= 10000) {
        return (num / 10000).toFixed(2) + '万'
      }
      return num.toLocaleString()
    },
    getRankClass(index) {
      if (index === 0) return 'gold'
      if (index === 1) return 'silver'
      if (index === 2) return 'bronze'
      return ''
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.ph-title {
  font-size: 22px;
  font-weight: 600;
  color: var(--fin-foreground);
  margin: 0 0 4px;
}

.ph-desc {
  font-size: 13px;
  color: var(--fin-muted-foreground);
  margin: 0;
}

.date-range {
  display: flex;
  background: var(--fin-card);
  border-radius: var(--fin-radius-md);
  padding: 4px;
  border: 1px solid var(--fin-border);
}

.dr-btn {
  padding: 6px 16px;
  border: none;
  background: transparent;
  border-radius: var(--fin-radius-sm);
  font-size: 12.5px;
  color: var(--fin-muted-foreground);
  cursor: pointer;
  transition: all 0.15s ease;
}

.dr-btn.active {
  background: var(--fin-primary-700);
  color: #fff;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  background: var(--fin-card);
  border: 1px solid var(--fin-border);
  border-radius: var(--fin-radius-lg);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.sc-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--fin-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.sc-icon.blue {
  background: var(--fin-primary-50);
  color: var(--fin-primary-700);
}

.sc-icon.green {
  background: #ecfdf5;
  color: #059669;
}

.sc-icon.red {
  background: #fef2f2;
  color: #b91c1c;
}

.sc-icon.orange {
  background: #fff7ed;
  color: #ea580c;
}

.sc-icon.purple {
  background: #f5f3ff;
  color: #7c3aed;
}

.sc-value {
  font-size: 20px;
  font-weight: 600;
  color: var(--fin-foreground);
  margin-bottom: 4px;
}

.sc-label {
  font-size: 12.5px;
  color: var(--fin-muted-foreground);
  margin-bottom: 6px;
}

.sc-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
}

.sc-trend.up {
  color: #059669;
}

.sc-trend.down {
  color: #b91c1c;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.card {
  background: var(--fin-card);
  border: 1px solid var(--fin-border);
  border-radius: var(--fin-radius-lg);
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid var(--fin-border);
}

.ch-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--fin-foreground);
  margin: 0 0 2px;
}

.ch-sub {
  font-size: 12px;
  color: var(--fin-muted-foreground);
}

.ch-right {
  display: flex;
  gap: 4px;
}

.ch-btn {
  padding: 4px 12px;
  border: none;
  background: transparent;
  border-radius: var(--fin-radius-sm);
  font-size: 12px;
  color: var(--fin-muted-foreground);
  cursor: pointer;
  transition: all 0.15s ease;
}

.ch-btn.active {
  background: var(--fin-primary-700);
  color: #fff;
}

.ch-action {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border: none;
  background: transparent;
  border-radius: var(--fin-radius-sm);
  font-size: 12px;
  color: var(--fin-primary-700);
  cursor: pointer;
  transition: all 0.15s ease;
}

.ch-action:hover {
  background: var(--fin-primary-50);
}

.chart-area {
  padding: 20px;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  height: 200px;
  padding-top: 20px;
}

.bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 12%;
}

.bar-wrapper {
  width: 100%;
  height: 160px;
  background: var(--fin-muted);
  border-radius: var(--fin-radius-sm);
  display: flex;
  align-items: flex-end;
  overflow: hidden;
}

.bar-fill {
  width: 100%;
  background: linear-gradient(180deg, var(--fin-primary-600) 0%, var(--fin-primary-700) 100%);
  border-radius: var(--fin-radius-sm);
  transition: height 0.3s ease;
}

.bar-label {
  font-size: 12px;
  color: var(--fin-muted-foreground);
  margin-top: 8px;
}

.bar-value {
  font-size: 11px;
  color: var(--fin-foreground);
  font-weight: 500;
  margin-top: 4px;
}

.todo-list {
  padding: 4px 20px;
}

.todo-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid var(--fin-border);
  transition: opacity 0.2s ease;
}

.todo-item:last-child {
  border-bottom: none;
}

.todo-item.completed {
  opacity: 0.5;
}

.todo-checkbox {
  width: 16px;
  height: 16px;
  accent-color: var(--fin-primary-700);
  margin-top: 2px;
}

.todo-title {
  font-size: 13.5px;
  color: var(--fin-foreground);
  margin-bottom: 6px;
}

.todo-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.todo-tag {
  padding: 2px 8px;
  border-radius: var(--fin-radius-sm);
  font-size: 11px;
  font-weight: 500;
}

.todo-tag.urgent {
  background: #fef2f2;
  color: #b91c1c;
}

.todo-tag.warning {
  background: #fff7ed;
  color: #ea580c;
}

.todo-tag.normal {
  background: var(--fin-muted);
  color: var(--fin-muted-foreground);
}

.todo-time {
  font-size: 11.5px;
  color: var(--fin-muted-foreground);
}

.recent-list {
  padding: 4px 20px;
}

.recent-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid var(--fin-border);
}

.recent-item:last-child {
  border-bottom: none;
}

.ri-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ri-icon {
  width: 32px;
  height: 32px;
  border-radius: var(--fin-radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
}

.ri-icon.success {
  background: #ecfdf5;
  color: #059669;
}

.ri-icon.pending {
  background: #fef3c7;
  color: #d97706;
}

.ri-number {
  font-size: 13.5px;
  font-weight: 500;
  color: var(--fin-foreground);
  margin-bottom: 3px;
}

.ri-customer {
  font-size: 12px;
  color: var(--fin-muted-foreground);
}

.ri-amount {
  font-size: 14px;
  font-weight: 600;
  color: var(--fin-foreground);
  margin-bottom: 3px;
}

.ri-status {
  font-size: 11.5px;
  padding: 2px 8px;
  border-radius: var(--fin-radius-sm);
}

.ri-status.success {
  background: #ecfdf5;
  color: #059669;
}

.ri-status.pending {
  background: #fef3c7;
  color: #d97706;
}

.customer-list {
  padding: 4px 20px;
}

.customer-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid var(--fin-border);
}

.customer-item:last-child {
  border-bottom: none;
}

.ci-rank {
  width: 24px;
  height: 24px;
  border-radius: var(--fin-radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  color: var(--fin-muted-foreground);
  background: var(--fin-muted);
}

.ci-rank.gold {
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  color: #fff;
}

.ci-rank.silver {
  background: linear-gradient(135deg, #9ca3af, #6b7280);
  color: #fff;
}

.ci-rank.bronze {
  background: linear-gradient(135deg, #d97706, #b45309);
  color: #fff;
}

.ci-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--fin-primary-100);
  color: var(--fin-primary-700);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
}

.ci-name {
  font-size: 13.5px;
  color: var(--fin-foreground);
  margin-bottom: 3px;
}

.ci-count {
  font-size: 12px;
  color: var(--fin-muted-foreground);
}

.ci-amount {
  margin-left: auto;
  font-size: 14px;
  font-weight: 600;
  color: var(--fin-foreground);
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .content-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>