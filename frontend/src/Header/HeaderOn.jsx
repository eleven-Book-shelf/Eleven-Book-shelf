import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import './Header.css';
import axiosInstance from "../api/axiosInstance";

const HeaderOn = ({ onLogout }) => {
    const navigate = useNavigate();

    const handleLogoutClick = async () => {
        try {
            await axiosInstance.patch('/logout', null, {
                headers: { Authorization: `${localStorage.getItem('Authorization')}` }
            });
            onLogout();
            navigate('/login');
        } catch (error) {
            console.error('로그아웃 실패:', error);
        }
    };

    axiosInstance.interceptors.response.use(
        response => response,
        error => {
            if (error.response && error.response.status === 401) {
                // JWT가 만료된 경우
                onLogout();
                navigate('/login');
            }
            return Promise.reject(error);
        }
    );

    return (
        <header className="header">
            <a href="/" className="logo">11조 프로젝트</a>
            <nav className="nav">
                <a href="/webtoon" className="nav-link">웹툰</a>
                <a href="/webnovel" className="nav-link">웹소설</a>
                <a href="/community" className="nav-link">커뮤니티</a>
            </nav>
            <div className="auth-buttons">
                <a href="/mypage" className="nav-link active">마이페이지</a>
                <button onClick={handleLogoutClick} className="button">로그아웃</button>
            </div>
        </header>
    );
}

export default HeaderOn;
