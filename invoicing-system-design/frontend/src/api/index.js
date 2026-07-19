import axios from 'axios'
import { Message } from 'element-ui'

const service = axios.create({
  baseURL: '/api',
  timeout: 30000
})

service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
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
    Message({
      message: error.message || '网络异常',
      type: 'error',
      duration: 3000
    })
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