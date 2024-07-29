package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.*;
import com.sparta.elevenbookshelf.entity.Board;
import com.sparta.elevenbookshelf.entity.Content;
import com.sparta.elevenbookshelf.entity.post.ContentPost;
import com.sparta.elevenbookshelf.entity.post.NormalPost;
import com.sparta.elevenbookshelf.entity.post.Post;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.entity.post.ReviewPost;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.BoardRepository;
import com.sparta.elevenbookshelf.repository.contentRepository.ContentRepositiry;
import com.sparta.elevenbookshelf.repository.postRepository.PostRepository;
import com.sparta.elevenbookshelf.repository.userRepository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    //:::::::::::::::::// board //::::::::::::::::://

    @Transactional
    public BoardResponseDto createBoard(User user, BoardRequestDto req) {

        checkUserRole(user);

        Board board = Board.builder()
                .title(req.getTitle())
                .build();

        boardRepository.save(board);

        return new BoardResponseDto(board);
    }

    public List<BoardResponseDto> readBoards() {

        List<Board> boards = boardRepository.findAll();

        return boards.stream().map(
                        BoardResponseDto::new)
                .toList();


    }

    @Transactional
    public List<PostResponseDto> readBoard(Long boardId, int offset, int pagesize) {

        List<Post> posts = postRepository.getPostsByBoard(boardId, offset, pagesize);

        return posts.stream().map(
                PostResponseDto::new)
                .toList();
    }

    @Transactional
    public BoardResponseDto updateBoard(User user, Long boardId, BoardRequestDto req) {

        checkUserRole(user);

        Board board = getBoard(boardId);

        board.updateTitle(req.getTitle());

        boardRepository.save(board);

        return new BoardResponseDto(board);
    }

    @Transactional
    public void deleteBoard(User user, Long boardId) {

        checkUserRole(user);

        Board board = getBoard(boardId);

        boardRepository.delete(board);

    }

    //:::::::::::::::::// post //::::::::::::::::://

    @Transactional
    public PostResponseDto createPost(User user, Long boardId, PostRequestDto req) {

        Board board = getBoard(boardId);

        Post post = Post.builder()
                .postType(req.getPostType())
                .title(req.getTitle())
                .content(req.getContent())
                .user(user)
                .board(board)
                .build();

        postRepository.save(post);

        return new PostResponseDto(post);
    }


    public PostResponseDto readPost(Long boardId, Long postId) {

        Post post = getPost(postId);

        checkPostBoard(boardId, post);

        return new PostResponseDto(post);
    }

    @Transactional
    public PostResponseDto updatePost(User user, Long boardId, Long postId, PostRequestDto req) {

        Post post = getPost(postId);

//        checkPostUser(user, post);
//        checkPostBoard(boardId, post);

        post.updatePostType(req.getPostType());
        post.updateBoard(getBoard(req.getBoardId()));
        post.updateTitle(req.getTitle());
        post.updateContents(req.getContents());

        postRepository.save(post);

        return new PostResponseDto(post);
    }

    @Transactional
    public void deletePost(User user, Long boardId, Long postId) {

        Post post = getPost(postId);

        checkPostUser(user, post);
        checkPostBoard(boardId, post);

        postRepository.delete(post);
    }

    //::::::::::::::::::::::::// Content //:::::::::::::::::::::::://

    public ContentResponseDto createContent(ContentRequestDto req) {

        Content content = Content.builder()
                .title(req.getTitle())
                .imgurl(req.getImgUrl())
                .description(req.getDescription())
                .author(req.getAuthor())
                .platform(req.getPlatform())
                .view(req.getView())
                .rating(req.getRating())
                .type(req.getType())
                .isEnd(req.getIsEnd())
                .build();

        contentRepository.save(content);

        return new ContentResponseDto(content);
    }


    //::::::::::::::::::::::::// TOOL BOX //:::::::::::::::::::::::://
    
    private Post getPost(Long postId) {

        return postRepository.findById(postId).orElseThrow(
                () -> new BusinessException(ErrorCode.POST_NOT_FOUND)
        );
    }

    private Board getBoard(Long boardId) {

        return boardRepository.findById(boardId).orElseThrow(
                () -> new BusinessException(ErrorCode.BOARD_NOT_FOUND)
        );
    }

    private User getUser(Long userId) {

        return userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }

    private void checkUserRole(User user) {
        if(!user.getRole().equals(User.Role.ADMIN)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }

    private void checkPostUser(User user, Post post) {
        if (!post.getUser().equals(user)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }

    private void checkPostBoard(Long boardId, Post post) {
        if (!post.getBoard().equals(getBoard(boardId))) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }
    }
}
