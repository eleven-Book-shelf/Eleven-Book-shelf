import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';
import CommentSection from '../../tool/CommentSection/CommentSection';
import './PostDetailPage.css';

const PostDetailPage = ({ isLoggedIn }) => {
    const { boardId, postId } = useParams();
    const [post, setPost] = useState(null);
    const [loading, setLoading] = useState(true);
    const [liked, setLiked] = useState(false);
    const [editMode, setEditMode] = useState(false);
    const [editedTitle, setEditedTitle] = useState('');
    const [editedContent, setEditedContent] = useState('');
    const userId = localStorage.getItem('userId'); // 로그인된 사용자의 ID를 가져옴
    const navigate = useNavigate();

    useEffect(() => {
        const fetchPost = async () => {
            try {
                const response = await axiosInstance.get(`/boards/${boardId}/post/${postId}`);
                setPost(response.data);
                setEditedTitle(response.data.title);
                setEditedContent(response.data.body);
                setLoading(false);
            } catch (error) {
                console.error("There was an error fetching the post!", error);
                setLoading(false);
            }
        };

        fetchPost();
    }, [boardId, postId]);

    useEffect(() => {
        const fetchLikeStatus = async () => {
            try {
                const response = await axiosInstance.get(`/like/${postId}/likesPost`, {
                    headers: { Authorization: `${localStorage.getItem('Authorization')}` }
                });
                setLiked(response.data);
            } catch (error) {
                console.error("There was an error fetching the like status!", error);
            }
        };

        if (isLoggedIn) {
            fetchLikeStatus();
        }
    }, [postId, isLoggedIn]);

    const handleLikeButtonClick = async () => {
        const headers = { Authorization: `${localStorage.getItem('Authorization')}` };
        try {
            if (liked) {
                await axiosInstance.delete(`/like/${postId}/likesPost`, { headers });
            } else {
                await axiosInstance.post(`/like/${postId}/likesPost`, {}, { headers });
            }
            setLiked(!liked);
        } catch (error) {
            console.error("There was an error updating the like status!", error);
        }
    };

    const handleDelete = async () => {
        const headers = { Authorization: `${localStorage.getItem('Authorization')}` };
        try {
            await axiosInstance.delete(`/boards/deletePost/${boardId}/post/${postId}`, { headers });
            navigate(`/community/board/${boardId}`);
        } catch (error) {
            console.error("There was an error deleting the post!", error);
        }
    };

    const handleEdit = () => {
        setEditMode(true);
    };

    const handleSave = async () => {
        const headers = { Authorization: `${localStorage.getItem('Authorization')}` };
        try {
            await axiosInstance.put(`/boards/${boardId}/post/${postId}`, { title: editedTitle, body: editedContent }, { headers });
            setPost({ ...post, title: editedTitle, body: editedContent });
            setEditMode(false);
        } catch (error) {
            console.error("There was an error saving the post!", error);
        }
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    if (!post) {
        return <div>Post not found</div>;
    }

    return (
        <div className="container">
            <div>
                <a href={`/community/board/${boardId}`} className="post button"><h3>뒤로</h3></a>
            </div>
            <div className="post-detail">
                {isLoggedIn && userId !== post.userId.toString() && (
                    <button
                        className={`post-like-button ${liked ? 'liked' : ''}`}
                        onClick={handleLikeButtonClick}
                        style={{ position: 'absolute', top: '20px', right: '20px' }}
                    >
                        {liked ? '좋아요 취소' : '좋아요'}
                    </button>
                )}
                <div className="post-detail-header">
                    {editMode ? (
                        <input
                            type="text"
                            value={editedTitle}
                            onChange={(e) => setEditedTitle(e.target.value)}
                            className="post-edit-title"
                        />
                    ) : (
                        <h2 className="post-detail-title">{post.title}</h2>
                    )}
                </div>
                <p className="post-detail-meta">작성자: {post.nickname} | {post.createdAt}</p>
                <div className="post-detail-content">
                    {editMode ? (
                        <textarea
                            value={editedContent}
                            onChange={(e) => setEditedContent(e.target.value)}
                            className="post-edit-textarea"
                        />
                    ) : (
                        <p>{post.body}</p>
                    )}
                </div>
                {isLoggedIn && userId === post.userId.toString() && (
                    <div className="post-actions">
                        {editMode ? (
                            <button className="post-save-button" onClick={handleSave}>저장</button>
                        ) : (
                            <>
                                <button className="post-edit-button" onClick={handleEdit}>수정</button>
                                <button className="post-delete-button" onClick={handleDelete}>삭제</button>
                            </>
                        )}
                    </div>
                )}
            </div>
            <CommentSection postId={postId}/>
        </div>
    );
}

export default PostDetailPage;
