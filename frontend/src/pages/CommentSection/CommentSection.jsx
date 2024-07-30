import React, {useEffect, useState} from 'react';
import axiosInstance from '../../api/axiosInstance';
import './CommentSection.css';

const CommentSection = ({postId}) => {
    const [comments, setComments] = useState([]);
    const [commentContent, setCommentContent] = useState('');
    const [replyContents, setReplyContents] = useState({});
    const [activeReplyId, setActiveReplyId] = useState(null);
    const [loading, setLoading] = useState(true);
    const [editingCommentId, setEditingCommentId] = useState(null);
    const [editingContent, setEditingContent] = useState('');
    const currentUserId = localStorage.getItem('userId'); // 현재 로그인한 사용자의 ID 가져오기

    const fetchComments = async () => {
        try {
            const response = await axiosInstance.get(`/${postId}/comments`);
            const commentsWithVisibility = response.data.map(comment => ({
                ...comment,
                isReplyVisible: false,
                children: comment.children || []
            }));
            setComments(commentsWithVisibility);
            setLoading(false);
        } catch (error) {
            console.error("댓글을 불러오는 중 오류가 발생했습니다!", error);
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchComments();
    }, [postId]);

    const handleCommentSubmit = async (e) => {
        e.preventDefault();
        try {
            await axiosInstance.post(`/${postId}/comments`, {
                contents: commentContent,
                parentId: null
            }, {
                headers: {Authorization: `${localStorage.getItem('Authorization')}`}
            });
            fetchComments();
            setCommentContent('');
        } catch (error) {
            console.error("댓글을 작성하는 중 오류가 발생했습니다!", error);
        }
    };

    const handleReplySubmit = async (e, commentId, parentCommentId = null) => {
        e.preventDefault();
        try {
            await axiosInstance.post(`/${postId}/comments`, {
                contents: replyContents[commentId] || '',
                parentId: commentId
            }, {
                headers: {Authorization: `${localStorage.getItem('Authorization')}`}
            });
            fetchComments();  // 대댓글 작성 후 댓글 목록 다시 불러오기
            setReplyContents({...replyContents, [commentId]: ''});
            setActiveReplyId(null);
        } catch (error) {
            console.error("대댓글을 작성하는 중 오류가 발생했습니다!", error);
        }
    };

    const handleReplyClick = (commentId) => {
        const newActiveReplyId = activeReplyId === commentId ? null : commentId;
        setActiveReplyId(newActiveReplyId);
    };

    const handleReplyContentChange = (e, commentId) => {
        setReplyContents({...replyContents, [commentId]: e.target.value});
    };

    const handleEditClick = (commentId, content) => {
        setEditingCommentId(commentId);
        setEditingContent(content);
    };

    const handleEditSubmit = async (e, commentId) => {
        e.preventDefault();
        try {
            await axiosInstance.put(`/${postId}/comments/${commentId}`, {
                contents: editingContent
            }, {
                headers: {Authorization: `${localStorage.getItem('Authorization')}`}
            });
            fetchComments();
            setEditingCommentId(null);
            setEditingContent('');
        } catch (error) {
            console.error("댓글을 수정하는 중 오류가 발생했습니다!", error);
        }
    };

    const handleDeleteClick = async (commentId) => {
        try {
            await axiosInstance.delete(`/${postId}/comments/${commentId}`, {
                headers: {Authorization:`${localStorage.getItem('Authorization')}`}
            });
            fetchComments();
        } catch (error) {
            console.error("댓글을 삭제하는 중 오류가 발생했습니다!", error);
        }
    };

    const renderComments = (comments, parentCommentId = null) => {
        return comments.map(comment => {
            const isCurrentUser = comment.userId === parseInt(currentUserId, 10);
            return (
                <div className={`comment ${isCurrentUser ? 'current-user' : ''}`} key={comment.id}>
                    <p className="comment-username">{comment.nickname}</p>
                    {editingCommentId === comment.id ? (
                        <form onSubmit={(e) => handleEditSubmit(e, comment.id)}>
                            <textarea
                                value={editingContent}
                                onChange={(e) => setEditingContent(e.target.value)}
                                className="edit-textarea"
                            ></textarea>
                            <button type="submit" className="button">수정 완료</button>
                        </form>
                    ) : (
                        <p className="comment-content">{comment.contents}</p>
                    )}
                    <p className="comment-meta">{comment.createdAt}</p>
                    {currentUserId && (
                        <div className="buttons">
                            <button
                                className="reply-button"
                                onClick={() => handleReplyClick(comment.id)}
                            >
                                답글
                            </button>
                            {isCurrentUser && (
                                <>
                                    <button
                                        className="edit-button"
                                        onClick={() => handleEditClick(comment.id, comment.contents)}
                                    >
                                        수정
                                    </button>
                                    <button
                                        className="delete-button"
                                        onClick={() => handleDeleteClick(comment.id)}
                                    >
                                        삭제
                                    </button>
                                </>
                            )}
                        </div>
                    )}
                    {activeReplyId === comment.id && (
                        <form className="reply-form"
                              onSubmit={(e) => handleReplySubmit(e, comment.id, parentCommentId)}>
                            <textarea
                                name="reply"
                                placeholder="답글을 입력하세요..."
                                value={replyContents[comment.id] || ''}
                                onChange={(e) => handleReplyContentChange(e, comment.id)}
                                className="reply-textarea"
                            ></textarea>
                            <button type="submit" className="button left_button">답글 작성</button>
                        </form>
                    )}
                    {comment.children && comment.children.length > 0 && (
                        <div className="replies">
                            {renderComments(comment.children, comment.id)}
                        </div>
                    )}
                </div>
            );
        });
    };

    if (loading) {
        return <div>로딩 중...</div>;
    }

    return (
        <div className="comment-section">
            <h3>댓글 ({comments.length})</h3>
            {renderComments(comments)}
            {currentUserId && (
                <form className="comment-form" onSubmit={handleCommentSubmit}>
                    <h3>댓글 작성</h3>
                    <textarea
                        name="comment"
                        placeholder="댓글을 입력하세요..."
                        value={commentContent}
                        onChange={(e) => setCommentContent(e.target.value)}
                        className="comment-textarea"
                    ></textarea>
                    <button type="submit" className="button left_button">댓글 작성</button>
                </form>
            )}
        </div>
    );
};

export default CommentSection;
