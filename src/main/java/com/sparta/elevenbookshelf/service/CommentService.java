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
            hashtagService.updateHashtagByPost(user.getId(), postId, "comment");
        }
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
