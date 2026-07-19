import service from './index'

export const getAllContracts = (params) => {
  return service({
    url: '/contracts',
    method: 'get',
    params
  })
}

export const getContractById = (id) => {
  return service({
    url: `/contracts/${id}`,
    method: 'get'
  })
}

export const getContractByNo = (contractNo) => {
  return service({
    url: `/contracts/no/${contractNo}`,
    method: 'get'
  })
}

export const createContract = (data) => {
  return service({
    url: '/contracts',
    method: 'post',
    data
  })
}

export const updateContract = (id, data) => {
  return service({
    url: `/contracts/${id}`,
    method: 'put',
    data
  })
}