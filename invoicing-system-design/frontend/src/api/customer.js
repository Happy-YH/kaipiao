import service from './index'

export const getAllCustomers = (params) => {
  return service({
    url: '/customers',
    method: 'get',
    params
  })
}

export const getCustomerById = (id) => {
  return service({
    url: `/customers/${id}`,
    method: 'get'
  })
}

export const getCustomerByTaxId = (taxId) => {
  return service({
    url: `/customers/tax/${taxId}`,
    method: 'get'
  })
}

export const createCustomer = (data) => {
  return service({
    url: '/customers',
    method: 'post',
    data
  })
}

export const updateCustomer = (id, data) => {
  return service({
    url: `/customers/${id}`,
    method: 'put',
    data
  })
}

export const deleteCustomer = (id) => {
  return service({
    url: `/customers/${id}`,
    method: 'delete'
  })
}