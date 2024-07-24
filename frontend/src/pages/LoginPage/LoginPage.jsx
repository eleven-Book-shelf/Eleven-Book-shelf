import React from 'react';
import './LoginPage.css';

const LoginPage = () => {
    return (
        <div className="container">
            <main className="main">
                <div className="login-form">
                    <h2>로그인</h2>
                    <p>소셜 계정으로 간편하게 로그인하세요.</p>
                    <div className="social-btn-container">
                        <a href="/auth/naver/login" className="social-btn naver-btn">네이버로 로그인</a>
                        <a href="/auth/kakao/login" className="social-btn kakao-btn">카카오로 로그인</a>
                    </div>
                    <p className="signup-link">계정이 없으신가요? <a href="/signup">회원가입</a></p>
                </div>
            </main>
            <footer className="footer">
                <p>&copy; 2023 11조 프로젝트. All rights reserved.</p>
            </footer>
        </div>
    );
}

export default LoginPage;
