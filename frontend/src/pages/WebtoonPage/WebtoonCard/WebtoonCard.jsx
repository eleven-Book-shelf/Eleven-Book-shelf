import React from 'react';
import './WebtoonCard.css';

const WebtoonCard = ({ rank, title, author, rating, views }) => {
    return (
        <div className="webtoon-card">
            <span className="webtoon-rank">{rank}</span>
            <h3 className="webtoon-title">{title}</h3>
            <p className="webtoon-author">{author}</p>
            <span className="webtoon-rating">{rating}</span>
            <p>조회수: {views}</p>
        </div>
    );
}

export default WebtoonCard;
