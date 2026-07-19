import service from './index'

export const getAllConfirmations = (params) => {
  return service({
    url: '/red-confirmations',
    method: 'get',
    params
  })
}

export const getConfirmationById = (id) => {
  return service({
    url: `/red-confirmations/${id}`,
    method: 'get'
  })
}

export const getConfirmationByNo = (confirmationNo) => {
  return service({
    url: `/red-confirmations/no/${confirmationNo}`,
    method: 'get'
  })
}

export const createRedConfirmation = (data) => {
  return service({
    url: '/red-confirmations',
    method: 'post',
    data
  })
}

export const confirmRed = (id) => {
  return service({
    url: `/red-confirmations/${id}/confirm`,
    method: 'post'
  })
}

export const cancelRed = (id) => {
  return service({
    url: `/red-confirmations/${id}/cancel`,
    method: 'post'
  })
}