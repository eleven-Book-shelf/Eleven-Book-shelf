import React, { useEffect, useState } from 'react';
import Modal from 'react-modal';
import styles from './MyPage.module.css';
import axiosInstance from '../../api/axiosInstance';

Modal.setAppElement('#root'); // 모달을 사용하는 경우, 접근성을 위해 필요

const MyPage = () => {
    const [profile, setProfile] = useState(null);
    const [bookmarkedWebtoons, setBookmarkedWebtoons] = useState([]);
    const [bookmarkedWebnovels, setBookmarkedWebnovels] = useState([]);
    const [recentPosts, setRecentPosts] = useState([]);
    const [newUsername, setNewUsername] = useState('');
    const [isModalOpen, setIsModalOpen] = useState(false);

    const fetchData = async () => {
        try {
            const profileResponse = await axiosInstance.get('/user', {
                headers: { Authorization: `${localStorage.getItem('Authorization')}` }
            });
            setProfile(profileResponse.data);
            console.log(profileResponse.data);

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

            setBookmarkedWebtoons(webtoonsData);
            setBookmarkedWebnovels(webnovelsData);
            setRecentPosts(postsData);
        } catch (error) {
            console.error('데이터 불러오기 실패:', error);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    const handleEditProfile = async () => {
        try {
            await axiosInstance.put('/user/edit', null, {
                params: { username: newUsername },
                headers: { Authorization: `${localStorage.getItem('Authorization')}` }
            });
            // 프로필 수정 후 데이터 다시 가져오기
            await fetchData();
            setIsModalOpen(false);
            setNewUsername('');
        } catch (error) {
            console.error('프로필 수정 실패:', error);
        }
    };

    if (!profile) {
        return <div>로딩 중...</div>;
    }

    return (
        <div className={styles.container}>
            <div className={styles.profileHeader}>
                <div className={styles.profileInfo}>
                    <h1 className={styles.name}> {profile.nickname}</h1>
                    <p>가입일: {profile.createdAt}</p>
                    <p>이메일: {profile.email}</p>
                    <button onClick={() => setIsModalOpen(true)} className={styles.button}>프로필 수정</button>
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

            <Modal
                isOpen={isModalOpen}
                onRequestClose={() => setIsModalOpen(false)}
                contentLabel="프로필 수정"
                className={styles.modal}
                overlayClassName={styles.overlay}
            >
                <h2>프로필 수정</h2>
                <input
                    type="text"
                    value={newUsername}
                    onChange={(e) => setNewUsername(e.target.value)}
                    placeholder="새 닉네임 입력"
                />
                <button onClick={handleEditProfile}>저장</button>
                <button onClick={() => setIsModalOpen(false)}>취소</button>
            </Modal>
        </div>
    );
};

export default MyPage;
