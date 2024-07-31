import React from 'react';
import ContentPage from '../../../tool/ContentPage/ContentPage';

const BookmarkedWebnovels = () => {
    return (
        <ContentPage
            type="/webnovel/bookmark"
            title="북마크 웹소설"
            genres={['전체', '판타지', '로맨스', '무협', '미스터리', 'SF']}
            tabs={['실시간 랭킹', '월간 랭킹', '누적 랭킹']}
        />
    );
}

export default BookmarkedWebnovels;
