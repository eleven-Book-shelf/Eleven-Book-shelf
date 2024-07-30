import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import CommentSection from '../CommentSection/CommentSection';
import LikeBookmarkButtons from './LikeBookmarkButtons/LikeBookmarkButtons';
import axiosInstance from '../../api/axiosInstance';
import styles from './ContentDetailPage.module.css';

const ContentDetailPage = () => {
    const { cardId } = useParams();
    const [post, setPost] = useState(null);

    useEffect(() => {
        const fetchContentDetail = async () => {
            try {
                const response = await axiosInstance.get(`/card/${cardId}`);
                setPost(response.data);
            } catch (error) {
                console.error('Error fetching content detail:', error);
            }
        };

        fetchContentDetail();
    }, [cardId]);

    if (!post) {
        return <div>Loading...</div>;
    }

    return (
        <div className={styles.container}>
            <div className={styles.postDetail}>
                <div className={styles.leftColumn}>
                    <div className={styles.post_img} style={{ backgroundImage: `url(${post.imgUrl})` }}></div>
                    <div className={styles.bookInfo}>
                        <h2>{post.title}</h2>
                        <p>작가: {post.author}</p>
                        <p>출판일: {post.date}</p>
                    </div>
                </div>
                <div className={styles.rightColumn}>
                    <div className={styles.topBox}>
                        <LikeBookmarkButtons postId={cardId} />
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
        </div>
    );
};

export default ContentDetailPage;
