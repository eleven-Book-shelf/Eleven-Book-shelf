import React from 'react';
import './WebnovelPage.css';

const WebnovelPage = () => {
    const webnovels = [
        { rank: 1, title: '전지적 독자 시점', author: '싱숑', rating: '4.9', views: '2,500,000', chapters: 500, description: '세계관 최강자를 향한 서사시. 소설 속 주인공이 되어 폐허가 된 세상을 살아가는 이야기.' },
        { rank: 2, title: '나 혼자만 레벨업', author: '추공', rating: '4.8', views: '2,300,000', chapters: 270, description: '최약체 헌터, 진우가 각성하여 세계 최강 헌터의 자리에 오르는 이야기.' },
        { rank: 3, title: '템빨', author: '박새', rating: '4.7', views: '2,100,000', chapters: 350, description: '게임 아이템으로 현실을 바꾸는 주인공의 모험과 성장 이야기.' },
        { rank: 4, title: '달빛조각사', author: '남희성', rating: '4.6', views: '1,900,000', chapters: 600, description: '가상현실 게임 속 최고의 조각사를 꿈꾸는 주인공의 성장기.' },
        { rank: 5, title: '재벌집 막내아들', author: '산경', rating: '4.8', views: '1,700,000', chapters: 200, description: '재벌 3세의 몸에 빙의한 주인공이 재벌가를 장악해가는 이야기.' },
        { rank: 6, title: '전생했더니 슬라임이었던 건에 대하여', author: '후세 츠라', rating: '4.5', views: '1,500,000', chapters: 300, description: '슬라임으로 전생한 주인공의 판타지 세계 모험기.' }
    ];

    return (
        <div className="container">
            <h1>웹소설</h1>
            <div className="genre-filter">
                <button className="genre-button active">전체</button>
                <button className="genre-button">판타지</button>
                <button className="genre-button">로맨스</button>
                <button className="genre-button">무협</button>
                <button className="genre-button">미스터리</button>
                <button className="genre-button">SF</button>
            </div>
            <div className="ranking-tabs">
                <button className="ranking-tab active">실시간 랭킹</button>
                <button className="ranking-tab">월간 랭킹</button>
                <button className="ranking-tab">누적 랭킹</button>
            </div>
            <div className="webnovel-grid">
                {webnovels.map((webnovel, index) => (
                    <a href={`/webnovel/${webnovel.rank}`} className="webnovel-card" key={index}>
                        <span className="webnovel-rank">{webnovel.rank}</span>
                        <h3 className="webnovel-title">{webnovel.title}</h3>
                        <p className="webnovel-author">{webnovel.author}</p>
                        <span className="webnovel-rating">{webnovel.rating}</span>
                        <p className="webnovel-description">{webnovel.description}</p>
                        <div className="webnovel-stats">
                            <span>조회수: {webnovel.views}</span>
                            <span>연재: {webnovel.chapters}화</span>
                        </div>
                    </a>
                ))}
            </div>
        </div>
    );
}

export default WebnovelPage;
