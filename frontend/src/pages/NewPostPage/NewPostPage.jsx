import React from 'react';
import { Link } from 'react-router-dom';
import styles from './NewPostPage.module.css';

const NewPostPage = () => {
    return (
        <div className={styles.container}>
            <h1>새 글 작성</h1>
            <form action="/community/post/submit" method="POST">
                <div className={styles['form-group']}>
                    <label htmlFor="title">제목</label>
                    <input type="text" id="title" name="title" required />
                </div>
                <div className={styles['form-group']}>
                    <label htmlFor="category">카테고리</label>
                    <select id="category" name="category">
                        <option value="general">일반 토론</option>
                        <option value="review">리뷰</option>
                        <option value="recommendation">추천</option>
                        <option value="question">질문</option>
                        <option value="announcement">공지사항</option>
                    </select>
                </div>
                <div className={styles['form-group']}>
                    <label htmlFor="content">내용</label>
                    <textarea id="content" name="content" required></textarea>
                </div>
                <div className={styles['form-group']}>
                    <button type="submit" className={styles.button}>글 작성</button>
                    <Link to="/community" className={`${styles.button} ${styles['button-secondary']}`}>취소</Link>
                </div>
            </form>
        </div>
    );
}

export default NewPostPage;
