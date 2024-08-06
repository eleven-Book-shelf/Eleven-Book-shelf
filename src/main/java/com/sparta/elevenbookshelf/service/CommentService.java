package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.CommentRequestDto;
import com.sparta.elevenbookshelf.dto.CommentResponseDto;
import com.sparta.elevenbookshelf.entity.Comment;
import com.sparta.elevenbookshelf.entity.Content;
import com.sparta.elevenbookshelf.entity.Hashtag;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.entity.mappingEntity.ContentHashtag;
import com.sparta.elevenbookshelf.entity.mappingEntity.UserHashtag;
import com.sparta.elevenbookshelf.entity.post.Post;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.commentRepository.CommentRepository;
import com.sparta.elevenbookshelf.repository.contentRepository.ContentRepository;
import com.sparta.elevenbookshelf.repository.postRepository.PostRepository;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final Double COMMENT_WEIGHT = 2.0;

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ContentRepository contentRepository;
    private final HashtagService hashtagService;

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

        if (post.getContent() != null) {
            Content content = post.getContent();

            Set<ContentHashtag> contentHashtags = content.getContentHashtags();

            for (ContentHashtag tag : contentHashtags) {

                Hashtag hashtag = hashtagService.createOrUpdateHashtag(tag.getHashtag().getTag());

                UserHashtag userHashtag = hashtagService.createOrUpdateUserHashtag(user, hashtag, COMMENT_WEIGHT);
                user.addHashtag(userHashtag);

                ContentHashtag contentHashtag = hashtagService.createOrUpdateContentHashtag(content, hashtag, COMMENT_WEIGHT);
                content.addHashtag(contentHashtag);
            }
        }
    }

    /**
     * 게시물의 댓글 목록 조회 기능
     * - 해당 게시물의 댓글을 페이지 단위로 조회합니다.
     * @param postId 게시물 ID
     * @param offset 페이지 오프셋
     * @param pageSize 페이지 크기
     * @return List<CommentResponseDto> 댓글 목록
     */
    public List<CommentResponseDto> readCommentsPost(Long postId, int offset, int pageSize) {
        List<Comment> comments = commentRepository.findAllByPostIdAndParentIsNull(postId, offset, pageSize)
                .orElse(new ArrayList<>());

        return comments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 댓글 업데이트 기능
     * - 해당 게시물의 특정 댓글을 업데이트합니다.
     * @param postId 게시물 ID
     * @param commentId 댓글 ID
     * @param user 사용자
     * @param commentRequestDto 댓글 요청 DTO
     */
    public void updateComment(Long postId, Long commentId, User user, CommentRequestDto commentRequestDto) {

        Comment comment = getComment(postId, commentId, user);

        comment.updateContent(commentRequestDto.getContents());

        commentRepository.save(comment);
    }

    /**
     * 댓글 삭제 기능
     * - 해당 게시물의 특정 댓글을 삭제합니다.
     * @param postId 게시물 ID
     * @param commentId 댓글 ID
     * @param user 사용자
     */
    @Transactional
    public void deleteComment(Long postId, Long commentId, User user) {

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

}
