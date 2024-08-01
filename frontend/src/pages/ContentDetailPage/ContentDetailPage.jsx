import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import CommentSection from '../../tool/CommentSection/CommentSection';
import LikeBookmarkButtons from './LikeBookmarkButtons/LikeBookmarkButtons';
import axiosInstance from '../../api/axiosInstance';
import PostList from '../../tool/PostList/PostList';
import Pagination from '../../tool/Pagination/Pagination';
import styles from './ContentDetailPage.module.css';

const platformColors = {
    '리디': '#03beea',
    '문피아': '#034889',
    '카카오페이지': '#FFCD00',
    '네이버': '#00C73C'
};

const ContentDetailPage = ({ isLoggedIn }) => {
    const { cardId } = useParams();
    const navigate = useNavigate();
    const [post, setPost] = useState(null);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const itemsPerPage = 4; // 페이지당 아이템 수

    useEffect(() => {
        const fetchContentDetail = async () => {
            try {
                const response = await axiosInstance.get(`/card/${cardId}`);
                setPost(response.data);
                setTotalPages(Math.ceil(response.data.posts.length / itemsPerPage)); // 총 페이지 수 설정
                console.log(response);

                await axiosInstance.post(`/card/${cardId}/viewcount`);
            } catch (error) {
                console.error('Error fetching content detail:', error);
            }
        };

        fetchContentDetail();
    }, [cardId]);

    const handlePageClick = (page) => {
        setPage(page);
    };

    if (!post) {
        return <div>Loading...</div>;
    }

    const platformColor = platformColors[post.platform] || '#ccc';
    const boardId = 2;

    // 현재 페이지에 해당하는 게시물들만 가져오기
    const paginatedPosts = post.posts.slice((page - 1) * itemsPerPage, page * itemsPerPage);

    return (
        <div className={styles.container}>
            <div className={styles.postDetail}>
                <div className={styles.leftColumn}>
                    <div className={styles.post_img} style={{ backgroundImage: `url(${post.imgUrl})` }}>
                        <div
                            className={styles.post_platform}
                            style={{ backgroundColor: platformColor }}
                        >
                            {post.platform}
                        </div>
                    </div>
                    <div className={styles.bookInfo}>
                        <h2>{post.title}</h2>
                        <p>작가: {post.author}</p>
                        <a href={post.url}>
                            <div className='button'>
                                보러가기
                            </div>
                        </a>
                        {isLoggedIn && (
                            <button className="button" onClick={() => navigate(`/review/${cardId}/new`)}>
                                리뷰 쓰기
                            </button>
                        )}
                    </div>
                </div>
                <div className={styles.rightColumn}>
                    <div className={styles.topBox}>
                        {isLoggedIn && <LikeBookmarkButtons postId={cardId} />}
                        <h2 className={styles.postDetailTitle}>{post.title}</h2>
                        <p className={styles.postDetailMeta}>작가: {post.author} | {post.date}</p>
                        <div className={styles.postDetailContent}>
                            <p>{post.description}</p>
                        </div>
                    </div>
                    <div className='commentSection'>
                        <CommentSection postId={cardId} />
                    </div>
                </div>
            </div>

            <div className={styles.review_container}>
                <div className="community-header review_head">
                    <a href={`/community/board/2`}><h2 className="review_head" >리뷰 보드</h2></a>
                </div>

                <PostList posts={paginatedPosts} boardId={boardId} />

                <Pagination currentPage={page} totalPages={totalPages} onPageClick={handlePageClick} />
            </div>
        </div>
    );
};

export default ContentDetailPage;
