import axios from 'axios'

const request = axios.create({
    baseURL: 'http://localhost:8080/api', // Backend URL
    timeout: 5000
})

request.interceptors.request.use(config => {
    // Add token if needed
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (user.userId) {
        // config.headers['Authorization'] = ...
    }
    return config
}, error => {
    return Promise.reject(error)
})

request.interceptors.response.use(response => {
    const res = response.data
    if (res.code !== 200) {
        // Handle error
        return Promise.reject(new Error(res.msg || 'Error'))
    }
    return res.data
}, error => {
    return Promise.reject(error)
})

export default request
