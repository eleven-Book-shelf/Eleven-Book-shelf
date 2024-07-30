import React from 'react';
import GenreFilter from '../../WebtoonPage/GenreFilter/GenreFilter';
import RankingTabs from '../../WebtoonPage/RankingTabs/RankingTabs';
import Card from "../../HomePage/Card/Card";

const BookMarkedWebtoons = () => {
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
                <h1>북마크 웹툰</h1>
                <GenreFilter/>
                <RankingTabs/>
                <div className="webtoon-grid">
                    {webtoons.map((webtoon, index) => (
                        <a href={`/webtoon/${webtoon.rank}`} key={index}>
                            <Card
                                img={webtoon.imgUrl}
                                title={webtoon.title}
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

export default BookMarkedWebtoons;
