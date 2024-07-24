import React from 'react';
import './CommunityPage.css';

const CommunityPage = () => {
    const posts = [
        { id: 1, title: '테스트', author: '테스트유저 1', date: '2023년 5월 15일 14:30', views: 15, comments: 3 },
        { id: 2, title: '두 번째 테스트 게시물', author: '테스트유저 2', date: '2023년 5월 15일 16:45', views: 10, comments: 1 },
        { id: 3, title: '세 번째 테스트 게시물', author: '테스트유저 3', date: '2023년 5월 16일 09:20', views: 8, comments: 0 },
        { id: 4, title: '네 번째 테스트 게시물', author: '테스트유저 4', date: '2023년 5월 16일 11:15', views: 5, comments: 2 },
        { id: 5, title: '다섯 번째 테스트 게시물', author: '테스트유저 5', date: '2023년 5월 16일 14:00', views: 3, comments: 0 },
    ];

    const pages = [1, 2, 3, 4, 5];

    return (
        <div className="container">
            <div className="community-header">
                <h1>커뮤니티</h1>
                <a href="/community/post/new" className="button">새 글 작성</a>
            </div>

            <div className="post-list">
                {posts.map(post => (
                    <div className="post-item" key={post.id}>
                        <a href={`/community/post/${post.id}`} className="post-title">{post.title}</a>
                        <span className="post-meta">{post.author} | {post.date} | 조회 {post.views} | 댓글 {post.comments}</span>
                    </div>
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
