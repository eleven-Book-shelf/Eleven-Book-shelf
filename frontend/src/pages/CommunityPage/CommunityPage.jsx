import React, { useEffect, useState } from 'react';
import axiosInstance from '../../api/axiosInstance';
import styles from './CommunityPage.module.css';

const CommunityPage = () => {
    const [boards, setBoards] = useState([]);

    useEffect(() => {
        axiosInstance.get('/boards')
            .then(response => {
                const boardsWithPosts = response.data.map(board => {
                    return axiosInstance.get(`/boards/${board.id}`, {
                        params: { offset: 0, pagesize: 3 }
                    })
                        .then(postResponse => ({
                            ...board,
                            posts: postResponse.data.posts
                        }))
                        .catch(error => {
                            console.error(`There was an error fetching the posts for board ${board.id}!`, error);
                            return { ...board, posts: [] };
                        });
                });
                Promise.all(boardsWithPosts).then(setBoards);
            })
            .catch(error => {
                console.error("There was an error fetching the boards!", error);
            });
    }, []);

    return (
        <div className={styles.container}>
            <div className={styles.communityHeader}>
                <h1>커뮤니티</h1>
            </div>

            <div className={styles.boardList}>
                {boards.map(board => (
                    <div className={styles.boardItem} key={board.id}>
                        <a href={`/community/board/${board.id}`} className={styles.boardLink}>
                            <h2 className={styles.boardTitle}>{board.title}</h2>
                        </a>
                        <div className={styles.boardPosts}>
                            {board.posts && board.posts.length > 0 ? (
                                board.posts.map(post => (
                                    <a href={`/community/board/${board.id}/post/${post.id}`} key={post.id} className={styles.boardPost}>
                                        <div className={styles.postTitle}>{post.title}</div>
                                        <div className={styles.postMeta}>
                                            <span>작성자: {post.nickname}</span>
                                            <span>조회수: {post.viewCount}</span>
                                        </div>
                                    </a>
                                ))
                            ) : (
                                <p>No posts available</p>
                            )}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default CommunityPage;
