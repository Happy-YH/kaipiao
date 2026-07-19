import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 30000
})

service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      // 后端 AuthInterceptor 同时支持 Bearer 前缀与裸 token
      config.headers['Authorization'] = token.startsWith('Bearer ') ? token : 'Bearer ' + token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    // 二进制流响应（导出文件）直接返回，避免对 Blob 调用 .code
    if (response.config && response.config.responseType === 'blob') {
      return response
    }
    const res = response.data
    if (res && res.code !== 200) {
      // 401 未登录或登录过期，跳转登录
      if (res.code === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('username')
        if (router.currentRoute.path !== '/login') {
          router.push('/login')
        }
      }
      Message({
        message: res.message || '请求失败',
        type: 'error',
        duration: 3000
      })
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    const status = error.response && error.response.status
    if (status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      if (router.currentRoute.path !== '/login') {
        router.push('/login')
      }
      Message({ message: '登录已过期，请重新登录', type: 'error', duration: 3000 })
    } else {
      Message({
        message: (error.response && error.response.data && error.response.data.message) || error.message || '网络异常',
        type: 'error',
        duration: 3000
      })
    }
    return Promise.reject(error)
  }
)

export default service

export function getKingdeeToken() {
  return service.get('/kingdee/token')
}

export function queryKingdeeInvoice(invoiceNo) {
  return service.get(`/kingdee/invoice/${invoiceNo}`)
}

export function cancelKingdeeInvoice(invoiceNo) {
  return service.post(`/kingdee/invoice/${invoiceNo}/cancel`)
}

export function deliverKingdeeInvoice(invoiceNo, email, phone) {
  return service.post(`/kingdee/invoice/${invoiceNo}/deliver`, null, {
    params: { email, phone }
  })
}

export function getKingdeeTaxClassifications() {
  return service.get('/kingdee/tax-classifications')
}

export function getKingdeeStatus() {
  return service.get('/kingdee/status')
}

/** 导出发票列表为Excel */
export function exportInvoicesExcel(params) {
  return service.get('/export/invoices/excel', { params, responseType: 'blob' })
}

/** 导出发票列表为PDF */
export function exportInvoicesPdf(params) {
  return service.get('/export/invoices/pdf', { params, responseType: 'blob' })
}

/** 导出发票详情为PDF */
export function exportInvoiceDetailPdf(id) {
  return service.get(`/export/invoice/${id}/pdf`, { responseType: 'blob' })
}

/** 导入批量开票Excel文件 */
export function importBatchInvoice(file) {
  const formData = new FormData()
  formData.append('file', file)
  return service.post('/export/batch-invoice/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/** 下载批量开票导入模板 */
export function downloadBatchTemplate() {
  return service.get('/export/batch-invoice/template', { responseType: 'blob' })
}