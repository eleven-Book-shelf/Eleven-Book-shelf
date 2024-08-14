package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.LikeResponseDto;
import com.sparta.elevenbookshelf.entity.*;
import com.sparta.elevenbookshelf.entity.post.Post;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.BoardRepository;
import com.sparta.elevenbookshelf.repository.LikeContentRepository;
import com.sparta.elevenbookshelf.repository.LikePostRepository;
import com.sparta.elevenbookshelf.repository.commentRepository.CommentRepository;
import com.sparta.elevenbookshelf.repository.contentRepository.ContentRepository;
import com.sparta.elevenbookshelf.repository.likeCommentRepository.LikeCommentRepository;
import com.sparta.elevenbookshelf.repository.postRepository.PostRepository;
import com.sparta.elevenbookshelf.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final CommentRepository commentRepository;
    private final CommentService commentService;

    private final LikeCommentRepository likeCommentRepository;
    private final LikeContentRepository likeContentRepository;
    private final LikePostRepository likePostRepository;

    //:::::::::::::::::// comment //::::::::::::::::://

    /**
     * 댓글 좋아요 추가 기능
     * - 특정 댓글에 좋아요를 추가
     * @param commentId 댓글 ID
     * @param userId 유저 ID
     * @return LikeResponseDto 좋아요 응답 DTO
     */
    @Transactional
    public LikeResponseDto createLikeComment(Long commentId, Long userId) {

        Comment comment = commentService.getCommentId(commentId);
        User user = getUser(userId);

        if (Objects.equals(comment.getUser().getId(), userId)) {
            throw new BusinessException(ErrorCode.LIKE_ME);
        }

        if (likeCommentRepository.findByUserIdAndCommentId(userId, comment.getId()).isPresent()) {
            throw new BusinessException(ErrorCode.ALREADY_LIKE);
        }

        LikeComment likeComment = likeCommentRepository.save(LikeComment.builder()
                                                         .user(user)
                                                         .comment(comment)
                                                         .build());

        comment.addLikes(likeCommentRepository.countByCommentId(comment.getId()));

        return new LikeResponseDto(likeComment);
    }

    /**
     * 댓글 좋아요 삭제 기능
     * - 특정 댓글의 좋아요를 삭제
     * @param commentId 댓글 ID
     * @param userId 유저 ID
     */
    @Transactional
    public void deleteLikeComment(Long commentId, Long userId) {

        Comment comment = commentService.getCommentId(commentId);


        LikeComment likeComment = likeCommentRepository.findByUserIdAndCommentId(userId, comment.getId()).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_LIKE)
        );

        likeCommentRepository.delete(likeComment);

        comment.addLikes(likeCommentRepository.countByCommentId(comment.getId()));

    }

    /**
     * 댓글 좋아요 여부 확인 기능
     * - 특정 댓글에 대해 사용자가 좋아요를 했는지 확인
     * @param commentId 댓글 ID
     * @param userId 유저 ID
     * @return Boolean 좋아요 여부
     */
    public Boolean getLikeComment(Long commentId, Long userId) {
         return likeCommentRepository.existsByCommentIdAndUserId(commentId, userId);
    }

    //:::::::::::::::::// content //::::::::::::::::://

    /**
     * 콘텐츠 좋아요 추가 기능
     * - 특정 콘텐츠에 좋아요를 추가
     * @param ContentId 콘텐츠 ID
     * @param userId 유저 ID
     */
    @Transactional
    public void createLikeContent(Long ContentId, Long userId) {

        Content content = getContent(ContentId);

        if (likeContentRepository.findByUserIdAndContentId(userId, content.getId()).isPresent()) {
            throw new BusinessException(ErrorCode.ALREADY_LIKE);
        }

        User user = getUser(userId);

        LikeContent likeContent = LikeContent.builder()
                .user(user)
                .content(content)
                .build();

        likeContentRepository.save(likeContent);
    }

    /**
     * 콘텐츠 좋아요 삭제 기능
     * - 특정 콘텐츠의 좋아요를 삭제
     * @param ContentId 콘텐츠 ID
     * @param userId 유저 ID
     */
    @Transactional
    public void deleteLikeContent(Long ContentId, Long userId) {
        Content content = getContent(ContentId);

        LikeContent likeContent = likeContentRepository.findByUserIdAndContentId(userId, content.getId()).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_LIKE)
        );

        likeContentRepository.delete(likeContent);
    }

    /**
     * 콘텐츠 좋아요 여부 확인 기능
     * - 특정 콘텐츠에 대해 사용자가 좋아요를 했는지 확인
     * @param ContentId 콘텐츠 ID
     * @param userId 유저 ID
     * @return Boolean 좋아요 여부
     */
    public Boolean getLikeContent(Long ContentId, Long userId) {
        return likeContentRepository.existsByContentIdAndUserId(ContentId, userId);
    }

    //:::::::::::::::::// Post //::::::::::::::::://

    /**
     * 게시물 좋아요 추가 기능
     * - 특정 게시물에 좋아요를 추가
     * @param postId 게시물 ID
     * @param userId 유저 ID
     */
    @Transactional
    public void createLikePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new BusinessException(ErrorCode.NOTFOUND)
        );

        User user = getUser(userId);

        if (Objects.equals(post.getUser().getId(), userId)) {
            throw new BusinessException(ErrorCode.LIKE_ME);
        }

        if (likePostRepository.existsByUserIdAndPostId(userId, post.getId())) {
            throw new BusinessException(ErrorCode.ALREADY_LIKE);
        }

        LikePost likePost = likePostRepository.save(LikePost.builder()
                                                                     .user(user)
                                                                     .post(post)
                                                                     .build());

        post.addLikes(likePostRepository.countByPostId(post.getId()));

    }

    /**
     * 게시물 좋아요 삭제 기능
     * - 특정 게시물의 좋아요를 삭제
     * @param userId 유저 ID
     */
    public void deleteLikePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new BusinessException(ErrorCode.NOTFOUND)
        );

        LikePost likePost = likePostRepository.findByUserIdAndPostId(userId, post.getId()).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_LIKE)
        );

        likePostRepository.delete(likePost);
    }

    /**
     * 게시물 좋아요 여부 확인 기능
     * - 특정 게시물에 대해 사용자가 좋아요를 했는지 확인
     * @param postId 게시물 ID
     * @param userId 유저 ID
     * @return Boolean 좋아요 여부
     */
    public Boolean getLikePost(Long postId, Long userId) {
        return likePostRepository.existsByUserIdAndPostId(userId,postId);
    }

    //:::::::::::::::::// TOOL BOX //::::::::::::::::://

    /**
     * 특정 콘텐츠 조회 유틸리티 메서드
     * - 콘텐츠를 조회하고 유효성을 검증
     * @param id 콘텐츠 ID
     * @return Content 콘텐츠 엔티티
     */
    private Content getContent(Long id) {
        return contentRepository.findById(id).orElseThrow(
                ()-> new BusinessException(ErrorCode.NOT_FOUND_CONTENT)
        );
    }

    /**
     * 특정 유저 조회 유틸리티 메서드
     * - 유저를 조회하고 유효성을 검증
     * @param userId 유저 ID
     * @return User 유저 엔티티
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                ()-> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }

}
