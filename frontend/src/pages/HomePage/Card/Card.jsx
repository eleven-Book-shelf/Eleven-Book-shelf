import React from 'react';
import './Card.css';

const Card = ({img, title, description, genre, rating}) => {
    return (<div className="card">
            <div className="card-img" style={{backgroundImage: `url(${img})`}}></div>
            <h2 className="card-title">{title}</h2>
            <p className="card-description">{description}</p>
            <p className="card-genre">장르: {genre}</p>
            <p className="card-rating">평점: {rating}</p>
        </div>);
}

export default Card;
