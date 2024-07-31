import React from 'react';
import styles from './MyCard.module.css';

const MyCard = ({ title, author }) => {
    return (
        <div className={styles.card}>
            <h2 className={styles.title}>{title}</h2>
            <p className={styles.author}>작가: {author}</p>
        </div>
    );
}

export default MyCard;
