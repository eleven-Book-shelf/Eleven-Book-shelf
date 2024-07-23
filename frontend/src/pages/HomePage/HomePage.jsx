import React from 'react';
import Card from './Card/Card';
import './HomePage.css';

const HomePage = () => {
    const webtoons = [
        {title: '전지적 독자 시점', description: '세계관 최강자를 향한 서사시', genre: '판타지, 액션', rating: '★★★★☆ 4.5/5'},
        {title: '나 혼자만 레벨업', description: '현대 판타지의 정점', genre: '판타지, 액션', rating: '★★★★★ 4.8/5'},
        {title: '갓 오브 하이스쿨', description: '액션과 판타지의 조화', genre: '액션, 개그', rating: '★★★★☆ 4.3/5'},
        {title: '전생했더니 슬라임이었던 건에 대하여', description: '이세계 판타지의 새로운 지평', genre: '판타지, 모험', rating: '★★★★☆ 4.6/5'}
    ];

    return (
        <div className="container">
            <div>
                <h1>인기 웹툰 & 웹소설</h1>
                <div className="grid">
                    {webtoons.map((webtoon, index) => (
                        <Card
                            key={index}
                            title={webtoon.title}
                            description={webtoon.description}
                            genre={webtoon.genre}
                            rating={webtoon.rating}
                        />
                    ))}
                </div>
            </div>
        </div>
    );
}

export default HomePage;
