// LikeBookmarkButtons.js
import React, { useState } from 'react';
import styles from './LikeBookmarkButtons.module.css';

const LikeBookmarkButtons = ({ postId }) => {
    const [liked, setLiked] = useState(false);
    const [bookmarked, setBookmarked] = useState(false);

    const handleLike = () => {
        setLiked(!liked);
        // 추가적인 좋아요 로직
    };

    const handleBookmark = () => {
        setBookmarked(!bookmarked);
        // 추가적인 북마크 로직
    };

    return (
        <div className={styles.buttonContainer}>
            <button className={styles.likeButton} onClick={handleLike}>
                {liked ? '💔' : '❤️'} 좋아요
            </button>
            <button className={styles.bookmarkButton} onClick={handleBookmark}>
                {bookmarked ? '🔖' : '📑'} 북마크
            </button>
        </div>
    );
};

export default LikeBookmarkButtons;
