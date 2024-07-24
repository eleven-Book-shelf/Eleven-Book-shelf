import React from 'react';
import {BrowserRouter as Router, Route, Routes , useLocation } from 'react-router-dom';
import Header from './Header/Header';
import HeaderAuth from './Header/HeaderAuth';

import MyPage from './pages/MyPage/MyPage';
import LoginPage from './pages/LoginPage/LoginPage';
import SignupPage from './pages/SignupPage/SignupPage';

import HomePage from './pages/HomePage/HomePage';
import WebtoonPage from './pages/WebtoonPage/WebtoonPage';
import WebnovelPage from './pages/WebnovelPage/WebnovelPage';

import CommunityPage from './pages/CommunityPage/CommunityPage';
import PostDetailPage from './pages/PostDetailPage/PostDetailPage';
import NewPostPage from './pages/NewPostPage/NewPostPage';

import './App.css';

const App = () => {
    return (
        <Router>
            <div>
                <HeaderWrapper />
                <Routes>
                    <Route path="/" element={<HomePage/>}/>
                    <Route path="/webtoon" element={<WebtoonPage/>}/>
                    <Route path="/webnovel" element={<WebnovelPage/>}/>

                    <Route path="/signup" element={<SignupPage/>}/>
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/mypage" element={<MyPage />} />

                    <Route path="/community" element={<CommunityPage/>}/>
                    <Route path="/community/post/:id" element={<PostDetailPage />} />
                    <Route path="/community/post/new" element={<NewPostPage />} />
                </Routes>
            </div>
        </Router>
    );
}

const HeaderWrapper = () => {
    const location = useLocation();
    const showSimpleHeader = location.pathname === '/signup' || location.pathname === '/login';

    return showSimpleHeader ? <HeaderAuth /> : <Header />;
}

export default App;
