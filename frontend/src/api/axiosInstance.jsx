import axios from 'axios';

const axiosInstance = axios.create({
    baseURL: 'http://localhost:8080', // 배포 시 'https://www.elevenbookshelf.com'으로 변경
    headers: {
        'Content-Type': 'application/json',
    },
});

axiosInstance.interceptors.response.use(
    response => response,
    async error => {
        const originalRequest = error.config;

        // 에러가 토큰 만료로 인한 것인지 확인하고, 요청이 이미 재시도된 것이 아닌지 확인
        if (error.response && error.response.status === 403 && !originalRequest._retry) {
            originalRequest._retry = true; // 재시도 플래그 설정

            try {
                const refreshToken = localStorage.getItem('RefreshToken');
                const response = await axios.post('http://localhost:8080/api/auth/refresh', null, {
                    params: { token: refreshToken }
                });

                const { accessToken, refreshToken: newRefreshToken } = response.data;
                localStorage.setItem('Authorization', accessToken);
                localStorage.setItem('RefreshToken', newRefreshToken);

                originalRequest.headers['Authorization'] = `${accessToken}`;
                return axiosInstance(originalRequest);
            } catch (refreshError) {
                console.error('토큰 갱신 실패:', refreshError);
                return Promise.reject(refreshError);
            }
        }
        return Promise.reject(error);
    }
);

export default axiosInstance;
