import service from './index'

export const getAllTasks = (params) => {
  return service({
    url: '/batch/tasks',
    method: 'get',
    params
  })
}

export const getTaskById = (id) => {
  return service({
    url: `/batch/tasks/${id}`,
    method: 'get'
  })
}

export const getTaskItems = (id) => {
  return service({
    url: `/batch/tasks/${id}/items`,
    method: 'get'
  })
}

export const createTask = (taskName, taskType, totalCount) => {
  return service({
    url: '/batch/tasks',
    method: 'post',
    params: { taskName, taskType, totalCount }
  })
}

export const addBatchItems = (id, items) => {
  return service({
    url: `/batch/tasks/${id}/items`,
    method: 'post',
    data: items
  })
}

export const startTask = (id) => {
  return service({
    url: `/batch/tasks/${id}/start`,
    method: 'post'
  })
}

export const pauseTask = (id) => {
  return service({
    url: `/batch/tasks/${id}/pause`,
    method: 'post'
  })
}

export const resumeTask = (id) => {
  return service({
    url: `/batch/tasks/${id}/resume`,
    method: 'post'
  })
}
