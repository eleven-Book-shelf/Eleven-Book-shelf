import React from 'react';
import './GenreFilter.css';

const GenreFilter = () => {
    return (
        <div className="genre-filter">
            <button className="genre-button active">전체</button>
            <button className="genre-button">액션</button>
            <button className="genre-button">로맨스</button>
            <button className="genre-button">판타지</button>
            <button className="genre-button">일상</button>
            <button className="genre-button">개그</button>
        </div>
    );
}

export default GenreFilter;
