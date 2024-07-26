import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import './Header.css';

const HeaderOn = ({ onLogout }) => {
    const navigate = useNavigate();

    const handleLogoutClick = () => {
        onLogout();
        navigate('/login'); // 로그아웃 후 로그인 페이지로 리디렉션
    };

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
