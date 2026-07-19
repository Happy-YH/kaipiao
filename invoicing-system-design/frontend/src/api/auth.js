import service from './index'

export const login = (data) => {
  return service({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export const logout = () => {
  return service({
    url: '/auth/logout',
    method: 'post'
  })
}