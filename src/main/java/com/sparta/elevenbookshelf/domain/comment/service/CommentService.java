package com.sparta.elevenbookshelf.domain.comment.service;

import com.sparta.elevenbookshelf.domain.comment.dto.CommentMapResponseDto;
import com.sparta.elevenbookshelf.domain.comment.repository.CommentRepository;
import com.sparta.elevenbookshelf.domain.comment.dto.CommentRequestDto;
import com.sparta.elevenbookshelf.domain.comment.dto.CommentResponseDto;
import com.sparta.elevenbookshelf.domain.comment.entity.Comment;
import com.sparta.elevenbookshelf.domain.post.entity.Post;
import com.sparta.elevenbookshelf.domain.post.repository.PostRepository;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import com.sparta.elevenbookshelf.domain.user.service.UserService;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final Double COMMENT_WEIGHT = 2.0;

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    //:::::::::::::::::// post //::::::::::::::::://

    /**
     * 댓글 생성 기능
     * - 게시물에 댓글을 추가합니다.
     * - 해시태그 가중치를 업데이트합니다.
     * @param postId 게시물 ID
     * @param userPrincipal 사용자 정보
     * @param commentRequestDto 댓글 요청 DTO
     */
    public void createComment(Long postId, UserPrincipal userPrincipal, CommentRequestDto commentRequestDto) {

        Post post = postRepository.findById(postId).orElse(null);

        User user = userPrincipal.getUser();
        Comment parentComment = null;

        if (commentRequestDto.getParentId() != null) {
            parentComment = commentRepository.findById(commentRequestDto.getParentId()).orElseThrow(
                    () -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND)
            );
        }

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .contents(commentRequestDto.getContents())
                .parent(parentComment)
                .build();

        Comment savedComment = commentRepository.save(comment);
    }

    /**
     * 게시물의 댓글 목록 조회 기능
     * - 해당 게시물의 댓글을 페이지 단위로 조회합니다.
     * @param postId 게시물 ID
     * @param page 페이지 오프셋
     * @param pageSize 페이지 크기
     * @return List<CommentResponseDto> 댓글 목록
     */
    public CommentMapResponseDto readCommentsPost(Long postId, int page, int pageSize) {
        Page<Comment> comments = commentRepository.findAllByPostIdAndParentIsNull(postId, page, pageSize);

        return new CommentMapResponseDto(comments.getTotalPages(),comments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList()));
    }

    /**
     * 댓글 업데이트 기능
     * - 해당 게시물의 특정 댓글을 업데이트합니다.
     * @param postId 게시물 ID
     * @param commentId 댓글 ID
     * @param userId 사용자 ID
     * @param commentRequestDto 댓글 요청 DTO
     */
    public void updateComment(Long postId, Long commentId, Long userId, CommentRequestDto commentRequestDto) {

        User user = getUser(userId);

        Comment comment = getComment(postId, commentId, user);

        comment.updateContent(commentRequestDto.getContents());

        commentRepository.save(comment);
    }

    /**
     * 댓글 삭제 기능
     * - 해당 게시물의 특정 댓글을 삭제합니다.
     * @param postId 게시물 ID
     * @param commentId 댓글 ID
     * @param userId 사용자ID
     */
    @Transactional
    public void deleteComment(Long postId, Long commentId, Long userId) {

        User user = getUser(userId);

        Comment comment = getComment(postId, commentId, user);
        if (comment.getParent() != null) {
            Comment parentcomment = comment.getParent();
            parentcomment.deleteChildren(comment);
        }

        commentRepository.delete(comment);
    }

    //::::::::::::::::::::::::// TOOL BOX  //:::::::::::::::::::::::://

    /**
     * 특정 댓글을 조회하는 내부 유틸리티 메서드
     * - 댓글을 조회하고 유효성을 검증합니다.
     * @param postId 게시물 ID
     * @param commentId 댓글 ID
     * @param user 사용자
     * @return Comment 댓글 엔티티
     */
    private Comment getComment(Long postId, Long commentId, User user) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND)
        );

        if (!comment.getPost().getId().equals(postId)) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);

        }
        return comment;
    }

    /**
     * 댓글 엔티티를 DTO로 변환하는 유틸리티 메서드
     * - 자식 댓글도 재귀적으로 변환합니다.
     * @param comment 댓글 엔티티
     * @return CommentResponseDto 댓글 응답 DTO
     */
    private CommentResponseDto convertToResponseDto(Comment comment) {
        List<CommentResponseDto> childrenDto = comment.getChildren().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return new CommentResponseDto(comment.getId(),
                                      comment.getUser().getId(),
                                      comment.getPost().getId(),
                                      comment.getUser().getNickname(),
                                      comment.getContents(), childrenDto,
                                      comment.getLikes(),
                                      comment.getCreatedAt()

        );
    }

    private User getUser(Long userId) {
        return userService.getUser(userId);
    }

}
