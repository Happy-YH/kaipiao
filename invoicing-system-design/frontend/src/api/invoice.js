import service from './index'

export const getAllInvoices = (params) => {
  return service({
    url: '/invoices',
    method: 'get',
    params
  })
}

export const getInvoiceById = (id) => {
  return service({
    url: `/invoices/${id}`,
    method: 'get'
  })
}

export const getInvoiceDetail = (id) => {
  return service({
    url: `/invoices/${id}/detail`,
    method: 'get'
  })
}

export const getInvoiceByNo = (invoiceNo) => {
  return service({
    url: `/invoices/no/${invoiceNo}`,
    method: 'get'
  })
}

export const createInvoice = (data) => {
  return service({
    url: '/invoices',
    method: 'post',
    data
  })
}

export const submitForReview = (id) => {
  return service({
    url: `/invoices/${id}/submit`,
    method: 'post'
  })
}

export const reviewInvoice = (id, approved) => {
  return service({
    url: `/invoices/${id}/review`,
    method: 'post',
    params: { approved }
  })
}

export const issueInvoice = (id) => {
  return service({
    url: `/invoices/${id}/issue`,
    method: 'post'
  })
}

export const deliverInvoice = (id) => {
  return service({
    url: `/invoices/${id}/deliver`,
    method: 'post'
  })
}

export const deleteInvoice = (id) => {
  return service({
    url: `/invoices/${id}`,
    method: 'delete'
  })
}

export const createRedInvoice = (data) => {
  return service({
    url: '/red-invoices',
    method: 'post',
    data
  })
}

export const getInvoiceRepayments = (id) => {
  return service({
    url: `/invoices/${id}/repayments`,
    method: 'get'
  })
}