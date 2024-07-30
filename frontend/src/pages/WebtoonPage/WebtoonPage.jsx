import React, {useEffect, useState} from 'react';
import GenreFilter from './GenreFilter/GenreFilter';
import RankingTabs from './RankingTabs/RankingTabs';
import Card from "../HomePage/Card/Card";
import axiosInstance from "../../api/axiosInstance";
import './WebtoonPage.css';

const WebtoonPage = () => {
    const [webtoons, setRanking] = useState([]);

    useEffect(() => {
        fetchContent();
    }, []);

    const fetchContent = async () => {
        try {
            const response = await axiosInstance.get(`/card/webtoon`);
            const content = response.data.map(content => ({
                ...content,
            }));
            setRanking(content);
        } catch (error) {
            console.error("컨텐츠를 불러오는 중 오류가 발생했습니다!", error);
        }
    };

    return (
        <div>
            <div className="container">
                <h1>웹툰</h1>
                <GenreFilter/>
                <RankingTabs/>
                <div className="webtoon-grid">
                    {webtoons.map((webtoon, index) => (
                        <a href={`/webtoon/${webtoon.id}`} key={index}>
                            <Card
                                img={webtoon.imgUrl}
                                title={webtoon.title}
                                author={webtoon.author}
                                description={webtoon.description}
                                genre={webtoon.genre}
                                rating={webtoon.rating}
                            />
                        </a>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default WebtoonPage;
