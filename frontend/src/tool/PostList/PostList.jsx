import React from 'react';
import PropTypes from 'prop-types';
import './PostList.css';

const PostList = ({ posts }) => {
    const formatDateTime = (dateTime) => {
        const date = new Date(dateTime);
        const formattedDate = date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        });
        const formattedTime = date.toLocaleTimeString('ko-KR', {
            hour: '2-digit',
            minute: '2-digit'
        });
        return `${formattedDate} ${formattedTime}`;
    };

    return (
        <div className="board-list">
            {posts.map(post => (
                <a
                    href={`/community/board/${post.boardId}/post/${post.id}`}
                    className="board-item"
                    key={post.id}
                >
                    <div className="board-title">
                        <p className={`post_postType ${post.postType}`}>{post.postType}</p> {post.title}
                    </div>
                    <span className="board-meta">
                        {post.nickname} | {formatDateTime(post.createdAt)} | 조회 {post.viewCount}
                    </span>
                </a>
            ))}
        </div>
    );
};

PostList.propTypes = {
    posts: PropTypes.array.isRequired,
};

export default PostList;
