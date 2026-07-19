import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

const router = new Router({
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/',
      component: () => import('@/layout/MainLayout.vue'),
      children: [
        {
          path: '',
          name: 'Dashboard',
          component: () => import('@/views/Dashboard.vue')
        },
        {
          path: 'blue-invoice',
          name: 'BlueInvoice',
          component: () => import('@/views/invoice/BlueInvoice.vue')
        },
        {
          path: 'red-invoice',
          name: 'RedInvoice',
          component: () => import('@/views/invoice/RedInvoice.vue')
        },
        {
          path: 'invoice-list',
          name: 'InvoiceList',
          component: () => import('@/views/invoice/InvoiceList.vue')
        },
        {
          path: 'batch-invoice',
          name: 'BatchInvoice',
          component: () => import('@/views/invoice/BatchInvoice.vue')
        },
        {
          path: 'customers',
          name: 'Customers',
          component: () => import('@/views/Customers.vue')
        },
        {
          path: 'contracts',
          name: 'Contracts',
          component: () => import('@/views/Contracts.vue')
        },
        {
          path: 'repayments',
          name: 'Repayments',
          component: () => import('@/views/Repayments.vue')
        },
        {
          path: 'red-confirmations',
          name: 'RedConfirmations',
          component: () => import('@/views/RedConfirmations.vue')
        },
        {
          path: 'tax-classifications',
          name: 'TaxClassifications',
          component: () => import('@/views/TaxClassifications.vue')
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