import React, { useState } from 'react';
import styles from './FavGenre.module.css';

const genres = [
    '학원/아카데미', '빙의물', '회귀물', '성장물', '사이다물', '피폐물',
    '전쟁물', '경영물', '차원이동물', '이세계', '생존물', '모험가',
    '디스토피아', '아포칼립스', '포스트아포칼립스', '탑등반물', '생존',
    '서바이벌', '청부업자', '배틀', '던전', '인터넷방송', '게임시스템',
    '경영', '게임', '재벌', '정령사', '마법사', '아카데미', '귀환물',
    '고인물', '이종족', '퓨전', '경찰', '라이트노벨', '빌런캐',
    '회사원', '가수', '성직자', '서포터', '요리사', '군인', '고구마',
    '육성', '힐링', '먹방/요리', '악마', '레이드', '창', '노력가',
    '해결사', '삼국지', 'BJ', '암살자', '만능', '얼굴천재', '칼잡이',
    '용병', '아이템', '후회', '연예계', '무공', '검은머리', '음악',
    '성좌', '몬스터', '축구', '소드마스터', '오컬트', '스팀펑크',
    '조력자', '요리', '농부', '크툴루', '천재', '드래곤', '사이버펑크',
    '배우', '이능력', '전쟁', '플레이어', '군주', '마왕', '환생', '계략캐',
    '사연캐', '범죄', '농사', '조선', '스포츠', '대한제국', '천마',
    '능력자', '마나', '영지', '전쟁_밀리터리', '매니저', '신무협',
    '영웅/신화', '무인', '로맨스', '복수', '마법', '하렘', '암흑가',
    '교수', '신', '착각', '게이트', '기갑', '정치', '헌터', '희생캐',
    '시스템'
];

const getRandomColor = () => {
    const letters = '0123456789ABCDEF';
    let color = '#';
    for (let i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
};

const FavGenre = ({ onSubmit, onClose }) => {
    const [selectedGenres, setSelectedGenres] = useState([]);
    const [genreColors, setGenreColors] = useState({});

    const handleGenreClick = (genre) => {
        if (selectedGenres.includes(genre)) {
            setSelectedGenres((prevSelectedGenres) =>
                prevSelectedGenres.filter((g) => g !== genre)
            );
            setGenreColors((prevGenreColors) => {
                const newColors = { ...prevGenreColors };
                delete newColors[genre];
                return newColors;
            });
        } else {
            const randomColor = getRandomColor();
            setSelectedGenres((prevSelectedGenres) => [...prevSelectedGenres, genre]);
            setGenreColors((prevGenreColors) => ({
                ...prevGenreColors,
                [genre]: randomColor,
            }));
        }
    };

    const handleSubmit = () => {
        onSubmit(selectedGenres);
        onClose(); // 모달 창 닫기
    };

    return (
        <div className={styles.modal}>
            <div className={styles.modalContent}>
                <div className={styles.genreList}>
                    {genres.map((genre) => (
                        <button
                            key={genre}
                            className={`${styles.genreItem} ${selectedGenres.includes(genre) ? styles.selected : ''}`}
                            onClick={() => handleGenreClick(genre)}
                            style={{
                                backgroundColor: selectedGenres.includes(genre)
                                    ? genreColors[genre]
                                    : '#f9f9f9',
                                borderColor: selectedGenres.includes(genre)
                                    ? genreColors[genre]
                                    : '#ccc',
                                color: selectedGenres.includes(genre) ? '#fff' : '#505050',
                            }}
                        >
                            {genre}
                        </button>
                    ))}
                </div>
                <button className={styles.button} onClick={handleSubmit}>추가</button>
            </div>
        </div>
    );
};

export default FavGenre;
