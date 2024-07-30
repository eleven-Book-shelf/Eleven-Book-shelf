package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.LikeResponseDto;
import com.sparta.elevenbookshelf.entity.*;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.BoardRepository;
import com.sparta.elevenbookshelf.repository.LikeContentRepository;
import com.sparta.elevenbookshelf.repository.commentRepository.CommentRepository;
import com.sparta.elevenbookshelf.repository.LikeCommentRepository;
import com.sparta.elevenbookshelf.repository.contentRepository.ContentRepository;
import com.sparta.elevenbookshelf.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeCommentRepository likeCommentRepository;
    private final LikeContentRepository likeContentRepository;
    private final ContentRepository contentRepository;

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

        comment.addlikes(likeCommentRepository.countByCommentId(comment.getId()));

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

        comment.addlikes(likeCommentRepository.countByCommentId(comment.getId()));

    }

    public Boolean getLikeComment(Long commentId, Long userId) {
         return likeCommentRepository.existsByCommentIdAndUserId(commentId, userId);
    }

    //:::::::::::::::::// content //::::::::::::::::://

    @Transactional
    public void createLikeContent(Long ContentId, Long userId) {

        Content content = getContent(ContentId);

        if (likeCommentRepository.findByUserIdAndCommentId(userId, content.getId()).isPresent()) {
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
