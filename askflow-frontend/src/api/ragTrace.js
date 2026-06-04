import request from './request'

export function listRagTraces(spaceId) {
  return request({
    url: '/api/rag/traces',
    method: 'get',
    params: {
      spaceId
    }
  })
}

export function getRagTraceDetail(id) {
  return request({
    url: `/api/rag/traces/${id}`,
    method: 'get'
  })
}