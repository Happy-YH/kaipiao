<template>
  <div class="login-wrap">
    <aside class="login-aside">
      <div class="la-top">
        <span class="la-logo">
          <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 5h18v14H3z"/><path d="M3 9h18"/><path d="M8 14h4"/></svg>
        </span>
        <div class="la-brand">
          <span class="la-name">金融贷款利息开票系统</span>
          <span class="la-sub">Loan Interest e-Invoicing Platform</span>
        </div>
      </div>
      <div class="la-mid">
        <h2 class="la-headline">合规开票 · 数据可溯<br>贷款利息电子发票一站式管理</h2>
        <ul class="la-points">
          <li><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><path d="m9 12 2 2 4-4"/></svg><span>对接全电发票平台，发票真伪实时校验</span></li>
          <li><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/><line x1="16" x2="8" y1="13" y2="13"/><line x1="16" x2="8" y1="17" y2="17"/><polyline points="10 9 9 9 8 9"/></svg><span>还款明细自动归集，开票申请一键提交</span></li>
          <li><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 20V10"/><path d="M12 20V4"/><path d="M6 20v-6"/></svg><span>开票趋势与税务台账可视化呈现</span></li>
        </ul>
      </div>
      <div class="la-foot">
        <span class="la-badge"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="18" x="3" y="3" rx="2" ry="2"/><path d="M7 16V8a5 5 0 0 1 10 0v8"/></svg> 国密 SM2 / SM4 加密传输</span>
        <span class="la-copy">© 2026 金融贷款利息开票系统 · v1.0.0</span>
      </div>
    </aside>

    <main class="login-main">
      <div class="login-card">
        <div class="lc-head">
          <h1 class="lc-title">欢迎登录</h1>
          <p class="lc-desc">请使用注册手机号登录系统</p>
        </div>

        <el-form ref="loginForm" :model="loginForm" :rules="rules" class="lc-form">
          <div class="form-field">
            <label class="form-label">手机号</label>
            <div class="phone-input">
              <span class="phone-prefix">+86</span>
              <el-input v-model="loginForm.username" placeholder="请输入 11 位手机号" inputmode="numeric" size="medium"></el-input>
            </div>
          </div>

          <div class="form-field">
            <label class="form-label">密码</label>
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" size="medium"></el-input>
          </div>

          <div class="lc-row">
            <label class="remember">
              <input type="checkbox" class="checkbox" v-model="rememberMe"> 记住登录
            </label>
            <button type="text" class="btn-link">忘记密码？</button>
          </div>

          <label class="agree">
            <input type="checkbox" class="checkbox" checked>
            <span>我已阅读并同意 <button type="text" class="btn-link">《服务协议》</button> 与 <button type="text" class="btn-link">《隐私政策》</button></span>
          </label>

          <el-button type="primary" class="lc-submit" @click="handleLogin">
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/><polyline points="10 17 15 12 10 7"/><line x1="15" x2="3" y1="12" y2="12"/></svg> 登 录
          </el-button>
        </el-form>

        <div class="lc-foot">
          <span class="lc-foot-text">还没有账号？</span>
          <button type="text" class="btn-link">联系管理员开通</button>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { login } from '@/api/auth'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      rememberMe: true,
      rules: {
        username: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          login(this.loginForm).then(res => {
            localStorage.setItem('token', res.data.token)
            localStorage.setItem('username', res.data.username)
            this.$router.push('/')
          }).catch(err => {
            this.$message.error(err.message || '登录失败')
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.login-wrap {
  display: flex;
  min-height: 100vh;
}

.login-aside {
  flex: 0 0 46%;
  background: linear-gradient(160deg, var(--fin-primary-900) 0%, var(--fin-primary-800) 55%, var(--fin-primary-700) 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 40px 44px;
  position: relative;
  overflow: hidden;
}

.login-aside::before {
  content: "";
  position: absolute;
  right: -120px;
  top: -120px;
  width: 380px;
  height: 380px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(93, 128, 196, .35), transparent 70%);
}

.login-aside::after {
  content: "";
  position: absolute;
  left: -80px;
  bottom: -80px;
  width: 260px;
  height: 260px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(31, 58, 122, .4), transparent 70%);
}

.la-top {
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;
  z-index: 1;
}

.la-logo {
  width: 42px;
  height: 42px;
  border-radius: var(--fin-radius-md);
  background: rgba(255, 255, 255, .12);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  border: 1px solid rgba(255, 255, 255, .18);
}

.la-brand {
  display: flex;
  flex-direction: column;
}

.la-name {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  line-height: 1.3;
}

.la-sub {
  font-size: 11px;
  color: rgba(255, 255, 255, .6);
  letter-spacing: .02em;
}

.la-mid {
  position: relative;
  z-index: 1;
}

.la-headline {
  font-size: 24px;
  font-weight: 600;
  color: #fff;
  line-height: 1.4;
  margin: 0 0 24px;
  letter-spacing: .01em;
}

.la-points {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.la-points li {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13.5px;
  color: rgba(255, 255, 255, .85);
}

.la-points li svg {
  color: var(--fin-primary-300);
  flex-shrink: 0;
}

.la-foot {
  display: flex;
  flex-direction: column;
  gap: 10px;
  position: relative;
  z-index: 1;
}

.la-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: rgba(255, 255, 255, .7);
  padding: 6px 10px;
  border: 1px solid rgba(255, 255, 255, .15);
  border-radius: var(--fin-radius-md);
  width: fit-content;
  background: rgba(255, 255, 255, .04);
}

.la-copy {
  font-size: 11.5px;
  color: rgba(255, 255, 255, .45);
}

.login-main {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
  background: var(--fin-background);
}

.login-card {
  width: 100%;
  max-width: 400px;
  background: var(--fin-card);
  border: 1px solid var(--fin-border);
  border-radius: var(--fin-radius-lg);
  padding: 36px 34px;
  box-shadow: var(--fin-shadow-md);
}

.lc-head {
  margin-bottom: 24px;
}

.lc-title {
  font-size: 22px;
  font-weight: 600;
  color: var(--fin-foreground);
  margin: 0 0 6px;
}

.lc-desc {
  font-size: 13px;
  color: var(--fin-muted-foreground);
  margin: 0;
}

.lc-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-field {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.form-label {
  font-size: 12.5px;
  font-weight: 500;
  color: var(--fin-foreground);
  margin-bottom: 6px;
}

.phone-input {
  display: flex;
  align-items: stretch;
}

.phone-prefix {
  display: inline-flex;
  align-items: center;
  padding: 0 12px;
  border: 1px solid var(--fin-input);
  border-right: none;
  border-radius: var(--fin-radius-md) 0 0 var(--fin-radius-md);
  background: var(--fin-muted);
  color: var(--fin-foreground);
  font-size: 13.5px;
  font-weight: 500;
}

.phone-input .el-input__inner {
  border-radius: 0 var(--fin-radius-md) var(--fin-radius-md) 0;
}

.lc-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.remember {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--fin-foreground);
  cursor: pointer;
}

.checkbox {
  width: 15px;
  height: 15px;
  accent-color: var(--fin-primary-700);
  cursor: pointer;
}

.btn-link {
  background: none;
  border: none;
  color: var(--fin-primary-700);
  font-size: 13px;
  font-weight: 500;
  padding: 0;
  cursor: pointer;
  text-decoration: none;
}

.btn-link:hover {
  text-decoration: underline;
}

.agree {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 12px;
  color: var(--fin-muted-foreground);
  line-height: 1.5;
  cursor: pointer;
}

.agree .checkbox {
  margin-top: 2px;
}

.agree .btn-link {
  font-size: 12px;
}

.lc-submit {
  margin-top: 4px;
  letter-spacing: 4px;
  height: 42px;
  background: var(--fin-primary-700);
  border-color: var(--fin-primary-700);
}

.lc-submit:hover {
  background: var(--fin-primary-800);
  border-color: var(--fin-primary-800);
}

.lc-foot {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 22px;
  padding-top: 18px;
  border-top: 1px dashed var(--fin-border);
}

.lc-foot-text {
  font-size: 12.5px;
  color: var(--fin-muted-foreground);
}

@media (max-width: 900px) {
  .login-aside {
    display: none;
  }
  .login-main {
    padding: 20px;
  }
}
</style>