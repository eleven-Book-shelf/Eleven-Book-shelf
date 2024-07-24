package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.CommentRequestDto;
import com.sparta.elevenbookshelf.dto.CommentResponseDto;
import com.sparta.elevenbookshelf.entity.Comment;
import com.sparta.elevenbookshelf.entity.Post;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.commentRepository.CommentRepository;
import com.sparta.elevenbookshelf.repository.postRepository.PostRepository;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
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

    //:::::::::::::::::// comment //::::::::::::::::://

    public CommentResponseDto createComment(Long postId, UserPrincipal userPrincipal, CommentRequestDto commentRequestDto){

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new BusinessException(ErrorCode.POST_NOT_FOUND)
        );

        User user = userPrincipal.getUser();
        Comment parentComment = null;

        if(commentRequestDto.getParentId() != null){
            parentComment = commentRepository.findById(commentRequestDto.getParentId()).orElseThrow(
                    ()-> new BusinessException(ErrorCode.COMMENT_NOT_FOUND)
            );
        }

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .contents(commentRequestDto.getContents())
                .parent(parentComment)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return new CommentResponseDto(savedComment);
    }

    public List<CommentResponseDto> readComments(Long postId, int offset, int pageSize) {
        List<Comment> comments = commentRepository.findAllByPostIdAndParentIsNull(postId, offset, pageSize)
                .orElse(new ArrayList<>());

        return comments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }


    public CommentResponseDto updateComment(Long postId, Long commentId, User user, CommentRequestDto commentRequestDto) {

        Comment comment = getComment(postId, commentId, user);

        comment.updateContent(commentRequestDto.getContents());

        commentRepository.save(comment);

        return new CommentResponseDto(comment);
    }

    public void deleteComment(Long postId, Long commentId, User user) {

        Comment comment = getComment(postId, commentId, user);

        commentRepository.delete(comment);
    }


    //::::::::::::::::::::::::// TOOL BOX  //:::::::::::::::::::::::://

    private Comment getComment(Long postId, Long commentId, User user) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                ()-> new BusinessException(ErrorCode.COMMENT_NOT_FOUND)
        );

        if(!comment.getPost().getId().equals(postId)){
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        if(!comment.getUser().getId().equals(user.getId())){
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);

        }
        return comment;
    }

    private CommentResponseDto convertToResponseDto(Comment comment) {
        List<CommentResponseDto> childrenDto = comment.getChildren().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return new CommentResponseDto(comment.getUser().getId(), comment.getPost().getId(), comment.getContents(), childrenDto);
    }

}
