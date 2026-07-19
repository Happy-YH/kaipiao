import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

const router = new Router({
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/',
      component: () => import('@/layout/MainLayout.vue'),
      meta: { title: '首页' },
      children: [
        {
          path: '',
          name: 'Dashboard',
          component: () => import('@/views/Dashboard.vue'),
          meta: { title: '工作台' }
        },
        {
          path: 'blue-invoice',
          name: 'BlueInvoice',
          component: () => import('@/views/invoice/BlueInvoice.vue'),
          meta: { title: '蓝字开票' }
        },
        {
          path: 'red-invoice',
          name: 'RedInvoice',
          component: () => import('@/views/invoice/RedInvoice.vue'),
          meta: { title: '红字冲销' }
        },
        {
          path: 'invoice-list',
          name: 'InvoiceList',
          component: () => import('@/views/invoice/InvoiceList.vue'),
          meta: { title: '开票记录' }
        },
        {
          path: 'batch-invoice',
          name: 'BatchInvoice',
          component: () => import('@/views/invoice/BatchInvoice.vue'),
          meta: { title: '批量开票' }
        },
        {
          path: 'customers',
          name: 'Customers',
          component: () => import('@/views/Customers.vue'),
          meta: { title: '客户管理' }
        },
        {
          path: 'contracts',
          name: 'Contracts',
          component: () => import('@/views/Contracts.vue'),
          meta: { title: '贷款合同' }
        },
        {
          path: 'repayments',
          name: 'Repayments',
          component: () => import('@/views/Repayments.vue'),
          meta: { title: '还款记录' }
        },
        {
          path: 'red-confirmations',
          name: 'RedConfirmations',
          component: () => import('@/views/RedConfirmations.vue'),
          meta: { title: '红字确认单' }
        },
        {
          path: 'tax-classifications',
          name: 'TaxClassifications',
          component: () => import('@/views/TaxClassifications.vue'),
          meta: { title: '税率设置' }
        }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login') {
    next()
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
