import React, { useEffect, useState } from 'react';
import axiosInstance from '../../api/axiosInstance';
import './CommunityPage.css';

const CommunityPage = () => {
    const [posts, setPosts] = useState([]);

    useEffect(() => {
        axiosInstance.get('/boards')
            .then(response => {
                setPosts(response.data);
            })
            .catch(error => {
                console.error("There was an error fetching the posts!", error);
            });
    }, []);

    const pages = [1, 2, 3, 4, 5];

    return (
        <div className="container">
            <div className="community-header">
                <h1>커뮤니티</h1>
            </div>

            <div className="board-list">
                {posts.map(board => (
                    <a
                        href={`/community/board/${board.id}`}
                        className="board-item"
                        key={board.id}
                    >
                        <a href={`/community/board/${board.id}`} className="board-title">{board.title}</a>
                        <span className="board-meta">
                            {board.author} | {board.date} | 조회 {board.views} | 댓글 {board.comments}
                        </span>
                    </a>
                ))}
            </div>

            <div className="pagination">
                {pages.map(page => (
                    <a href={`/community?page=${page}`} className="page-link" key={page}>{page}</a>
                ))}
            </div>
        </div>
    );
}

export default CommunityPage;
