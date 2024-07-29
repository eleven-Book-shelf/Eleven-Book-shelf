import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';
import './BoardPage.css';

const BoardPage = () => {
    const [posts, setPosts] = useState([]);
    const { boardId } = useParams();

    useEffect(() => {
        axiosInstance.get(`/boards/${boardId}`)
            .then(response => {
                setPosts(response.data);
            })
            .catch(error => {
                console.error("There was an error fetching the posts!", error);
            });
    }, [boardId]);

    const pages = [1, 2, 3, 4, 5];

    return (
        <div className="container">
            <div className="community-header">
                <h1>커뮤니티</h1>
                <a href={`/community/board/${boardId}/post/new`} className="button">새 글 작성</a>
            </div>

            <div className="board-list">
                {posts.map(board => (
                    <a
                        href={`/community/board/${boardId}/post/${board.id}`} // 새로운 URL 생성
                        className="board-item"
                        key={board.id}
                    >
                        <a href={`/community/board/${boardId}/post/${board.id}`} className="board-title">{board.title}</a>
                        <span className="board-meta">
                            {board.author} | {board.date} | 조회 {board.views} | 댓글 {board.comments}
                        </span>
                    </a>
                ))}
            </div>

            <div className="pagination">
                {pages.map(page => (
                    <a href={`/community/board/${boardId}?page=${page}`} className="page-link" key={page}>{page}</a>
                ))}
            </div>
        </div>
    );
}

export default BoardPage;
