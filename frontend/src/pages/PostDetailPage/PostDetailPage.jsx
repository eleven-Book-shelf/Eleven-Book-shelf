import React, {useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';
import CommentSection from '../../tool/CommentSection/CommentSection';
import './PostDetailPage.css';
import styles from "../ContentDetailPage/ContentDetailPage.module.css";

const platformColors = {
    '리디': '#03beea',
    '문피아': '#034889',
    '카카오페이지': '#FFCD00',
    '네이버': '#00C73C'
};

const PostDetailPage = ({isLoggedIn}) => {
    const {boardId, postId} = useParams();
    const [post, setPost] = useState(null);
    const [content, setContent] = useState(null);
    const [loading, setLoading] = useState(true);
    const [liked, setLiked] = useState(false);
    const [editMode, setEditMode] = useState(false);
    const [editedTitle, setEditedTitle] = useState('');
    const [editedContent, setEditedContent] = useState('');
    const [contentId, setContentId] = useState(null);
    const userId = localStorage.getItem('userId'); // 로그인된 사용자의 ID를 가져옴
    const navigate = useNavigate();

    useEffect(() => {
        const fetchPost = async () => {
            try {
                const response = await axiosInstance.get(`/boards/${boardId}/post/${postId}`);
                const postData = response.data;
                setPost(postData);
                setEditedTitle(postData.title);
                setEditedContent(postData.body);
                setContentId(postData.contentId); // contentId 설정
                setLoading(false);
                console.log(response);
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
                    headers: {Authorization: `${localStorage.getItem('Authorization')}`}
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

    useEffect(() => {
        const fetchContentDetail = async () => {
            if (contentId) {
                try {
                    const response = await axiosInstance.get(`/card/${contentId}`);
                    setContent(response.data);
                    console.log(response);

                    await axiosInstance.post(`/card/${contentId}/viewcount`);
                } catch (error) {
                    console.error('Error fetching content detail:', error);
                }
            }
        };

        fetchContentDetail();
    }, [contentId]);

    const handleLikeButtonClick = async () => {
        const headers = {Authorization: `${localStorage.getItem('Authorization')}`};
        try {
            if (liked) {
                await axiosInstance.delete(`/like/${postId}/likesPost`, {headers});
            } else {
                await axiosInstance.post(`/like/${postId}/likesPost`, {}, {headers});
            }
            setLiked(!liked);
        } catch (error) {
            console.error("There was an error updating the like status!", error);
        }
    };

    const handleDelete = async () => {
        const headers = {Authorization: `${localStorage.getItem('Authorization')}`};
        try {
            await axiosInstance.delete(`/boards/deletePost/${boardId}/post/${postId}`, {headers});
            navigate(`/community/board/${boardId}`);
        } catch (error) {
            console.error("There was an error deleting the post!", error);
        }
    };

    const handleEdit = () => {
        setEditMode(true);
    };

    const handleSave = async () => {
        const headers = {Authorization: `${localStorage.getItem('Authorization')}`};
        try {
            await axiosInstance.put(`/boards/${boardId}/post/${postId}`, {
                title: editedTitle,
                body: editedContent
            }, {headers});
            setPost({...post, title: editedTitle, body: editedContent});
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

    const platformColor = content && content.platform ? platformColors[content.platform] || '#ccc' : '#ccc';

    return (
        <div className="container">
            <div className="back-button">
                <a href={`/community/board/${boardId}`} className="post button"><h3>뒤로</h3></a>
            </div>
            <div className={`post-detail-container ${post.postType === "REVIEW" ? "review" : ""}`}>
                {post.postType === "REVIEW" && content && (
                    <div className={styles.post_img} style={{backgroundImage: `url(${content.imgUrl})`}}>
                        <div
                            className={styles.post_platform}
                            style={{backgroundColor: platformColor}}
                        >
                            {content.platform}
                        </div>
                        <div className={styles.button_container}>
                            <a href={content.url}>
                                <div className={styles.content_detail_button}>
                                    보러가기
                                </div>
                            </a>
                            <a href={`/content/${content.id}`}>
                                <div className={styles.content_detail_button}>
                                    상세페이지
                                </div>
                            </a>
                        </div>

                    </div>
                )}
                <div className="post-detail">
                    {post.postType === "REVIEW" && content && (
                        <div className="post-detail_2">
                            <h2 className={styles.contentDetailTitle}>{content.title}</h2>
                            <p className={styles.contentDetailMeta}>작가: {content.author} </p>
                            <div className={styles.postDetailContent}>
                                <p>{content.description}</p>
                            </div>
                        </div>
                    )}
                    <div className="post-detail_3">
                        {isLoggedIn && post.userId && userId !== post.userId.toString() && (
                            <button
                                className={`post-like-button ${liked ? 'liked' : ''}`}
                                onClick={handleLikeButtonClick}
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
                        {isLoggedIn && post.userId && userId === post.userId.toString() && (
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
                </div>
            </div>
            <CommentSection postId={postId}/>
        </div>
    );
}

export default PostDetailPage;
