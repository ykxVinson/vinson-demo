import axios from "axios";
import {message} from 'ant-design-vue';

if (process.env.NODE_ENV === 'production') {
    axios.defaults.baseURL = 'https://xxxxxx/xxx'
}else{
    axios.defaults.baseURL = 'http://localhost:8099'
}

const request = axios.create({
    headers: {
        common: {
            token: ''
        }
    },
    contentType: 'application/json',
    timeout: 30 * 1000
})

request.interceptors.request.use(config => {
    return config
}, error => {
    return Promise.reject(error)
})

request.interceptors.response.use(response => {
    const data = response.data
    const status = response.status
    if (status !== 200) {
        message.error(response.statusText);
        console.log(response.statusText);
        return Promise.reject(data)
    }
    return data
}, error => {
    return Promise.reject(error)
})

export default request;