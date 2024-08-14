package com.sparta.elevenbookshelf.domain.post.controller;

import com.sparta.elevenbookshelf.domain.hashtag.service.HashtagService;
import com.sparta.elevenbookshelf.domain.post.dto.PostMapResponseDto;
import com.sparta.elevenbookshelf.domain.post.dto.PostRequestDto;
import com.sparta.elevenbookshelf.domain.post.dto.PostResponseDto;
import com.sparta.elevenbookshelf.domain.post.dto.PostSearchCond;
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

    /**
     * 게시글 검색 기능
     * - 주어진 조건들에 맞춰 컨텐츠를 조회합니다.
     * @param offset 현재 위치
     * @param pagesize 페이지 사이즈
     * @Body  : PostSearchCond
     *      userId 작성자 id
     *      contentId 컨텐츠 id
     *      keyword 검색할 키워드 : 비어있으면 전체 조회
     *      postType NORMAL || REVIEW || NOTICE : 비어있으면 전체 조회
     *      sortBy 정렬조건 : 비어있으면 생성 순 정렬
     * @return List<PostResponseDto> 불러온 게시글 Dto 목록
     */
    @GetMapping
    public ResponseEntity<List<PostResponseDto>> readPosts(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "20") int pagesize,
            @RequestBody PostSearchCond cond) {

        List<PostResponseDto> res = postService.readPostsBySearchCond(offset, pagesize, cond);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // 포스트 제작
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody PostRequestDto req) {

        PostResponseDto res;

        if (req.getPostType().equals("REVIEW")) {
            res = postService.createReviewPost(userPrincipal.getUser().getId(), req);
            hashtagService.generatePostHashtags(userPrincipal.getUser().getId(), res.getId(), req.getPrehashtag());
        } else {
            res = postService.createNormalPost(userPrincipal.getUser().getId(), req);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    // 특정 포스트 가져오기
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> readPost(
            @AuthenticationPrincipal @Nullable UserPrincipal userPrincipal,
            @PathVariable Long postId) {

        PostResponseDto res = postService.readPost(postId);

        if (userPrincipal != null && res.getPostType().equals("REVIEW")) {

            hashtagService.userPostHashtagInteraction(userPrincipal.getUser().getId(), res.getId(), hashtagService.READ_WEIGHT);
        }

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    //포스트 수정
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId,
            @RequestBody PostRequestDto req) {

        PostResponseDto res = postService.updatePost(userPrincipal.getUser().getId(), postId, req);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    //포스트 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId) {

        postService.deletePost(userPrincipal.getUser().getId(), postId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    //포스트 검색
    @GetMapping("/keyword")
    public ResponseEntity<List<PostResponseDto>> readPostsByKeyword (
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "20") int pagesize) {

        List<PostResponseDto> res = postService.readPostsByKeyword(keyword, offset, pagesize);
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

    // 포스트 리스트 가져오기
    @GetMapping("/list")
    public ResponseEntity<PostMapResponseDto> readPostsByPostType (
            @RequestParam String postType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pagesize,
            @RequestParam boolean asc)  {

        PostMapResponseDto res = postService.readPostsByPostType(postType, page, pagesize,asc);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    //:::::::::::::::::// like //::::::::::::::::://

    //포스트 좋아요
    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> createLikePost(
            @PathVariable Long postId, @AuthenticationPrincipal UserPrincipal userPrincipal){

        postService.createLikePost(postId, userPrincipal.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //포스트 좋아요 취소
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> deleteLikePost(
            @PathVariable Long postId, @AuthenticationPrincipal UserPrincipal userPrincipal){

        postService.deleteLikePost(postId, userPrincipal.getUser().getId());
        return ResponseEntity.ok().build();
    }

    //포스트 좋아요 상태
    @GetMapping("/{postId}/like")
    public ResponseEntity<Boolean> getLikePost(
            @PathVariable Long postId, @AuthenticationPrincipal UserPrincipal userPrincipal){

        return ResponseEntity.ok().body(postService.getLikePost(postId, userPrincipal.getUser().getId()));
    }
}
