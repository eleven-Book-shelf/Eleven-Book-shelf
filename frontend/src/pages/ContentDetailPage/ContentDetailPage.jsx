import React from 'react';
import {useParams} from 'react-router-dom';
import CommentSection from '../CommentSection/CommentSection';
import styles from './ContentDetailPage.module.css';

const ContentDetailPage = () => {
    const {id} = useParams();
    const post = {
        id: 1,
        title: '테스트',
        author: '테스트작가 1',
        date: '2023년 5월 15일 14:30',
        content: '내용 테스트 문화산업진흥 기본법 제2조(정의) 이 법에서 사용하는 용어의 뜻은 다음과 같다.\n' +
            '3. "콘텐츠"란 부호·문자·도형·색채·음성·음향·이미지 및 영상 등(이들의 복합체를 포함한다)의 자료 또는 정보를 말한다.\n' +
            '4. "문화콘텐츠"란 문화적 요소가 체화된 콘텐츠를 말한다.\n' +
            '5. "디지털콘텐츠"란 부호·문자·도형·색채·음성·음향·이미지 및 영상 등(이들의 복합체를 포함한다)의 자료 또는 정보로서 그 보존 및 이용의 효용을 높일 수 있도록 디지털 형태로 제작하거나 처리한 것을 말한다.\n' +
            '6. "디지털문화콘텐츠"란 문화적 요소가 체화된 디지털콘텐츠를 말한다.\n' +
            '7. "멀티미디어콘텐츠"란 부호·문자·도형·색채·음성·음향·이미지 및 영상 등(이들의 복합체를 포함한다)과 관련된 미디어를 유기적으로 복합시켜 새로운 표현기능 및 저장기능을 갖게 한 콘텐츠를 말한다.\n' +
            '8. "공공문화콘텐츠"란 「공공기관의 정보공개에 관한 법률」 제2조제3호에 따른 공공기관 및 「박물관 및 미술관 진흥법」 제3조에 따른 국립 박물관, 공립 박물관, 국립 미술관, 공립 미술관 등에서 보유·제작 또는 관리하고 있는 문화콘텐츠를 말한다.\n' +
            '\n' +
            '콘텐츠산업 진흥법 제2조(정의) ① 이 법에서 사용하는 용어의 뜻은 다음과 같다.\n' +
            '1. \'콘텐츠\'란 부호·문자·도형·색채·음성·음향·이미지 및 영상 등(이들의 복합체를 포함한다)의 자료 또는 정보를 말한다.',
    };

    return (
        <div className={styles.container}>
            <div className={styles.postDetail}>
                <div className={styles.leftColumn}>
                    <img src="https://via.placeholder.com/250x400" alt="Book Cover" className={styles.bookCover}/>
                    <div className={styles.bookInfo}>
                        <h2>{post.title}</h2>
                        <p>작가: {post.author}</p>
                        <p>출판일: {post.date}</p>
                    </div>
                </div>
                <div className={styles.rightColumn}>
                    <div className={styles.topBox}>
                        <h2 className={styles.postDetailTitle}>{post.title}</h2>
                        <p className={styles.postDetailMeta}>작가: {post.author} | {post.date}</p>
                        <div className={styles.postDetailContent}>
                            <p>{post.content}</p>
                        </div>
                    </div>

                    <div className={styles.commentSection}>
                        <CommentSection postId={id} />
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ContentDetailPage;
