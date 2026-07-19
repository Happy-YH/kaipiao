<template>
  <div class="main-layout">
    <aside class="sidebar" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <div class="sidebar-brand">
        <div class="brand-inner">
          <span class="brand-logo">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 5h18v14H3z"/><path d="M3 9h18"/><path d="M8 14h4"/></svg>
          </span>
          <span class="brand-name">金融开票系统</span>
        </div>
      </div>

      <nav class="sidebar-nav">
        <div class="nav-group">
          <div class="nav-title">工作台</div>
          <ul class="nav-items">
            <li :class="{ active: $route.path === '/' }">
              <router-link to="/">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
                <span>工作台</span>
              </router-link>
            </li>
          </ul>
        </div>

        <div class="nav-group">
          <div class="nav-title">开票业务</div>
          <ul class="nav-items">
            <li :class="{ active: $route.path === '/blue-invoice' }">
              <router-link to="/blue-invoice">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10h-18"/><path d="M22 6h-18"/><path d="M22 14h-18"/><path d="M22 18h-18"/><path d="M10 6v12"/><path d="M14 6v12"/><path d="M18 6v12"/></svg>
                <span>开票申请</span>
              </router-link>
            </li>
            <li :class="{ active: $route.path === '/red-invoice' }">
              <router-link to="/red-invoice">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/></svg>
                <span>红字冲销</span>
              </router-link>
            </li>
            <li :class="{ active: $route.path === '/invoice-list' }">
              <router-link to="/invoice-list">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/><line x1="16" x2="8" y1="13" y2="13"/><line x1="16" x2="8" y1="17" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
                <span>开票记录</span>
              </router-link>
            </li>
            <li :class="{ active: $route.path === '/batch-invoice' }">
              <router-link to="/batch-invoice">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" x2="8" y1="13" y2="13"/><line x1="16" x2="8" y1="17" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
                <span>手工开票</span>
              </router-link>
            </li>
          </ul>
        </div>

        <div class="nav-group">
          <div class="nav-title">基础数据</div>
          <ul class="nav-items">
            <li :class="{ active: $route.path === '/customers' }">
              <router-link to="/customers">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                <span>客户管理</span>
              </router-link>
            </li>
            <li :class="{ active: $route.path === '/contracts' }">
              <router-link to="/contracts">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/></svg>
                <span>贷款合同</span>
              </router-link>
            </li>
            <li :class="{ active: $route.path === '/repayments' }">
              <router-link to="/repayments">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                <span>还款记录</span>
              </router-link>
            </li>
            <li :class="{ active: $route.path === '/tax-classifications' }">
              <router-link to="/tax-classifications">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><path d="m9 12 2 2 4-4"/></svg>
                <span>税率设置</span>
              </router-link>
            </li>
          </ul>
        </div>

        <div class="nav-group">
          <div class="nav-title">审批管理</div>
          <ul class="nav-items">
            <li :class="{ active: $route.path === '/red-confirmations' }">
              <router-link to="/red-confirmations">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 12l2 2 4-4"/><circle cx="12" cy="12" r="10"/></svg>
                <span>红字确认单</span>
              </router-link>
            </li>
          </ul>
        </div>
      </nav>

      <div class="sidebar-footer">
        <button class="collapse-btn" @click="sidebarCollapsed = !sidebarCollapsed">
          <svg v-if="sidebarCollapsed" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m9 18 6-6-6-6"/></svg>
          <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m15 18-6-6 6-6"/></svg>
        </button>
      </div>
    </aside>

    <div class="main-content">
      <header class="topbar">
        <div class="topbar-left">
          <button class="sidebar-toggle" @click="sidebarCollapsed = !sidebarCollapsed">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="3" x2="21" y1="6" y2="6"/><line x1="3" x2="21" y1="12" y2="12"/><line x1="3" x2="21" y1="18" y2="18"/></svg>
          </button>
          <div class="breadcrumbs">
            <span v-for="(crumb, index) in breadcrumbs" :key="index">
              <router-link v-if="index < breadcrumbs.length - 1" :to="crumb.path">{{ crumb.name }}</router-link>
              <span v-else>{{ crumb.name }}</span>
              <span v-if="index < breadcrumbs.length - 1" class="crumb-sep">/</span>
            </span>
          </div>
        </div>

        <div class="topbar-right">
          <div class="search-box">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
            <input type="text" placeholder="搜索发票号、合同号、客户名称..." class="search-input">
          </div>

          <button class="topbar-btn" title="消息通知">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>
            <span class="badge">2</span>
          </button>

          <button class="topbar-btn" title="系统设置">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09a1.65 1.65 0 0 0-1-1.51 1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09a1.65 1.65 0 0 0 1.51-1 1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>
          </button>

          <div class="user-menu" @click="handleUserMenuClick">
            <div class="user-avatar">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </div>
            <div class="user-info">
              <span class="user-name">{{ username }}</span>
              <span class="user-role">{{ roleText }}</span>
            </div>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="6 9 12 15 18 9"/></svg>

            <div class="user-dropdown" v-if="dropdownOpen" @click.stop>
              <div class="dropdown-header">
                <div class="dropdown-name">{{ username }}</div>
                <div class="dropdown-role">{{ roleText }}</div>
              </div>
              <div class="dropdown-divider"></div>
              <button class="dropdown-item" @click="handleLogout">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" x2="9" y1="12" y2="12"/></svg>
                <span>退出登录</span>
              </button>
            </div>
          </div>
        </div>
      </header>

      <router-view/>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MainLayout',
  data() {
    return {
      sidebarCollapsed: false,
      dropdownOpen: false,
      username: localStorage.getItem('username') || '管理员',
      role: localStorage.getItem('role') || 'ADMIN'
    }
  },
  computed: {
    roleText() {
      const map = {
        'ADMIN': '系统管理员',
        'OPERATOR': '操作员',
        'REVIEWER': '审核员'
      }
      return map[this.role] || '系统管理员'
    },
    breadcrumbs() {
      const routes = []
      const matched = this.$route.matched
      matched.forEach(item => {
        if (item.meta && item.meta.title) {
          routes.push({ name: item.meta.title, path: item.path })
        }
      })
      return routes
    }
  },
  mounted() {
    document.addEventListener('click', this.handleOutsideClick)
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleOutsideClick)
  },
  beforeMount() {
    if (!localStorage.getItem('token')) {
      this.$router.push('/login')
    }
  },
  methods: {
    handleUserMenuClick() {
      this.dropdownOpen = !this.dropdownOpen
    },
    handleOutsideClick(e) {
      const menu = this.$el.querySelector('.user-menu')
      if (menu && !menu.contains(e.target)) {
        this.dropdownOpen = false
      }
    },
    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        localStorage.removeItem('token')
        localStorage.removeItem('username')
        localStorage.removeItem('role')
        this.$message.success('已退出登录')
        this.$router.push('/login')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.main-layout {
  display: flex;
  min-height: 100vh;
  background: var(--fin-background);
}

.sidebar {
  width: 220px;
  flex-shrink: 0;
  background: var(--fin-primary-900);
  display: flex;
  flex-direction: column;
  transition: width 0.25s ease;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar-collapsed {
  width: 64px;
}

.sidebar-brand {
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, .06);
}

.brand-inner {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-logo {
  width: 36px;
  height: 36px;
  border-radius: var(--fin-radius-md);
  background: rgba(255, 255, 255, .1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.brand-name {
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sidebar-collapsed .brand-name {
  display: none;
}

.sidebar-nav {
  flex: 1;
  padding: 12px 0;
  overflow-y: auto;
}

.nav-group {
  margin-bottom: 8px;
}

.nav-title {
  font-size: 11px;
  font-weight: 600;
  color: rgba(255, 255, 255, .45);
  padding: 8px 20px 6px;
  text-transform: uppercase;
  letter-spacing: .06em;
}

.sidebar-collapsed .nav-title {
  display: none;
}

.nav-items {
  list-style: none;
  margin: 0;
  padding: 0;
}

.nav-items li {
  margin: 2px 8px;
  border-radius: var(--fin-radius-sm);
  transition: background-color 0.15s ease;
}

.nav-items li a {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  color: rgba(255, 255, 255, .75);
  text-decoration: none;
  font-size: 13.5px;
  transition: color 0.15s ease;
}

.nav-items li.active {
  background: rgba(255, 255, 255, .08);
}

.nav-items li.active a {
  color: #fff;
}

.nav-items li:hover:not(.active) {
  background: rgba(255, 255, 255, .04);
}

.nav-items li a svg {
  flex-shrink: 0;
  opacity: 0.7;
}

.nav-items li.active a svg {
  opacity: 1;
}

.sidebar-collapsed .nav-items li a span {
  display: none;
}

.sidebar-footer {
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, .06);
}

.collapse-btn {
  width: 100%;
  height: 34px;
  border: none;
  background: rgba(255, 255, 255, .06);
  border-radius: var(--fin-radius-sm);
  color: rgba(255, 255, 255, .6);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.15s ease;
}

.collapse-btn:hover {
  background: rgba(255, 255, 255, .1);
  color: rgba(255, 255, 255, .8);
}

.main-content {
  flex: 1;
  margin-left: 220px;
  transition: margin-left 0.25s ease;
  display: flex;
  flex-direction: column;
}

.sidebar-collapsed ~ .main-content {
  margin-left: 64px;
}

.topbar {
  height: 56px;
  background: var(--fin-card);
  border-bottom: 1px solid var(--fin-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  flex-shrink: 0;
}

.topbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.sidebar-toggle {
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  border-radius: var(--fin-radius-sm);
  color: var(--fin-muted-foreground);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.15s ease;
}

.sidebar-toggle:hover {
  background: var(--fin-muted);
  color: var(--fin-foreground);
}

.breadcrumbs {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: var(--fin-muted-foreground);
}

.breadcrumbs a {
  color: var(--fin-primary-700);
  text-decoration: none;
}

.breadcrumbs a:hover {
  text-decoration: underline;
}

.crumb-sep {
  margin: 0 6px;
  color: var(--fin-border);
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: var(--fin-muted);
  border-radius: var(--fin-radius-md);
  border: 1px solid transparent;
  transition: all 0.15s ease;
}

.search-box:focus-within {
  background: var(--fin-card);
  border-color: var(--fin-input);
  box-shadow: 0 0 0 3px rgba(58, 93, 172, 0.08);
}

.search-box svg {
  color: var(--fin-muted-foreground);
}

.search-input {
  border: none;
  background: transparent;
  outline: none;
  font-size: 13px;
  color: var(--fin-foreground);
  width: 220px;
}

.search-input::placeholder {
  color: var(--fin-muted-foreground);
}

.topbar-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  border-radius: var(--fin-radius-sm);
  color: var(--fin-muted-foreground);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: all 0.15s ease;
}

.topbar-btn:hover {
  background: var(--fin-muted);
  color: var(--fin-foreground);
}

.topbar-btn .badge {
  position: absolute;
  top: 4px;
  right: 4px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  background: var(--fin-destructive);
  color: #fff;
  font-size: 10px;
  font-weight: 600;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px 6px 8px;
  border-left: 1px solid var(--fin-border);
  cursor: pointer;
  transition: background-color 0.15s ease;
  position: relative;
}

.user-menu:hover {
  background: var(--fin-muted);
}

.user-dropdown {
  position: absolute;
  top: calc(100% + 4px);
  right: 0;
  min-width: 200px;
  background: var(--fin-card);
  border: 1px solid var(--fin-border);
  border-radius: var(--fin-radius-md);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  z-index: 1000;
  overflow: hidden;
}

.dropdown-header {
  padding: 12px 16px;
  background: var(--fin-muted);
}

.dropdown-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--fin-foreground);
}

.dropdown-role {
  font-size: 11px;
  color: var(--fin-muted-foreground);
  margin-top: 2px;
}

.dropdown-divider {
  height: 1px;
  background: var(--fin-border);
}

.dropdown-item {
  width: 100%;
  padding: 10px 16px;
  border: none;
  background: transparent;
  color: var(--fin-foreground);
  font-size: 13px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  text-align: left;
  transition: background-color 0.15s ease;
}

.dropdown-item:hover {
  background: var(--fin-muted);
  color: var(--fin-destructive);
}

.user-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: var(--fin-primary-100);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--fin-primary-700);
}

.user-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.user-name {
  font-size: 13px;
  font-weight: 500;
  color: var(--fin-foreground);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  font-size: 11px;
  color: var(--fin-muted-foreground);
}

.user-menu svg {
  color: var(--fin-muted-foreground);
  flex-shrink: 0;
}

.router-view {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
    z-index: 200;
  }
  .sidebar.sidebar-collapsed {
    transform: translateX(0);
    width: 64px;
  }
  .main-content {
    margin-left: 0;
  }
  .sidebar-collapsed ~ .main-content {
    margin-left: 64px;
  }
  .search-box {
    display: none;
  }
}
</style>