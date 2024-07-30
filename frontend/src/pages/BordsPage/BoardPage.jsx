import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';
import './BoardPage.css';

const BoardPage = () => {
    const [posts, setPosts] = useState([]);
    const [totalPages, setTotalPages] = useState(1);
    const [boardTitle, setBoardTitle] = useState('');
    const { boardId } = useParams();
    const location = useLocation();
    const navigate = useNavigate();

    const query = new URLSearchParams(location.search);
    const page = parseInt(query.get('page') || '1', 10);
    const offset = (page - 1) * 5;
    const pagesize = 5;

    useEffect(() => {
        axiosInstance.get(`/boards/${boardId}`, {
            params: { offset, pagesize }
        })
            .then(response => {
                setPosts(response.data.posts);
                console.log(response.data.posts);
                setTotalPages(response.data.totalPages);
            })
            .catch(error => {
                console.error("There was an error fetching the posts!", error);
            });

    }, [boardId, page]);

    useEffect(() => {
        axiosInstance.get(`/boards/${boardId}/title`)
            .then(response => {
                setBoardTitle(response.data);
            })
            .catch(error => {
                console.error("There was an error fetching the board title!", error);
            });
    }, [boardId]);

    const handlePageClick = (pageNumber) => {
        navigate(`/community/board/${boardId}?page=${pageNumber}`);
    };

    return (
        <div className="container">
            <div className="community-header">
                <h1>{boardTitle}</h1>
                <a href={`/community/board/${boardId}/post/new`} className="button">새 글 작성</a>
            </div>

            <div className="board-list">
                {posts.map(post => (
                    <a
                        href={`/community/board/${boardId}/post/${post.id}`}
                        className="board-item"
                        key={post.id}
                    >
                        <div className="board-title">
                            {post.title}
                        </div>
                        <span className="board-meta">
                            {post.nickname} | {post.createdAt} | 조회 {post.viewCount}
                        </span>
                    </a>
                ))}
            </div>

            <div className="pagination">
                {[...Array(totalPages).keys()].map(pageNumber => (
                    <button
                        key={pageNumber + 1}
                        onClick={() => handlePageClick(pageNumber + 1)}
                        className={`page-link ${page === pageNumber + 1 ? 'active' : ''}`}
                    >
                        {pageNumber + 1}
                    </button>
                ))}
            </div>
        </div>
    );
}

export default BoardPage;
