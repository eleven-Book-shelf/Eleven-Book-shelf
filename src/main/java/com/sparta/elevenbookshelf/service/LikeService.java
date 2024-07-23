package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.LikeResponseDto;
import com.sparta.elevenbookshelf.entity.Comment;
import com.sparta.elevenbookshelf.entity.LikeComment;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.BoardRepository;
import com.sparta.elevenbookshelf.repository.CommentRepository;
import com.sparta.elevenbookshelf.repository.LikeCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final LikeCommentRepository likeCommentRepository;


    @Transactional
    public LikeResponseDto createLikeComment(Long id, User user) {

        Comment comment = commentRepository.findById(id).orElseThrow(
                ()-> new BusinessException(ErrorCode.NOTFOUND)
        );

        if (Objects.equals(comment.getUser().getId(), user.getId())) {
            throw new BusinessException(ErrorCode.LIKE_ME);
        }

        if (likeCommentRepository.findByUserIdAndCommentId(user.getId(), comment.getId()).isPresent()) {
            throw new BusinessException(ErrorCode.ALREADY_LIKE);
        }

        LikeComment likeComment = LikeComment.builder()
                .user(user)
                .comment(comment)
                .build();

        likeComment = likeCommentRepository.save(likeComment);
        return new LikeResponseDto(likeComment);
    }

    @Transactional
    public void deleteLikeComment(Long id, User user) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new BusinessException(ErrorCode.NOTFOUND)
        );

        LikeComment likeComment = likeCommentRepository.findByUserIdAndCommentId(user.getId(), comment.getId()).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_LIKE)
        );

        likeCommentRepository.delete(likeComment);
    }
}
