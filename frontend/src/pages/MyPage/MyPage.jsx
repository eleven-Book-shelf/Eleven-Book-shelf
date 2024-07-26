import React, { useState, useEffect } from 'react';
import styles from './MyPage.module.css';

const MyPage = () => {
    const [profile, setProfile] = useState(null);
    const [bookmarkedWebtoons, setBookmarkedWebtoons] = useState([]);
    const [bookmarkedWebnovels, setBookmarkedWebnovels] = useState([]);
    const [recentPosts, setRecentPosts] = useState([]);

    useEffect(() => {
        // TODO: 실제 데이터 API 호출로 대체해야 합니다.
        const fetchData = async () => {
            const profileData = {
                name: '홍길동',
                joinDate: '2022년 3월 15일',
                email: 'hong@example.com'
            };

            const webtoonsData = [
                { title: '전지적 독자 시점', author: '싱숑' },
                { title: '나 혼자만 레벨업', author: '추공 / 장성락' },
                { title: '여신강림', author: '야옹이' }
            ];

            const webnovelsData = [
                { title: '전생했더니 슬라임이었던 건에 대하여', author: '후세 츠라' },
                { title: '재벌집 막내아들', author: '산경' },
                { title: '달빛조각사', author: '남희성' }
            ];

            const postsData = [
                { id: 12345, title: '전지적 독자 시점 85화 리뷰 - 놀라운 반전!' },
                { id: 12346, title: '웹소설 추천 부탁드립니다' },
                { id: 12347, title: '나 혼자만 레벨업 vs 템빨 - 어떤 작품이 더 재밌나요?' }
            ];

            setProfile(profileData);
            setBookmarkedWebtoons(webtoonsData);
            setBookmarkedWebnovels(webnovelsData);
            setRecentPosts(postsData);
        };

        fetchData();
    }, []);

    if (!profile) {
        return <div>로딩 중...</div>;
    }

    return (
        <div className={styles.container}>
            <div className={styles.profileHeader}>
                <div className={styles.profileInfo}>
                    <h1>{profile.name}</h1>
                    <p>가입일: {profile.joinDate}</p>
                    <p>이메일: {profile.email}</p>
                    <a href="/mypage/edit" className={styles.button}>프로필 수정</a>
                </div>
            </div>

            <div className={styles.section}>
                <h2>북마크한 웹툰</h2>
                <div className={styles.grid}>
                    {bookmarkedWebtoons.map((webtoon, index) => (
                        <div key={index} className={styles.card}>
                            <h3>{webtoon.title}</h3>
                            <p>작가: {webtoon.author}</p>
                        </div>
                    ))}
                </div>
            </div>

            <div className={styles.section}>
                <h2>북마크한 웹소설</h2>
                <div className={styles.grid}>
                    {bookmarkedWebnovels.map((webnovel, index) => (
                        <div key={index} className={styles.card}>
                            <h3>{webnovel.title}</h3>
                            <p>작가: {webnovel.author}</p>
                        </div>
                    ))}
                </div>
            </div>

            <div className={styles.section}>
                <h2>최근 작성한 게시글</h2>
                <ul>
                    {recentPosts.map(post => (
                        <li key={post.id}>
                            <a href={`/community/post/${post.id}`}>{post.title}</a>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

export default MyPage;
