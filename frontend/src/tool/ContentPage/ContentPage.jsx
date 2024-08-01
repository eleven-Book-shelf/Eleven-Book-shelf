import React, { useEffect, useState, useCallback } from 'react';
import Card from "../../tool/Card/Card";
import axiosInstance from "../../api/axiosInstance";
import './ContentPage.css';

const ContentPage = ({ type, title, genres, tabs }) => {
    const [contents, setContents] = useState([]);
    const [offset, setOffset] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [loading, setLoading] = useState(false);
    const [hasMore, setHasMore] = useState(true);
    const [selectedGenre, setSelectedGenre] = useState('전체');
    const [selectedTab, setSelectedTab] = useState(tabs[0]);

    useEffect(() => {
        setContents([]);
        setOffset(0);
        setHasMore(true);
        fetchContent(0, pageSize, selectedGenre, selectedTab);
    }, [selectedGenre, selectedTab]);

    useEffect(() => {
        if (offset !== 0) {
            fetchContent(offset, pageSize, selectedGenre, selectedTab);
        }
    }, [offset]);

    const fetchContent = async (offset, pageSize, genre, tab) => {
        if (loading) return;
        setLoading(true);
        try {
            const response = await axiosInstance.get(`/card/${type}`, {
                params: { offset, pagesize: pageSize, genre: genre === '전체' ? '' : genre, tab }
            });
            const content = response.data.map(content => ({
                ...content,
            }));
            setContents(prevContents => [...prevContents, ...content]);
            setHasMore(content.length > 0);
        } catch (error) {
            console.error("컨텐츠를 불러오는 중 오류가 발생했습니다!", error);
        } finally {
            setLoading(false);
        }
    };

    const handleScroll = useCallback(() => {
        if (window.innerHeight + window.scrollY >= document.documentElement.scrollHeight - 1 && hasMore && !loading) {
            setOffset(prevOffset => prevOffset + pageSize);
        }
    }, [loading, pageSize, hasMore]);

    useEffect(() => {
        window.addEventListener('scroll', handleScroll);
        return () => {
            window.removeEventListener('scroll', handleScroll);
        };
    }, [handleScroll]);

    const handleGenreChange = (genre) => {
        setSelectedGenre(genre);
    };

    const handleTabChange = (tab) => {
        setSelectedTab(tab);
    };

    return (
        <div className="content-container">
            <h1>{title}</h1>
            <div className="genre-filter">
                {genres.map((genre) => (
                    <button
                        key={genre}
                        className={`genre-button ${selectedGenre === genre ? 'active' : ''}`}
                        onClick={() => handleGenreChange(genre)}
                    >
                        {genre}
                    </button>
                ))}
            </div>
            <div className="ranking-tabs">
                {tabs.map((tab) => (
                    <button
                        key={tab}
                        className={`ranking-tab ${selectedTab === tab ? 'active' : ''}`}
                        onClick={() => handleTabChange(tab)}
                    >
                        {tab}
                    </button>
                ))}
            </div>
            <div className="content-grid">
                {contents.map((content, index) => (
                    <a href={`/content/${content.id}`} key={index}>
                        <Card
                            img={content.imgUrl}
                            title={content.title}
                            author={content.author}
                            description={content.description}
                            genre={content.genre}
                            rating={content.rating}
                        />
                    </a>
                ))}
            </div>
            {loading && <p>더 많은 컨텐츠를 불러오는 중...</p>}
            {!hasMore && <p>더 이상 컨텐츠가 없습니다.</p>}
        </div>
    );
}

export default ContentPage;
