package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.CommentRequestDto;
import com.sparta.elevenbookshelf.dto.CommentResponseDto;
import com.sparta.elevenbookshelf.entity.Board;
import com.sparta.elevenbookshelf.entity.Comment;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.BoardRepository;
import com.sparta.elevenbookshelf.repository.CommentRepository;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public CommentResponseDto createComment(Long boardId, UserPrincipal userPrincipal, CommentRequestDto commentRequestDto){
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new BusinessException(ErrorCode.BOARD_NOT_FOUND)
        );
        User user = userPrincipal.getUser();
        Comment comment = Comment.builder()
                .user(user)
                .board(board)
                .contents(commentRequestDto.getContents())
                .build();
        Comment savedComment =commentRepository.save(comment);
        return new CommentResponseDto(savedComment);
    }

    public List<CommentResponseDto> readComments(Long BoardId){

        List<Comment> comments = commentRepository.findAllByBoardId(BoardId).orElse(null);
        List<CommentResponseDto> commentResponseDto = new ArrayList<>();

        if(comments != null){
            for (Comment comment : comments){
                commentResponseDto.add(new CommentResponseDto(comment));
            }
        }

        return commentResponseDto;
    }


    public CommentResponseDto updateComment(Long boardId, Long commentId, User user, CommentRequestDto commentRequestDto) {

        Comment comment = getComment(boardId, commentId, user);

        comment.updateContent(commentRequestDto.getContents());

        commentRepository.save(comment);

        return new CommentResponseDto(comment);
    }

    public void deleteComment(Long boardId, Long commentId, User user) {

        Comment comment = getComment(boardId, commentId, user);

        commentRepository.delete(comment);
    }







    private Comment getComment(Long boardId, Long commentId, User user) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                ()-> new BusinessException(ErrorCode.COMMENT_NOT_FOUND)
        );

        if(!comment.getBoard().getId().equals(boardId)){
            throw new BusinessException(ErrorCode.BOARD_NOT_FOUND);
        }

        if(!comment.getUser().getId().equals(user.getId())){
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);

        }
        return comment;
    }


}
