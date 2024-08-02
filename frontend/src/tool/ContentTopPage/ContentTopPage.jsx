import React, { useEffect, useState } from 'react';
import Card from "../../tool/Card/Card";
import axiosInstance from "../../api/axiosInstance";
import Slider from 'react-slick';
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import styles from './ContentTopPage.module.css';

const ContentTopPage = ({ type, title, genres, tabs }) => {
    const [contents, setContents] = useState([]);
    const [loading, setLoading] = useState(false);
    const [selectedTab, setSelectedTab] = useState(tabs[0]);

    useEffect(() => {
        setContents([]);
        fetchContent(selectedTab);
    }, [selectedTab]);

    const fetchContent = async (genre, subGenre, tab) => {
        if (loading) return;
        setLoading(true);
        try {
            const response = await axiosInstance.get(`/card${type}`, {
                headers: { Authorization: `${localStorage.getItem('Authorization')}` },
                params: { pagesize: 10, genre: '', tab }
            });
            const content = response.data.map(content => ({
                ...content,
            }));
            setContents(content);
        } catch (error) {
            console.error("컨텐츠를 불러오는 중 오류가 발생했습니다!", error);
        } finally {
            setLoading(false);
        }
    };

    const NextArrow = ({ className, style, onClick }) => {
        return (
            <div
                className={className}
                style={{ ...style }}
                onClick={onClick}
            />
        );
    };

    const PrevArrow = ({ className, style, onClick }) => {
        return (
            <div
                className={className}
                style={{ ...style }}
                onClick={onClick}
            />
        );
    };

    const settings = {
        dots: true,
        infinite: false,
        speed: 500,
        slidesToShow: 3,
        slidesToScroll: 3,
        initialSlide: 0,
        nextArrow: <NextArrow />,
        prevArrow: <PrevArrow />,
        responsive: [
            {
                breakpoint: 1024,
                settings: {
                    slidesToShow: 2,
                    slidesToScroll: 2,
                    infinite: true,
                    dots: true
                }
            },
            {
                breakpoint: 600,
                settings: {
                    slidesToShow: 1,
                    slidesToScroll: 1,
                    initialSlide: 1
                }
            }
        ]
    };

    return (
        <div className={styles.topContentContainer}>
            <h1>{title}</h1>
            <Slider {...settings}>
                {contents.map((content, index) => (
                    <a href={`/content/${content.id}`} key={index}>
                        <Card
                            img={content.imgUrl}
                            title={content.title}
                            platform={content.platform}
                            author={content.author}
                            description={content.description}
                            genre={content.genre}
                            rating={content.rating}
                        />
                    </a>
                ))}
            </Slider>
            {loading && <p>더 많은 컨텐츠를 불러오는 중...</p>}
        </div>
    );
}

export default ContentTopPage;
