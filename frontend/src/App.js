import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes, useLocation } from 'react-router-dom';
import Header from './Header/Header';
import HeaderAuth from './Header/HeaderAuth';
import HeaderOn from './Header/HeaderOn';

import MyPage from './pages/MyPage/MyPage';
import LoginPage from './pages/LoginPage/LoginPage';
import AuthCallback from './api/AuthCallback';
import SignupPage from './pages/SignupPage/SignupPage';

import HomePage from './pages/HomePage/HomePage';
import WebtoonPage from './pages/WebtoonPage/WebtoonPage';
import ContentDetailPage from './pages/ContentDetailPage/ContentDetailPage';


import WebnovelPage from './pages/WebnovelPage/WebnovelPage';
import CommunityPage from './pages/CommunityPage/CommunityPage';
import BoardDetailPage from './pages/BoardDetailPage/BoardDetailPage';
import NewPostPage from './pages/NewPostPage/NewPostPage';

import './App.css';

const App = () => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    useEffect(() => {
        // 초기 로그인 상태 확인
        const token = localStorage.getItem('Authorization');
        if (token) {
            setIsLoggedIn(true);
        }
    }, []);

    const handleLogin = () => {
        setIsLoggedIn(true);
    };

    const handleLogout = () => {
        setIsLoggedIn(false);
        localStorage.removeItem('Authorization');
    };

    return (
        <Router>
            <div>
                <HeaderWrapper isLoggedIn={isLoggedIn} onLogout={handleLogout} />
                <Routes>
                    <Route path="/" element={<HomePage onLogin={handleLogin} />} />
                    <Route path="/webtoon" element={<WebtoonPage />} />
                    <Route path="/webnovel" element={<WebnovelPage />} />
                    <Route path="/webtoon/:id" element={<ContentDetailPage />} />
                    <Route path="/webnovel/:id" element={<ContentDetailPage />} />

                    <Route path="/signup" element={<SignupPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/auth/callback" element={<AuthCallback onLogin={handleLogin} />} />

                    <Route path="/mypage" element={<MyPage />} />

                    <Route path="/community" element={<CommunityPage />} />
                    <Route path="/community/board/:id" element={<BoardDetailPage />} />
                    <Route path="/community/board/new" element={<NewPostPage />} />
                </Routes>
            </div>
        </Router>
    );
};

const HeaderWrapper = ({ isLoggedIn, onLogout }) => {
    const location = useLocation();
    const showSimpleHeader = location.pathname === '/signup' || location.pathname === '/login';

    if (showSimpleHeader) {
        return <HeaderAuth />;
    } else if (isLoggedIn) {
        return <HeaderOn onLogout={onLogout} />;
    } else {
        return <Header />;
    }
};

export default App;
