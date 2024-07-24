import React from 'react';
import './RankingTabs.css';

const RankingTabs = () => {
    return (
        <div className="ranking-tabs">
            <button className="ranking-tab active">실시간 랭킹</button>
            <button className="ranking-tab">월간 랭킹</button>
            <button className="ranking-tab">누적 랭킹</button>
        </div>
    );
}

export default RankingTabs;
