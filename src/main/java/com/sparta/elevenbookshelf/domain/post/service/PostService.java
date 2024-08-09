package com.sparta.elevenbookshelf.domain.post.service;

import com.sparta.elevenbookshelf.domain.content.entity.Content;
import com.sparta.elevenbookshelf.domain.content.service.ContentService;
import com.sparta.elevenbookshelf.domain.post.dto.PostRequestDto;
import com.sparta.elevenbookshelf.domain.post.dto.PostResponseDto;
import com.sparta.elevenbookshelf.domain.post.entity.Post;
import com.sparta.elevenbookshelf.domain.post.repository.PostRepository;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import com.sparta.elevenbookshelf.domain.user.service.UserService;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final UserService userService;
    private final ContentService contentService;

    //:::::::::::::::::// create //::::::::::::::::://

    @Transactional
    public PostResponseDto createNormalPost(Long userId, PostRequestDto req) {

        User user = getUser(userId);

        Post post =  Post.builder()
                .title(req.getTitle())
                .body(req.getBody())
                .user(user)
                .build();

        user.addPost(post);

        postRepository.saveAndFlush(post);

        return new PostResponseDto(post);
    }

    @Transactional
    public PostResponseDto createReviewPost(Long userId, PostRequestDto req) {

        User user = getUser(userId);

        Content content = getContent(req.getContentId());

        Post post = Post.builder()
                .title(req.getTitle())
                .body(req.getBody())
                .user(user)
                .content(content)
                .rating(req.getRating())
                .build();

        user.addPost(post);
        content.addReview(post);

        postRepository.saveAndFlush(post);

        return new PostResponseDto(post);
    }

    //:::::::::::::::::// read //::::::::::::::::://

    @Transactional
    public PostResponseDto readPost(Long postId) {

        Post post = getPost(postId);

        post.incrementViewCount();

        return new PostResponseDto(post);
    }

    public List<PostResponseDto> readPostsByUser(Long userId, long offset, int pageSize) {

        List<Post> posts = postRepository.getPostsByUserId(userId, offset, pageSize);

        return posts.stream()
                .map(PostResponseDto::new)
                .toList();
    }

    public List<PostResponseDto> readPostsByContent(Long contentId, long offset, int pagesize) {

        List<Post> posts = postRepository.getPostsByContentId(contentId, offset, pagesize);

        return posts.stream()
                .map(PostResponseDto::new)
                .toList();
    }

    public List<PostResponseDto> readPostsByKeyword(String keyword, long offset, int pagesize) {

        List<Post> posts = postRepository.findReviewsByHashtagContainKeyword(keyword, offset, pagesize);

        return posts.stream()
                .map(PostResponseDto::new)
                .toList();
    }

    public Page<PostResponseDto> getAdminPage(int page, int size, String sortBy, boolean asc) {

        Sort.Direction direction = asc ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> post = postRepository.findAll(pageable);

        return post.map(PostResponseDto::new);
    }

    public void deletePostAdmin(Long postId) {

        Post post = getPost(postId);
        postRepository.delete(post);
    }

    @Transactional
    public PostResponseDto updatePost(Long userId, Long postId, PostRequestDto req) {

        User user = getUser(userId);

        Post post = getPost(postId);

        validateUser(user, post);

        post.updateTitle(req.getTitle());
        post.updateBody(req.getBody());

        postRepository.save(post);

        return new PostResponseDto(post);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {

        User user = getUser(userId);

        Post post = getPost(postId);

        validateUser(user, post);

        postRepository.delete(post);
    }

    //::::::::::::::::::::::::// TOOL BOX //:::::::::::::::::::::::://

    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new BusinessException(ErrorCode.POST_NOT_FOUND)
        );
    }

    private void validateUser(User user, Post post) {

        if (!isPostUserEqual(user, post) && !user.isAdmin()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }

    private boolean isPostUserEqual(User user, Post post) {
        return post.getUser().getId().equals(user.getId());
    }

    private User getUser(Long userId) {
        return userService.getUser(userId);
    }

    private Content getContent(Long contentId) {
        return contentService.getContent(contentId);
    }

}
