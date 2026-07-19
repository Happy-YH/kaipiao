import service from './index'

export const getAllRecords = (params) => {
  return service({
    url: '/repayments',
    method: 'get',
    params
  })
}

export const getRecordById = (id) => {
  return service({
    url: `/repayments/${id}`,
    method: 'get'
  })
}

export const createRecord = (data) => {
  return service({
    url: '/repayments',
    method: 'post',
    data
  })
}

export const updateRecord = (id, data) => {
  return service({
    url: `/repayments/${id}`,
    method: 'put',
    data
  })
}

export const updateInvoiceStatus = (id, status) => {
  return service({
    url: `/repayments/${id}/status`,
    method: 'put',
    params: { status }
  })
}