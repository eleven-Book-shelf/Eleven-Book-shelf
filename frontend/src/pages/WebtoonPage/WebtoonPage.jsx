import React from 'react';
import ContentPage from '../../tool/ContentPage/ContentPage';

const WebtoonPage = () => {
    return (
        <ContentPage
            type="webtoon"
            title="웹툰"
            genres={['전체', '액션', '로맨스', '판타지', '일상', '개그']}
            tabs={['실시간 랭킹', '월간 랭킹', '누적 랭킹']}
        />
    );
}

export default WebtoonPage;
