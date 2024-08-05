import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes, useLocation } from 'react-router-dom';
import axiosInstance from './api/axiosInstance';
import Header from './Header/Header';
import HeaderAuth from './Header/HeaderAuth';
import HeaderOn from './Header/HeaderOn';
import FavGenre from './tool/FavGenre/FavGenre'; // 추가

import MyPage from './pages/MyPage/MyPage';
import BookmarkedWebtoons from "./pages/MyPage/bookmarkedWebtoons/bookmarkedWebtoons";
import BookmarkedWebnovels from "./pages/MyPage/bookmarkedWebnovels/bookmarkedWebnovels";

import LoginPage from './pages/LoginPage/LoginPage';
import LoginFailurePage from './pages/LoginPage/LoginFailurePage'; // 추가
import AuthCallback from './api/AuthCallback';
import SignupPage from './pages/SignupPage/SignupPage';

import HomePage from './pages/HomePage/HomePage';
import WebtoonPage from './pages/WebtoonPage/WebtoonPage';
import ContentDetailPage from './pages/ContentDetailPage/ContentDetailPage';

import WebnovelPage from './pages/WebnovelPage/WebnovelPage';
import CommunityPage from './pages/CommunityPage/CommunityPage';
import PostDetailPage from './pages/PostDetailPage/PostDetailPage';
import NewPostPage from './pages/NewPostPage/NewPostPage';
import NewPostReviewPage from './pages/NewPostPage/NewPostReviewPage';
import BoardPage from "./pages/BordsPage/BoardPage";

import './App.css';

const App = () => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [profile, setProfile] = useState(null);
    const [showFavGenreModal, setShowFavGenreModal] = useState(false); // 추가

    useEffect(() => {
        const token = localStorage.getItem('Authorization');
        if (token) {
            setIsLoggedIn(true);
        }
    }, []);

    const fetchData = async () => {
        try {
            const profileResponse = await axiosInstance.get('/api/user', {
                headers: { Authorization: `${localStorage.getItem('Authorization')}` }
            });
            setProfile(profileResponse.data);
            localStorage.setItem('userId', profileResponse.data.id);
            localStorage.setItem('favGenre', profileResponse.data.favGenre);
            console.log(profileResponse);

            if (!profileResponse.data.favGenre || profileResponse.data.favGenre.length === 0) {
                setShowFavGenreModal(true);
            }
        } catch (error) {
            console.error('데이터 불러오기 실패:', error);
        }
    };

    const handleLogin = () => {
        setIsLoggedIn(true);
        fetchData();
    };

    const handleLogout = () => {
        setIsLoggedIn(false);
        localStorage.removeItem('Authorization');
        localStorage.removeItem('userId');
        localStorage.removeItem('favGenre');
        setProfile(null);
    };

    const handleFavGenreSubmit = async (selectedGenres) => {
        try {
            await axiosInstance.post('/api/favgenres', { genre: selectedGenres }, {
                headers: { Authorization: `${localStorage.getItem('Authorization')}` }
            });
            setShowFavGenreModal(false);
            localStorage.setItem('favGenre', JSON.stringify(selectedGenres));
        } catch (error) {
            console.error('선호 장르 저장 실패:', error);
        }
    };

    return (
        <Router>
            <div>
                <HeaderWrapper isLoggedIn={isLoggedIn} onLogout={handleLogout} />
                <Routes>
                    <Route path="/" element={<HomePage onLogin={handleLogin} />} />
                    <Route path="/webtoon" element={<WebtoonPage />} />
                    <Route path="/webnovel" element={<WebnovelPage />} />
                    <Route path="/content/:cardId" element={<ContentDetailPage isLoggedIn={isLoggedIn} />} />

                    <Route path="/signup" element={<SignupPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/login/failure" element={<LoginFailurePage />} /> {/* 추가 */}
                    <Route path="/auth/callback" element={<AuthCallback onLogin={handleLogin} />} />

                    <Route path="/mypage" element={<MyPage setIsLoggedIn={setIsLoggedIn} />} />
                    <Route path="/bookmarkedWebtoons" element={<BookmarkedWebtoons />} />
                    <Route path="/bookmarkedWebnovels" element={<BookmarkedWebnovels />} />

                    <Route path="/community" element={<CommunityPage />} />
                    <Route path="/community/board/:boardId/" element={<BoardPage />} />
                    <Route path="/community/board/:boardId/post/:postId" element={<PostDetailPage isLoggedIn={isLoggedIn} />} />
                    <Route path="/review/:contentId/new" element={<NewPostReviewPage />} />
                    <Route path="/community/board/:boardId/post/new" element={<NewPostPage />} />
                </Routes>
                {showFavGenreModal && <FavGenre onSubmit={handleFavGenreSubmit} onClose={() => setShowFavGenreModal(false)} />} {/* 추가 */}
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
