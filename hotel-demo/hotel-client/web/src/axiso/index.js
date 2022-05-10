import axios from "axios";
import {storage} from "@/store/localStorage";

if (process.env.NODE_ENV === 'production') {
    axios.defaults.baseURL = 'https://xxxxxx/xxx'
}else{
    axios.defaults.baseURL = 'http://localhost:8089'
}

const request = axios.create({
    headers: {
        common: {
            token: storage.token
        }
    },
    contentType: 'application/json',
    timeout: 30 * 1000
})

export default request;