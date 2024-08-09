package com.sparta.elevenbookshelf.domain.board.service;

import com.sparta.elevenbookshelf.domain.board.dto.BoardRequestDto;
import com.sparta.elevenbookshelf.domain.board.dto.BoardResponseDto;
import com.sparta.elevenbookshelf.domain.board.entity.Board;
import com.sparta.elevenbookshelf.domain.board.repository.BoardRepository;
import com.sparta.elevenbookshelf.domain.content.dto.ContentRequestDto;
import com.sparta.elevenbookshelf.domain.content.dto.ContentResponseDto;
import com.sparta.elevenbookshelf.domain.content.entity.Content;
import com.sparta.elevenbookshelf.domain.content.repository.ContentRepository;
import com.sparta.elevenbookshelf.domain.content.repository.ContentRepositoryCustom;
import com.sparta.elevenbookshelf.domain.hashtag.entity.Hashtag;
import com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.ContentHashtag;
import com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.PostHashtag;
import com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.UserHashtag;
import com.sparta.elevenbookshelf.domain.hashtag.repository.ContentHashtagRepository;
import com.sparta.elevenbookshelf.domain.hashtag.repository.PostHashtagRepository;
import com.sparta.elevenbookshelf.domain.hashtag.repository.UserHashtagRepository;
import com.sparta.elevenbookshelf.domain.hashtag.service.HashtagService;
import com.sparta.elevenbookshelf.domain.post.dto.PostRequestDto;
import com.sparta.elevenbookshelf.domain.post.dto.PostResponseDto;
import com.sparta.elevenbookshelf.domain.post.entity.ContentPost;
import com.sparta.elevenbookshelf.domain.post.entity.NormalPost;
import com.sparta.elevenbookshelf.domain.post.entity.Post;
import com.sparta.elevenbookshelf.domain.post.entity.ReviewPost;
import com.sparta.elevenbookshelf.domain.post.repository.PostRepository;
import com.sparta.elevenbookshelf.domain.user.dto.UserResponseDto;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import com.sparta.elevenbookshelf.domain.user.repository.UserRepository;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j(topic = "BoardService")
@Service
@RequiredArgsConstructor
public class BoardService {

    private static final double CREATE_WEIGHT = 10.0;
    private static final double READ_WEIGHT = 1.0;
    private static final double READED_WEIGHT = 0.1;
    private static final double SEARCH_WEIGHT = 2.0;

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    private final HashtagService hashtagService;
    private final UserHashtagRepository userHashtagRepository;
    private final ContentHashtagRepository contentHashtagRepository;
    private final PostHashtagRepository postHashtagRepository;

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

        return postRepository.save(result);// 2번 세이브
    }

    // userhashtag, contenthashtag 갱신 필요
    @Transactional
    protected ReviewPost createReviewPost(Long userId, PostRequestDto req) {

        User user = getUser(userId);

        Content content = getContent(req.getContentId());

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

        userRepository.save(user);
        userHashtagRepository.saveAll(user.getUserHashtags());
        contentRepository.save(content);
        contentHashtagRepository.saveAll(content.getContentHashtags());
        postHashtagRepository.saveAll(result.getPostHashtags());

        return result;
    }

    // contenthashtag 갱신 필요
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
                .build();

        Set<String> tags = hashtagService.parseHashtag(content.getGenre() + content.getContentHashTag());

        for (String tag : tags) {
            Hashtag hashtag = hashtagService.createOrUpdateHashtag(tag);

            ContentHashtag contentHashtag = hashtagService.createOrUpdateContentHashtag(content, hashtag, 0.0);
            content.addHashtag(contentHashtag);
        }

        contentRepository.save(content);
        contentHashtagRepository.saveAll(content.getContentHashtags());

        return postRepository.save(result);
    }

    // userhashtag, contenthashtag 갱신필요
    @Transactional
    public PostResponseDto readPost(Long userId, Long boardId, Long postId) {

        User user = userRepository.findById(userId).orElse(null);

        Post post = getPost(postId);

        if(!isPostBoardEqual(boardId, post)) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        if(user != null && (post.getContent() != null || post.getPostHashtags() != null)) {

            log.info("in if phrase");

            Content content = post.getContent();
            log.info("content post read : " + content.getTitle());

            Set<ContentHashtag> contentHashtags = content.getContentHashtags();
            Set<PostHashtag> postHashtags = post.getPostHashtags();

            Set<Hashtag> hashtags = contentHashtags.stream().map(ContentHashtag::getHashtag).collect(Collectors.toSet());
            hashtags.addAll(postHashtags.stream().map(PostHashtag::getHashtag).collect(Collectors.toSet()));

            for (Hashtag tag : hashtags) {

                Hashtag hashtag = hashtagService.createOrUpdateHashtag(tag.getTag());

                UserHashtag userHashtag = hashtagService.createOrUpdateUserHashtag(user, hashtag, READ_WEIGHT);
                user.addHashtag(userHashtag);

                ContentHashtag contentHashtag = hashtagService.createOrUpdateContentHashtag(content, hashtag, READED_WEIGHT);
                content.addHashtag(contentHashtag);
            }

            userRepository.save(user);
            userHashtagRepository.saveAll(user.getUserHashtags());
            contentRepository.save(content);
            contentHashtagRepository.saveAll(content.getContentHashtags());
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
        return posts.stream().map(
                        PostResponseDto::new)
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

    public void deletePostAdmin(Long postId) {
        Post post = getPost(postId);
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

    public Page<PostResponseDto> getAdminPage(int page, int size, String sortBy, boolean asc) {
        Sort.Direction direction = asc ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> post = postRepository.findAll(pageable);

        return post.map(PostResponseDto::new);
    }

    //::::::::::::::::::::::::// User //:::::::::::::::::::::::://

/*    public List<PostResponseDto> readUserPost(Long userId , long offset, int pageSize) {
        List<Post> posts = postRepository.getPostsByUserId(userId, offset, pageSize);

        return posts.stream().map(
                        PostResponseDto::new)
                .toList();
    }*/

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
