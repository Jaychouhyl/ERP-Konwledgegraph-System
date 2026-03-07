import request from '@/utils/request'

/**
 * 统一登录入口
 * API 文档指出参数位置在 query
 */
export function login(username, password) {
  return request({
    url: '/auth/login',
    method: 'post',
    params: { // 注意：这里用 params，Axios 会将其拼装为 query 字符串 ?username=..&password=..
      username: username,
      password: password
    }
  })
}