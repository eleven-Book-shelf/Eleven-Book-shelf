package com.sparta.elevenbookshelf.domain.like.service;

import com.sparta.elevenbookshelf.domain.board.repository.BoardRepository;
import com.sparta.elevenbookshelf.domain.comment.dto.CommentRepository;
import com.sparta.elevenbookshelf.domain.comment.entity.Comment;
import com.sparta.elevenbookshelf.domain.content.entity.Content;
import com.sparta.elevenbookshelf.domain.content.repository.ContentRepository;
import com.sparta.elevenbookshelf.domain.like.dto.LikeResponseDto;
import com.sparta.elevenbookshelf.domain.like.entity.LikeComment;
import com.sparta.elevenbookshelf.domain.like.entity.LikeContent;
import com.sparta.elevenbookshelf.domain.like.entity.LikePost;
import com.sparta.elevenbookshelf.domain.like.repository.LikeCommentRepository;
import com.sparta.elevenbookshelf.domain.like.repository.LikeContentRepository;
import com.sparta.elevenbookshelf.domain.like.repository.LikePostRepository;
import com.sparta.elevenbookshelf.domain.post.entity.Post;
import com.sparta.elevenbookshelf.domain.post.repository.PostRepository;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import com.sparta.elevenbookshelf.domain.user.repository.UserRepository;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
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

    private final LikeCommentRepository likeCommentRepository;
    private final LikeContentRepository likeContentRepository;
    private final LikePostRepository likePostRepository;

    //:::::::::::::::::// comment //::::::::::::::::://

    @Transactional
    public LikeResponseDto createLikeComment(Long commentId, Long userId) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                ()-> new BusinessException(ErrorCode.NOTFOUND)
        );

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

    @Transactional
    public void deleteLikeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOTFOUND)
        );

        LikeComment likeComment = likeCommentRepository.findByUserIdAndCommentId(userId, comment.getId()).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_LIKE)
        );

        likeCommentRepository.delete(likeComment);

        comment.addLikes(likeCommentRepository.countByCommentId(comment.getId()));

    }

    public Boolean getLikeComment(Long commentId, Long userId) {
         return likeCommentRepository.existsByCommentIdAndUserId(commentId, userId);
    }

    //:::::::::::::::::// content //::::::::::::::::://

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

    @Transactional
    public void deleteLikeContent(Long ContentId, Long userId) {
        Content content = getContent(ContentId);

        LikeContent likeContent = likeContentRepository.findByUserIdAndContentId(userId, content.getId()).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_LIKE)
        );

        likeContentRepository.delete(likeContent);
    }

    public Boolean getLikeContent(Long ContentId, Long userId) {
        return likeContentRepository.existsByContentIdAndUserId(ContentId, userId);
    }

    //:::::::::::::::::// Post //::::::::::::::::://

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

    public void deleteLikePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new BusinessException(ErrorCode.NOTFOUND)
        );

        LikePost likePost = likePostRepository.findByUserIdAndPostId(userId, post.getId()).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_LIKE)
        );

        likePostRepository.delete(likePost);
    }

    public Boolean getLikePost(Long postId, Long userId) {
        return likePostRepository.existsByUserIdAndPostId(userId,postId);
    }

    //:::::::::::::::::// TOOL BOX //::::::::::::::::://

    private Content getContent(Long id) {
        return contentRepository.findById(id).orElseThrow(
                ()-> new BusinessException(ErrorCode.NOT_FOUND_CONTENT)
        );
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                ()-> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }

}
