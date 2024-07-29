import React from 'react';
import './MyCard.css';

const MyCard = ({ title, description, author }) => {
    return (
        <div className="card">
            <h2 className="card-title">{title}</h2>
            <p className="card-description">{description}</p>
            <p className="card-author">작가: {author}</p>
        </div>
    );
}

export default MyCard;
