import React from 'react';
import { useParams } from 'react-router-dom';
import './BoardDetailPage.css';

const BoardDetailPage = () => {
    const { id } = useParams();
    const post = {
        id: 1,
        title: '테스트',
        author: '테스트유저 1',
        date: '2023년 5월 15일 14:30',
        content: '내용 테스트',
        comments: [
            { id: 1, author: '테스트유저 2', content: '댓글 테스트입니다.', date: '2023년 5월 15일 15:10' },
            { id: 2, author: '테스트유저 3', content: '두 번째 댓글 테스트입니다.', date: '2023년 5월 15일 16:05' },
            { id: 3, author: '테스트유저 4', content: '세 번째 댓글 테스트입니다.', date: '2023년 5월 15일 17:30' },
        ]
    };

    return (
        <div className="container">
            <div className="post-detail">
                <h2 className="post-detail-title">{post.title}</h2>
                <p className="post-detail-meta">작성자: {post.author} | {post.date}</p>
                <div className="post-detail-content">
                    <p>{post.content}</p>
                </div>
            </div>

            <div className="comment-section">
                <h3>댓글 ({post.comments.length})</h3>
                {post.comments.map(comment => (
                    <div className="comment" key={comment.id}>
                        <p className="comment-author">{comment.author}</p>
                        <p className="comment-content">{comment.content}</p>
                        <p className="comment-meta">{comment.date}</p>
                    </div>
                ))}
            </div>

            <form className="comment-form" action={`/community/post/${id}/comment`} method="POST">
                <h3>댓글 작성</h3>
                <textarea name="comment" placeholder="댓글을 입력하세요..."></textarea>
                <button type="submit" className="button">댓글 작성</button>
            </form>
        </div>
    );
}

export default BoardDetailPage;
