package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.CommentRequestDto;
import com.sparta.elevenbookshelf.dto.CommentResponseDto;
import com.sparta.elevenbookshelf.entity.Comment;
import com.sparta.elevenbookshelf.entity.Content;
import com.sparta.elevenbookshelf.entity.User;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ContentRepository contentRepository;

    //:::::::::::::::::// post //::::::::::::::::://

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

    public List<CommentResponseDto> readCommentsPost(Long postId, int offset, int pageSize) {
        List<Comment> comments = commentRepository.findAllByPostIdAndParentIsNull(postId, offset, pageSize)
                .orElse(new ArrayList<>());

        return comments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public void updateComment(Long postId, Long commentId, User user, CommentRequestDto commentRequestDto) {

        Comment comment = getComment(postId, commentId, user);

        comment.updateContent(commentRequestDto.getContents());

        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, User user) {

        Comment comment = getComment(postId, commentId, user);
        if (comment.getParent() != null) {
            Comment parentcomment = comment.getParent();
            parentcomment.deleteChildren(comment);
        }

        commentRepository.delete(comment);
    }

    //:::::::::::::::::// content //::::::::::::::::://

    public void createCommentContent(Long contentId, UserPrincipal userPrincipal, CommentRequestDto commentRequestDto) {
        Content content = contentRepository.findById(contentId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_CONTENT));

        User user = userPrincipal.getUser();
        Comment parentComment = null;

        if (commentRequestDto.getParentId() != null) {
            parentComment = commentRepository.findById(commentRequestDto.getParentId()).orElseThrow(
                    () -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND)
            );
        }

        Comment comment = Comment.builder()
                .user(user)
                .content(content)
                .contents(commentRequestDto.getContents())
                .parent(parentComment)
                .build();

        Comment savedComment = commentRepository.save(comment);
    }

    public List<CommentResponseDto> readCommentsContent(Long contentId, int offset, int pageSize) {
        List<Comment> comments = commentRepository.findAllByContentIdAndParentIsNull(contentId, offset, pageSize)
                .orElse(new ArrayList<>());

        return comments.stream()
                .map(this::convertToResponseContentDto)
                .collect(Collectors.toList());
    }

    public void updateCommentContent(Long contentId, Long commentId, User user, CommentRequestDto commentRequestDto) {

        Comment comment = getCommentContent(contentId, commentId, user);

        comment.updateContent(commentRequestDto.getContents());

        commentRepository.save(comment);
    }

    public void deleteCommentContent(Long contentId, Long commentId, User user) {

        Comment comment = getCommentContent(contentId, commentId, user);
        if (comment.getParent() != null) {
            Comment parentcomment = comment.getParent();
            parentcomment.deleteChildren(comment);
        }

        commentRepository.delete(comment);

    }

    //::::::::::::::::::::::::// TOOL BOX  //:::::::::::::::::::::::://

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

    private Comment getCommentContent(Long contentId, Long commentId, User user) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND)
        );

        if (!comment.getContent().getId().equals(contentId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_CONTENT);
        }

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);

        }
        return comment;
    }

    private CommentResponseDto convertToResponseContentDto(Comment comment) {
        List<CommentResponseDto> childrenDto = comment.getChildren().stream()
                .map(this::convertToResponseContentDto)
                .collect(Collectors.toList());

        return new CommentResponseDto(comment.getId(),
                                      comment.getUser().getId(),
                                      comment.getContent().getId(),
                                      comment.getUser().getNickname(),
                                      comment.getContents(),
                                      childrenDto, comment.getLikes(),
                                      comment.getCreatedAt()
        );
    }

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
