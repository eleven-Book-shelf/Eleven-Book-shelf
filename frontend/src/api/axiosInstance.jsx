import axios from 'axios';

const axiosInstance = axios.create({
    baseURL: 'https://www.elevenbookshelf.com/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

export default axiosInstance;
