package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.*;
import com.sparta.elevenbookshelf.entity.Board;
import com.sparta.elevenbookshelf.entity.Content;
import com.sparta.elevenbookshelf.entity.Hashtag;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.entity.mappingEntity.ContentHashtag;
import com.sparta.elevenbookshelf.entity.mappingEntity.PostHashtag;
import com.sparta.elevenbookshelf.entity.mappingEntity.UserHashtag;
import com.sparta.elevenbookshelf.entity.post.ContentPost;
import com.sparta.elevenbookshelf.entity.post.NormalPost;
import com.sparta.elevenbookshelf.entity.post.Post;
import com.sparta.elevenbookshelf.entity.post.ReviewPost;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.BoardRepository;
import com.sparta.elevenbookshelf.repository.contentRepository.ContentRepository;
import com.sparta.elevenbookshelf.repository.hashtagRepository.ContentHashtagRepository;
import com.sparta.elevenbookshelf.repository.hashtagRepository.PostHashtagRepository;
import com.sparta.elevenbookshelf.repository.hashtagRepository.UserHashtagRepository;
import com.sparta.elevenbookshelf.repository.postRepository.PostRepository;
import com.sparta.elevenbookshelf.repository.userRepository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j(topic = "BoardService")
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    private final HashtagService hashtagService;

    //:::::::::::::::::// board //::::::::::::::::://

    @Transactional
    public BoardResponseDto createBoard(Long userId, BoardRequestDto req) {

        User user = getUser(userId);

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
    public BoardResponseDto updateBoard(Long userId, Long boardId, BoardRequestDto req) {

        User user = getUser(userId);

        if(!isUserAdmin(user)){
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Board board = getBoard(boardId);

        board.updateTitle(req.getTitle());

        boardRepository.save(board);

        return new BoardResponseDto(board);
    }

    @Transactional
    public void deleteBoard(Long userId, Long boardId) {

        User user = getUser(userId);

        if(!isUserAdmin(user)){
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Board board = getBoard(boardId);

        boardRepository.delete(board);

    }

    public String readBoardTitle(Long boardId) {
        return getBoard(boardId).getTitle();
    }

    //:::::::::::::::::// post //::::::::::::::::://

    public long getTotalPostsByBoard(Long boardId) {
        return postRepository.getTotalPostsByBoard(boardId);
    }

    @Transactional
    public PostResponseDto createPost(Long userId, Long boardId, PostRequestDto req) {

        Post post = switch (req.getPostType()) {

            case "NORMAL" -> createNormalPost(userId, boardId, req);

            case "REVIEW" -> createReviewPost(userId, req);

            case "CONTENT" -> createContentPost(req);

            default -> throw new BusinessException(ErrorCode.POST_INVALID);
        };

        return new PostResponseDto(post);
    }

    @Transactional
    protected NormalPost createNormalPost(Long userId, Long boardId, PostRequestDto req) {

        User user = getUser(userId);

        Board board = getBoard(boardId);

        NormalPost result =  NormalPost.builder()
                .title(req.getTitle())
                .body(req.getBody())
                .user(user)
                .board(board)
                .build();

        user.addPost(result);

        return postRepository.save(result);
    }

    @Transactional
    protected ReviewPost createReviewPost(Long userId, PostRequestDto req) {

        User user = getUser(userId);

        Content content = getContent(req.getContentId());
        ContentPost contentPost = postRepository.findByContentId(req.getContentId()).orElseThrow(
                () -> new BusinessException(ErrorCode.POST_NOT_FOUND)
        );

        log.info(req.getBody());

        ReviewPost result = ReviewPost.builder()
                .title(req.getTitle())
                .body(req.getBody())
                .user(user)
                .board(getBoard(2L))
                .content(content)
                .rating(req.getRating())
                .postHashtags(new HashSet<>())
                .build();

        postRepository.save(result);

        hashtagService.updateHashtagsByCreateReview(userId, result.getId(), content.getId(), req.getPrehashtag());

        return result;
    }

    @Transactional
    protected ContentPost createContentPost(PostRequestDto req) {

        Content content = getContent(req.getContentId());

        if (content == null) {
            throw new IllegalStateException("컨텐츠 게시글에는 컨텐츠가 있어야 합니다.");
        }

        ContentPost result =  ContentPost.builder()
                .title(content.getTitle())
                .body(content.getDescription())
                .user(getUser(1L))
                .board(getBoard(1L))
                .content(content)
                .postHashtags(new HashSet<>())
                .build();

        postRepository.save(result);

        hashtagService.updateHashtagByCreateContent(content.getId(), result.getId());

        return result;
    }

    @Transactional
    public PostResponseDto readPost(Long userId, Long boardId, Long postId) {

        User user = userRepository.findById(userId).orElse(null);

        Post post = getPost(postId);

        if(!isPostBoardEqual(boardId, post)) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        if(user != null && (post.getContent() != null || post.getPostHashtags() != null)) {

            hashtagService.updateHashtagByReadPost(user.getId(), post.getId());
        }

        post.incrementViewCount();

        return new PostResponseDto(post);
    }

    public List<PostResponseDto> readUserPost(Long userId , long offset, int pageSize) {

        List<Post> posts = postRepository.getPostsByUserId(userId, offset, pageSize);

        return posts.stream()
                .map(PostResponseDto::new)
                .toList();
    }

    public List<PostResponseDto> readPostsByContent(Long userId, Long boardId, Long contentId, long offset, int pagesize) {

        User user = userRepository.findById(userId).orElse(null);

        List<Post> posts = postRepository.getPostsByContent(contentId, offset, pagesize);

        if(user != null) {

            hashtagService.updateHashtagBySearchContent(userId, contentId);
        }

        return posts.stream()
                .map(PostResponseDto::new)
                .toList();
    }

    @Transactional
    public PostResponseDto updatePost(Long userId, Long boardId, Long postId, PostRequestDto req) {

        User user = getUser(userId);

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
    public void deletePost(Long userId, Long boardId, Long postId) {

        User user = getUser(userId);

        Post post = getPost(postId);

        if(!isPostBoardEqual(boardId, post)) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        if(!isPostUserEqual(user, post) && !isUserAdmin(user)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        postRepository.delete(post);
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

    private Content getContent(Long contentId) {

        return contentRepository.findById(contentId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOTFOUND)
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
