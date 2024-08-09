package com.sparta.elevenbookshelf.domain.post.controller;

import com.sparta.elevenbookshelf.domain.hashtag.entity.Hashtag;
import com.sparta.elevenbookshelf.domain.hashtag.service.HashtagService;
import com.sparta.elevenbookshelf.domain.post.dto.PostRequestDto;
import com.sparta.elevenbookshelf.domain.post.dto.PostResponseDto;
import com.sparta.elevenbookshelf.domain.post.entity.Post;
import com.sparta.elevenbookshelf.domain.post.service.PostService;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;
    private final HashtagService hashtagService;

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody PostRequestDto req) {

        PostResponseDto res;

        if (req.getPostType().equals("REVIEW")) {
            res = postService.createReviewPost(userPrincipal.getUser().getId(), req);
            hashtagService.generatePostHashtags(userPrincipal.getUser(), res.getId(), req.getPrehashtag());
        } else {
            res = postService.createNormalPost(userPrincipal.getUser().getId(), req);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> readPost(
            @AuthenticationPrincipal @Nullable UserPrincipal userPrincipal,
            @PathVariable Long postId) {

        PostResponseDto res = postService.readPost(postId);

        if (userPrincipal != null && res.getPostType().equals("REVIEW")) {

            hashtagService.userPostHashtagInteraction(userPrincipal.getUser(), res.getId(), hashtagService.READ_WEIGHT);
        }

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/content")
    public ResponseEntity<List<PostResponseDto>> readPostsByContent (
            @RequestParam(value = "content") Long contentId,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "20") int pagesize) {


        List<PostResponseDto> res = postService.readPostsByContent(contentId, offset, pagesize);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> readPostsByKeyword (
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "20") int pagesize) {

        List<PostResponseDto> res = postService.readPostsByKeyword(keyword, offset, pagesize);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId,
            @RequestBody PostRequestDto req) {

        PostResponseDto res = postService.updatePost(userPrincipal.getUser().getId(), postId, req);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId) {

        postService.deletePost(userPrincipal.getUser().getId(), postId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
