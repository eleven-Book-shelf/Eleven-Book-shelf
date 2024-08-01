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
import com.sparta.elevenbookshelf.repository.postRepository.PostRepository;
import com.sparta.elevenbookshelf.repository.userRepository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BoardService {

    private static final double CREATE_WEIGHT = 10.0;
    private static final double READ_WEIGHT = 5.0;
    private static final double READED_WEIGHT = 1.0;
    private static final double SEARCH_WEIGHT = 2.0;

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    private final HashtagService hashtagService;

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

            case "NORMAL" -> createNormalPost(user, boardId, req);

            case "REVIEW" -> createReviewPost(user, content, req);

            case "CONTENT" -> createContentPost(content);

            default -> throw new BusinessException(ErrorCode.POST_INVALID);
        };

        return new PostResponseDto(post);
    }

    //
    private NormalPost createNormalPost(User user, Long boardId, PostRequestDto req) {

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

    // userhashtag, contenthashtag 갱신 필요
    private ReviewPost createReviewPost(User user, Content content, PostRequestDto req) {
        if (req.getRating() == null) {
            throw new IllegalStateException("리뷰 게시글에는 평점이 있어야 합니다.");
        }

        if (content == null) {
            throw new IllegalStateException("리뷰 게시글에는 컨텐츠가 있어야 합니다.");
        }

        ReviewPost result = ReviewPost.builder()
                .title(req.getTitle())
                .body(req.getBody())
                .user(user)
                .board(getBoard(2L))
                .content(content)
                .rating(req.getRating())
                .build();

        Set<String> tags = hashtagService.parseHashtag(req.getPrehashtag());


        for (String tag : tags) {
            Hashtag hashtag = hashtagService.createOrUpdateHashtag(tag);

            UserHashtag userHashtag = hashtagService.createOrUpdateUserHashtag(user, hashtag, CREATE_WEIGHT);
            user.addHashtag(userHashtag);

            ContentHashtag contentHashtag = hashtagService.createOrUpdateContentHashtag(content, hashtag, CREATE_WEIGHT);
            content.addHashtag(contentHashtag);

            PostHashtag postHashtag = hashtagService.createOrUpdatePostHashtag(result, hashtag);
            result.addHashtag(postHashtag);
        }

        user.addPost(result);
        content.addReview(result);

        return postRepository.save(result);
    }

    // contenthashtag 갱신 필요
    private ContentPost createContentPost(Content content) {
        if (content == null) {
            throw new IllegalStateException("컨텐츠 게시글에는 컨텐츠가 있어야 합니다.");
        }

        ContentPost result =  ContentPost.builder()
                .title(content.getTitle())
                .body(content.getDescription())
                .user(getUser(1L))
                .board(getBoard(1L))
                .content(content)
                .build();

        Set<String> tags = hashtagService.parseHashtag(content.getGenre());
        Set<ContentHashtag> contentHashtags = new HashSet<>();

        for (String tag : tags) {
            Hashtag hashtag = hashtagService.createOrUpdateHashtag(tag);

            ContentHashtag contentHashtag = hashtagService.createOrUpdateContentHashtag(content, hashtag, 0.0);
            contentHashtags.add(contentHashtag);
            content.addHashtag(contentHashtag);
        }

        return postRepository.save(result);
    }

    // userhashtag, contenthashtag 갱신필요
    @Transactional
    public PostResponseDto readPost(User user, Long boardId, Long postId) {

        Post post = getPost(postId);

        if(!isPostBoardEqual(boardId, post)) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        if(user != null && post.getContent() != null && post.getPostHashtags() != null) {

            Content content = post.getContent();

            Set<PostHashtag> tags = post.getPostHashtags();
            Set<UserHashtag> userHashtags = new HashSet<>();
            Set<ContentHashtag> contentHashtags = new HashSet<>();

            for (PostHashtag tag : tags) {

                Hashtag hashtag = hashtagService.createOrUpdateHashtag(tag.getHashtag().getTag());

                UserHashtag userHashtag = hashtagService.createOrUpdateUserHashtag(user, hashtag, READ_WEIGHT);
                userHashtags.add(userHashtag);
                user.addHashtag(userHashtag);

                ContentHashtag contentHashtag = hashtagService.createOrUpdateContentHashtag(content, hashtag, READED_WEIGHT);
                contentHashtags.add(contentHashtag);
                content.addHashtag(contentHashtag);
            }
        }

        return new PostResponseDto(post);
    }

    // TODO : controller로 연결 필요
    // userhashtag, contenthashtag 갱신 필요
    @Transactional
    public List<PostResponseDto> readPostsByContent(User user, Long boardId, Long contentId, long offset, int pagesize) {

        List<Post> posts = postRepository.getPostsByContent(contentId, offset, pagesize);

        if(user != null) {
            Content content = getContent(contentId);

            Set<ContentHashtag> contentHashtags = content.getContentHashtags();

            for (ContentHashtag contentHashtag : contentHashtags) {

                hashtagService.createOrUpdateHashtag(contentHashtag.getHashtag().getTag());
                hashtagService.createOrUpdateUserHashtag(user, contentHashtag.getHashtag(), SEARCH_WEIGHT);
                hashtagService.createOrUpdateContentHashtag(content, contentHashtag.getHashtag(), SEARCH_WEIGHT);
            }
        }

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
                .imgUrl(req.getImgUrl())
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
