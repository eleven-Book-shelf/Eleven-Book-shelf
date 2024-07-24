import React from 'react';
import GenreFilter from './GenreFilter/GenreFilter';
import RankingTabs from './RankingTabs/RankingTabs';
import WebtoonCard from './WebtoonCard/WebtoonCard';
import './WebtoonPage.css';

const WebtoonPage = () => {
    const webtoons = [
        {rank: 1, title: '신의 탑', author: 'SIU', rating: '4.9', views: '1,500,000'},
        {rank: 2, title: '여신강림', author: '야옹이', rating: '4.8', views: '1,400,000'},
        {rank: 3, title: '독립일기', author: '자까', rating: '4.7', views: '1,300,000'},
        {rank: 4, title: '갓 오브 하이스쿨', author: '박용제', rating: '4.6', views: '1,200,000'},
        {rank: 5, title: '전지적 독자 시점', author: '슬리피-C', rating: '4.9', views: '1,100,000'},
        {rank: 6, title: '나 혼자만 레벨업', author: '서유경, 추공', rating: '4.8', views: '1,000,000'}
    ];

    return (
        <div>
            <div className="container">
                <h1>웹툰</h1>
                <GenreFilter/>
                <RankingTabs/>
                <div className="webtoon-grid">
                    {webtoons.map((webtoon, index) => (
                        <WebtoonCard
                            key={index}
                            rank={webtoon.rank}
                            title={webtoon.title}
                            author={webtoon.author}
                            rating={webtoon.rating}
                            views={webtoon.views}
                        />
                    ))}
                </div>
            </div>
        </div>
    );
}

export default WebtoonPage;
