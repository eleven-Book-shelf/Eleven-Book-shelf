import React, { useEffect, useState } from 'react';
import Modal from 'react-modal';
import styles from './MyPage.module.css';
import axiosInstance from '../../api/axiosInstance';
import CardGrid from './CardGrid';
import ProfileHeader from './ProfileHeader';
import PostList from '../../tool/PostList/PostList';

Modal.setAppElement('#root');

const MyPage = ({ setIsLoggedIn }) => {
    const [profile, setProfile] = useState(null);
    const [bookmarkedWebtoons, setBookmarkedWebtoons] = useState([]);
    const [bookmarkedWebnovels, setBookmarkedWebnovels] = useState([]);
    const [recentPosts, setRecentPosts] = useState([]);
    const [newUsername, setNewUsername] = useState('');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [totalPages, setTotalPages] = useState(1);
    const page = 1;
    const offset = (page - 1) * 4;
    const pagesize = 4;

    const fetchData = async () => {
        try {
            const profileResponse = await axiosInstance.get('/user', {
                headers: { Authorization: `${localStorage.getItem('Authorization')}` }
            });
            setProfile(profileResponse.data);

            const fetchWebtoonsData = async () => {
                try {
                    const response = await axiosInstance.get(`/card/webtoon/bookmark`, {
                        headers: { Authorization: `${localStorage.getItem('Authorization')}` },
                        params: { offset, pagesize }
                    });
                    return response.data;
                } catch (error) {
                    console.error("웹툰 데이터를 불러오는 중 오류가 발생했습니다!", error);
                    return [];
                }
            };

            const fetchWebnovelsData = async () => {
                try {
                    const response = await axiosInstance.get(`/card/webnovel/bookmark`, {
                        headers: { Authorization: `${localStorage.getItem('Authorization')}` },
                        params: { offset, pagesize }
                    });
                    return response.data;
                } catch (error) {
                    console.error("웹소설 데이터를 불러오는 중 오류가 발생했습니다!", error);
                    return [];
                }
            };

            const webtoonsData = await fetchWebtoonsData();
            const webnovelsData = await fetchWebnovelsData();

            const postsResponse = await axiosInstance.get(`/boards/user/posts`, {
                params: { offset, pagesize },
                headers: { Authorization: `${localStorage.getItem('Authorization')}` }
            });

            setBookmarkedWebtoons(Array.isArray(webtoonsData) ? webtoonsData : []);
            setBookmarkedWebnovels(Array.isArray(webnovelsData) ? webnovelsData : []);
            setRecentPosts(Array.isArray(postsResponse.data.posts) ? postsResponse.data.posts : []);
            setTotalPages(postsResponse.data.totalPages);

        } catch (error) {
            console.error('데이터 불러오기 실패:', error);
            setBookmarkedWebtoons([]);
            setBookmarkedWebnovels([]);
            setRecentPosts([]);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    const handleEditProfile = async () => {
        if (window.confirm('정말로 수정하시겠습니까?')) {
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
        }
    };

    const handleDeleteAccount = async () => {
        if (window.confirm('정말로 탈퇴하시겠습니까?')) {
            try {
                await axiosInstance.delete('/user/signout', {
                    headers: { Authorization: `${localStorage.getItem('Authorization')}` }
                });
                // 탈퇴 후 로그아웃 및 리디렉션
                localStorage.removeItem('Authorization');
                localStorage.removeItem('userId');
                setProfile(null);
                setIsLoggedIn(false);
                window.location.href = '/';
            } catch (error) {
                console.error('회원 탈퇴 실패:', error);
            }
        }
    };

    if (!profile) {
        return <div>로딩 중...</div>;
    }

    return (
        <div className={styles.container}>
            <ProfileHeader profile={profile} setIsModalOpen={setIsModalOpen} />

            <div className={styles.section}>
                <h2 className={styles.sectionTitle}>
                    북마크한 웹툰 <a href="/bookmarkedWebtoons" className={styles.more_btu}>더보기</a>
                </h2>
                <CardGrid items={bookmarkedWebtoons} />
            </div>

            <div className={styles.section}>
                <h2 className={styles.sectionTitle}>
                    북마크한 웹소설 <a href="/bookmarkedWebnovels" className={styles.more_btu}>더보기</a>
                </h2>
                <CardGrid items={bookmarkedWebnovels} />
            </div>

            <div className={styles.section}>
                <h2>최근 작성한 게시글</h2>
                <PostList posts={recentPosts} />
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
                <button onClick={handleDeleteAccount} className={styles.deleteButton}>회원 탈퇴</button>
            </Modal>
        </div>
    );
};

export default MyPage;
