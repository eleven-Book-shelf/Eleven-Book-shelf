import React, {useEffect, useState} from 'react';
import Card from './Card/Card';
import './HomePage.css';
import axiosInstance from "../../api/axiosInstance";

const HomePage = ({onLogin}) => {
    const [ranking, setRanking] = useState([]);

    useEffect(() => {
        fetchContent();
    }, []);

    const fetchContent = async () => {
        try {
            const response = await axiosInstance.get(`/card`);
            const content = response.data.map(content => ({
                ...content,
                type: content.type === 'COMICS' ? 'webtoon' : 'webnovel'
            }));
            setRanking(content);
        } catch (error) {
            console.error("컨텐츠를 불러오는 중 오류가 발생했습니다!", error);
        }
    };

    return (
        <div className="container">
            <div>
                <h1>인기 웹툰 & 웹소설</h1>
                <div className="grid">
                    {ranking.map((ranking, index) => (
                        <a href={`/${ranking.type}/${ranking.id}`} key={index}>
                            <Card
                                img={ranking.imgUrl}
                                title={ranking.title}
                                author={ranking.author}
                                description={ranking.description}
                                genre={ranking.genre}
                                rating={ranking.rating}
                            />
                        </a>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default HomePage;
