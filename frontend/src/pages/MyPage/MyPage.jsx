import React from 'react';
import styles from './MyPage.module.css';

const MyPage = () => {
    return (
        <div>
            <div className={styles.container}>
                <div className={styles.profileHeader}>
                    <div className={styles.profileInfo}>
                        <h1>홍길동</h1>
                        <p>가입일: 2022년 3월 15일</p>
                        <p>이메일: hong@example.com</p>
                        <a href="/mypage/edit" className={styles.button}>프로필 수정</a>
                    </div>
                </div>

                <div className={styles.section}>
                    <h2>북마크한 웹툰</h2>
                    <div className={styles.grid}>
                        <div className={styles.card}>
                            <h3>전지적 독자 시점</h3>
                            <p>작가: 싱숑</p>
                        </div>
                        <div className={styles.card}>
                            <h3>나 혼자만 레벨업</h3>
                            <p>작가: 추공 / 장성락</p>
                        </div>
                        <div className={styles.card}>
                            <h3>여신강림</h3>
                            <p>작가: 야옹이</p>
                        </div>
                    </div>
                </div>

                <div className={styles.section}>
                    <h2>북마크한 웹소설</h2>
                    <div className={styles.grid}>
                        <div className={styles.card}>
                            <h3>전생했더니 슬라임이었던 건에 대하여</h3>
                            <p>작가: 후세 츠라</p>
                        </div>
                        <div className={styles.card}>
                            <h3>재벌집 막내아들</h3>
                            <p>작가: 산경</p>
                        </div>
                        <div className={styles.card}>
                            <h3>달빛조각사</h3>
                            <p>작가: 남희성</p>
                        </div>
                    </div>
                </div>

                <div className={styles.section}>
                    <h2>최근 작성한 게시글</h2>
                    <ul>
                        <li><a href="/community/post/12345">전지적 독자 시점 85화 리뷰 - 놀라운 반전!</a></li>
                        <li><a href="/community/post/12346">웹소설 추천 부탁드립니다</a></li>
                        <li><a href="/community/post/12347">나 혼자만 레벨업 vs 템빨 - 어떤 작품이 더 재밌나요?</a></li>
                    </ul>
                </div>
            </div>
        </div>
    );
};

export default MyPage;
