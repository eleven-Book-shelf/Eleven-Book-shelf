import React, {useEffect, useState} from 'react';
import './Webnovel.module.css';
import styles from "../MyPage/bookmarkedWebnovels/BookmarkedWebnovels.module.css";
import axiosInstance from "../../api/axiosInstance";
import Card from "../HomePage/Card/Card";

const WebnovelPage = () => {

    const [webnovels, setRanking] = useState([]);

    useEffect(() => {
        fetchContent();
    }, []);

    const fetchContent = async () => {
        try {
            const response = await axiosInstance.get(`/card/webnovel`);
            const content = response.data.map(content => ({
                ...content,
            }));
            setRanking(content);
        } catch (error) {
            console.error("컨텐츠를 불러오는 중 오류가 발생했습니다!", error);
        }
    };

    return (
        <div className={styles.container}>
            <h1>웹소설</h1>
            <div className={styles['genre-filter']}>
                <button className={`${styles['genre-button']} ${styles.active}`}>전체</button>
                <button className={styles['genre-button']}>판타지</button>
                <button className={styles['genre-button']}>로맨스</button>
                <button className={styles['genre-button']}>무협</button>
                <button className={styles['genre-button']}>미스터리</button>
                <button className={styles['genre-button']}>SF</button>
            </div>
            <div className={styles['ranking-tabs']}>
                <button className={`${styles['ranking-tab']} ${styles.active}`}>실시간 랭킹</button>
                <button className={styles['ranking-tab']}>월간 랭킹</button>
                <button className={styles['ranking-tab']}>누적 랭킹</button>
            </div>
            <div className={styles['webnovel-grid']}>
                {webnovels.map((webnovel, index) => (
                    <a href={`/webnovel/${webnovel.id}`} key={index}>
                        <Card
                            img={webnovel.imgUrl}
                            title={webnovel.title}
                            author={webnovel.author}
                            description={webnovel.description}
                            genre={webnovel.genre}
                            rating={webnovel.rating}
                        />
                    </a>
                ))}
            </div>
        </div>
    );
}

export default WebnovelPage;
