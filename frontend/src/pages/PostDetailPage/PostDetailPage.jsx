import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';
import './PostDetailPage.css'; // 일반 CSS 파일 임포트

const PostDetailPage = () => {
    const { boardId, postId } = useParams();
    const [post, setPost] = useState(null);
    const [loading, setLoading] = useState(true);
    const [commentContent, setCommentContent] = useState('');
    const [replyContents, setReplyContents] = useState({}); // 대댓글 상태를 객체 형태로 관리
    const [comments, setComments] = useState([]);
    const [activeReplyId, setActiveReplyId] = useState(null); // 현재 열려 있는 답글 폼의 ID를 저장

    useEffect(() => {
        const fetchPost = async () => {
            try {
                const response = await axiosInstance.get(`/boards/${boardId}/${postId}`);
                setPost(response.data);
                setLoading(false);
            } catch (error) {
                console.error("There was an error fetching the post!", error);
                setLoading(false);
            }
        };

        const fetchComments = async () => {
            try {
                const response = await axiosInstance.get(`/${postId}/comments`);
                const commentsWithVisibility = response.data.map(comment => ({
                    ...comment,
                    isReplyVisible: false, // 대댓글 폼의 초기 상태를 숨김으로 설정
                    children: comment.children || [] // 대댓글 배열 초기화
                }));
                setComments(commentsWithVisibility);
                console.log(commentsWithVisibility)
            } catch (error) {
                console.error("There was an error fetching the comments!", error);
            }
        };

        fetchPost();
        fetchComments();
    }, [boardId, postId]);

    const handleCommentSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axiosInstance.post(`/${postId}/comments`, {
                contents: commentContent,
                parentId: null // 기본 댓글
            }, {
                headers: { Authorization: `${localStorage.getItem('Authorization')}` }
            });
            setComments([...comments, { ...response.data, isReplyVisible: false, children: [] }]);
            setCommentContent('');
        } catch (error) {
            console.error("There was an error submitting the comment!", error);
        }
    };

    const handleReplySubmit = async (e, commentId) => {
        e.preventDefault();
        try {
            const response = await axiosInstance.post(`/${postId}/comments`, {
                contents: replyContents[commentId] || '',
                parentId: commentId
            }, {
                headers: { Authorization: `${localStorage.getItem('Authorization')}` }
            });
            setComments(comments.map(comment =>
                comment.id === commentId
                    ? {
                        ...comment,
                        children: [...(comment.children || []), response.data],
                        isReplyVisible: false
                    }
                    : comment
            ));
            setReplyContents({ ...replyContents, [commentId]: '' }); // 특정 댓글에 대한 대댓글 내용 초기화
            setActiveReplyId(null); // 답글 제출 후 폼을 닫기 위해 activeReplyId를 null로 설정
        } catch (error) {
            console.error("There was an error submitting the reply!", error);
        }
    };

    const handleReplyClick = (commentId) => {
        const newActiveReplyId = activeReplyId === commentId ? null : commentId;
        console.log(`Toggling reply form for comment ${commentId}. New activeReplyId: ${newActiveReplyId}`);
        setActiveReplyId(newActiveReplyId); // 이미 열려있는 답글 폼을 다시 클릭하면 닫기
    };

    const handleReplyContentChange = (e, commentId) => {
        setReplyContents({ ...replyContents, [commentId]: e.target.value }); // 특정 댓글에 대한 대댓글 내용 설정
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    if (!post) {
        return <div>Post not found</div>;
    }

    return (
        <div className="container">
            <div className="post-detail">
                <h2 className="post-detail-title">{post.title}</h2>
                <p className="post-detail-meta">작성자: {post.nickname} | {post.createdAt}</p>
                <div className="post-detail-content">
                    <p>{post.contents}</p>
                </div>
            </div>

            <div className="comment-section">
                <h3>댓글 ({comments.length})</h3>
                {comments.map(comment => (
                    <div className="comment" key={comment.id}>
                        <p className="comment-username">{comment.nickname}</p>
                        <p className="comment-content">{comment.contents}</p>
                        <p className="comment-meta">{comment.createdAt}</p>
                        <button
                            className="reply-button"
                            onClick={() => handleReplyClick(comment.id)}
                        >
                            답글 달기
                        </button>
                        {activeReplyId === comment.id && (
                            <form className="reply-form" onSubmit={(e) => handleReplySubmit(e, comment.id)}>
                                <textarea
                                    name="reply"
                                    placeholder="답글을 입력하세요..."
                                    value={replyContents[comment.id] || ''} // 특정 댓글에 대한 대댓글 내용
                                    onChange={(e) => handleReplyContentChange(e, comment.id)}
                                    className="reply-textarea"
                                ></textarea>
                                <button type="submit" className="button">답글 작성</button>
                            </form>
                        )}
                        {comment.children && comment.children.map(reply => (
                            <div className="reply" key={reply.id}>
                                <p className="comment-username">{reply.nickname}</p>
                                <p className="comment-content">{reply.contents}</p>
                                <p className="comment-meta">{reply.createdAt}</p>
                            </div>
                        ))}
                    </div>
                ))}
            </div>

            <form className="comment-form" onSubmit={handleCommentSubmit}>
                <h3>댓글 작성</h3>
                <textarea
                    name="comment"
                    placeholder="댓글을 입력하세요..."
                    value={commentContent}
                    onChange={(e) => setCommentContent(e.target.value)}
                    className="comment-textarea"
                ></textarea>
                <button type="submit" className="button">댓글 작성</button>
            </form>
        </div>
    );
}

export default PostDetailPage;
