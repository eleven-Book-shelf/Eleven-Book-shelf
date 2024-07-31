import React, { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';
import styles from './NewPostPage.module.css';

const NewPostReviewPage = () => {
    const { contentId } = useParams();
    const navigate = useNavigate();

    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [rating, setRating] = useState(0);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (event) => {
        event.preventDefault();

        const token = localStorage.getItem('Authorization');

        const postData = {
            postType: 'REVIEW',
            title,
            body: content,
            contentId,
            rating,
        };

        try {
            const response = await axiosInstance.post(`/boards/2/post`, postData, {
                headers: {
                    Authorization: token,
                }
            });
            if (response.status === 201) {
                navigate(`/content/${contentId}`);
            }

        } catch (error) {
            console.error('Error creating post:', error);
        } finally {
            setLoading(false);  // 로딩 상태 해제
        }
    };

    return (
        <div className={styles.container}>
            <h1>리뷰 작성</h1>
            <form onSubmit={handleSubmit}>
                <div className={styles['form-group']}>
                    <label htmlFor="title">제목</label>
                    <input
                        type="text"
                        id="title"
                        name="title"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        required
                    />
                </div>
                <div className={styles['form-group']}>
                    <label htmlFor="rating">별점</label>
                    <select
                        id="rating"
                        name="rating"
                        value={rating}
                        onChange={(e) => setRating(Number(e.target.value))}
                        required
                    >
                        <option value="0">0</option>
                        <option value="1">1</option>
                        <option value="2">2</option>
                        <option value="3">3</option>
                        <option value="4">4</option>
                        <option value="5">5</option>
                    </select>
                </div>
                <div className={styles['form-group']}>
                    <label htmlFor="content">내용</label>
                    <textarea
                        id="content"
                        name="content"
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                        required
                    ></textarea>
                </div>
                <div className={styles['form-group']}>
                    <button type="submit" className={styles.button} disabled={loading}>글 작성</button>
                    <button
                        type="button"
                        className={`${styles.button} ${styles['button-secondary']}`}
                        onClick={() => navigate(`/content/${contentId}`)}
                    >
                        취소
                    </button>
                </div>
            </form>
        </div>
    );
};

export default NewPostReviewPage;
