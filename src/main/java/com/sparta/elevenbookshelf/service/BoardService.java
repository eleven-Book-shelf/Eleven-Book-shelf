package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.*;
import com.sparta.elevenbookshelf.entity.Board;
import com.sparta.elevenbookshelf.entity.Content;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.entity.post.ContentPost;
import com.sparta.elevenbookshelf.entity.post.NormalPost;
import com.sparta.elevenbookshelf.entity.post.Post;
import com.sparta.elevenbookshelf.entity.post.ReviewPost;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.BoardRepository;
import com.sparta.elevenbookshelf.repository.contentRepository.ContentRepository;
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
    private final ContentRepository contentRepository;

    //:::::::::::::::::// board //::::::::::::::::://

    @Transactional
    public BoardResponseDto createBoard(User user, BoardRequestDto req) {

        if(!isUserAdmin(user)){
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

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

        if(!isUserAdmin(user)){
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Board board = getBoard(boardId);

        board.updateTitle(req.getTitle());

        boardRepository.save(board);

        return new BoardResponseDto(board);
    }

    @Transactional
    public void deleteBoard(User user, Long boardId) {

        if(!isUserAdmin(user)){
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Board board = getBoard(boardId);

        boardRepository.delete(board);

    }

    //:::::::::::::::::// post //::::::::::::::::://

    @Transactional
    public PostResponseDto createPost(User user, Long boardId, PostRequestDto req) {

        Content content = contentRepository.findById(req.getContentId()).orElse(null);

        Post post = switch (req.getPostType()) {
            case "NORMAL" -> {
                Board board = null;

                if(boardId != null) {
                    board = getBoard(boardId);
                }

                yield NormalPost.builder()
                        .title(req.getTitle())
                        .body(req.getBody())
                        .user(user)
                        .board(board)
                        .content(content)
                        .build();
            }

            case "REVIEW" -> {
                if (req.getRating() == null) {
                    throw new IllegalStateException("리뷰 게시글에는 평점이 있어야 합니다.");
                }

                if (content == null) {
                    throw new IllegalStateException("리뷰 게시글에는 컨텐츠가 있어야 합니다.");
                }

                yield ReviewPost.builder()
                        .title(req.getTitle())
                        .body(req.getBody())
                        .user(user)
                        .board(getBoard(2L))
                        .content(content)
                        .rating(req.getRating())
                        .build();
            }

            case "CONTENT" -> {
                if (content == null) {
                    throw new IllegalStateException("컨텐츠 게시글에는 컨텐츠가 있어야 합니다.");
                }

                yield ContentPost.builder()
                        .title(content.getTitle())
                        .body(content.getDescription())
                        .user(getUser(1L))
                        .board(getBoard(1L))
                        .content(content)
                        .build();
            }
            default -> throw new BusinessException(ErrorCode.POST_INVALID);
        };

        if (post instanceof ReviewPost) {
            content.addReview((ReviewPost) post);
            contentRepository.save(content);
        }

        postRepository.save(post);

        return new PostResponseDto(post);
    }



    public PostResponseDto readPost(Long boardId, Long postId) {

        Post post = getPost(postId);

        if(!isPostBoardEqual(boardId, post)) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        return new PostResponseDto(post);
    }

    public List<PostResponseDto> readPostsByContent(Long boardId, Long contentId, long offset, int pagesize) {

        List<Post> posts = postRepository.getPostsByContent(contentId, offset, pagesize);

        return posts.stream().map(
                        PostResponseDto::new)
                .toList();
    }

    @Transactional
    public PostResponseDto updatePost(User user, Long boardId, Long postId, PostRequestDto req) {

        Post post = getPost(postId);

        if(!isPostBoardEqual(boardId, post)) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        if(!isPostUserEqual(user, post) && !isUserAdmin(user)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        post.updateBoard(getBoard(req.getBoardId()));
        post.updateTitle(req.getTitle());
        post.updateBody(req.getBody());

        postRepository.save(post);

        return new PostResponseDto(post);
    }

    @Transactional
    public void deletePost(User user, Long boardId, Long postId) {

        Post post = getPost(postId);

        if(!isPostBoardEqual(boardId, post)) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        if(!isPostUserEqual(user, post) && !isUserAdmin(user)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        postRepository.delete(post);
    }

    //::::::::::::::::::::::::// Content //:::::::::::::::::::::::://

    public ContentResponseDto createContent(ContentRequestDto req) {

        Content content = Content.builder()
                .title(req.getTitle())
                .imgurl(req.getImgurl())
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


    private boolean isUserAdmin(User user) {
        return user.getRole().toString().equals("ADMIN");
    }

    private boolean isPostUserEqual(User user, Post post) {
        return post.getUser().getId().equals(user.getId());
    }

    private boolean isPostBoardEqual(Long boardId, Post post) {
        return post.getBoard().equals(getBoard(boardId));
    }
}
