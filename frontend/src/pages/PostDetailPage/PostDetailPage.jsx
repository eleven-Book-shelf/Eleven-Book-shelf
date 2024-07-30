import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';
import CommentSection from '../CommentSection/CommentSection'; // CommentSection 경로 확인
import './PostDetailPage.css';

const PostDetailPage = () => {
    const { boardId, postId } = useParams();
    const [post, setPost] = useState(null);
    const [loading, setLoading] = useState(true);

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

        fetchPost();
    }, [boardId, postId]);

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
                    <p>{post.body}</p>
                </div>
            </div>
            <CommentSection postId={postId} /> {/* CommentSection 사용 */}
        </div>
    );
}

export default PostDetailPage;
