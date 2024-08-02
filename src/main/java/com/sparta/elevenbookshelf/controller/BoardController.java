package com.sparta.elevenbookshelf.controller;

import com.sparta.elevenbookshelf.dto.BoardRequestDto;
import com.sparta.elevenbookshelf.dto.BoardResponseDto;
import com.sparta.elevenbookshelf.dto.PostRequestDto;
import com.sparta.elevenbookshelf.dto.PostResponseDto;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import com.sparta.elevenbookshelf.service.BoardService;
import com.sparta.elevenbookshelf.service.HashtagService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;
    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
    private final HashtagService hashtagService;

    //:::::::::::::::::// board //::::::::::::::::://

    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody BoardRequestDto req
            ) {

        BoardResponseDto res = boardService.createBoard(userPrincipal.getUser().getId(), req);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> readBoards() {

        List<BoardResponseDto> res = boardService.readBoards();

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/{boardId}/title")
    public ResponseEntity<String> readBoardTitle(
            @PathVariable Long boardId) {
        return ResponseEntity.status(HttpStatus.OK).body(boardService.readBoardTitle(boardId));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<Map<String, Object>> readBoard(
            @PathVariable Long boardId,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "20") int pagesize) {

        List<PostResponseDto> posts = boardService.readBoard(boardId, offset, pagesize);
        long totalPosts = boardService.getTotalPostsByBoard(boardId);
        return getMapResponseEntity(pagesize, (double) totalPosts, posts);
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> updateBoard(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId,
            @RequestBody BoardRequestDto req) {

        BoardResponseDto res = boardService.updateBoard(userPrincipal.getUser().getId(), boardId, req);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> deleteBoard(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId
                                         ) {

        boardService.deleteBoard(userPrincipal.getUser().getId(), boardId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    //:::::::::::::::::// post //::::::::::::::::://

    @PostMapping("/{boardId}/post")
    public ResponseEntity<PostResponseDto> createPost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId,
            @RequestBody PostRequestDto req) {

        PostResponseDto res = boardService.createPost(userPrincipal.getUser().getId(), boardId, req);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/{boardId}/post/{postId}")
    public ResponseEntity<PostResponseDto> readPost(
            @AuthenticationPrincipal @Nullable UserPrincipal userPrincipal,
            @PathVariable Long boardId,
            @PathVariable Long postId) {

        PostResponseDto res = boardService.readPost(userPrincipal.getUser().getId(), boardId, postId);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // TODO : toFront
    @GetMapping("/{boardId}/post/")
    public ResponseEntity<List<PostResponseDto>> readPostsByContent (
            @AuthenticationPrincipal @Nullable UserPrincipal userPrincipal,
            @PathVariable Long boardId,
            @RequestParam(value = "content") Long contentId,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "20") int pagesize) {

        List<PostResponseDto> res = boardService.readPostsByContent(userPrincipal.getUser().getId(), boardId, contentId, offset, pagesize);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PutMapping("/{boardId}/post/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @RequestBody PostRequestDto req) {

        PostResponseDto res = boardService.updatePost(userPrincipal.getUser().getId(), boardId, postId, req);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @DeleteMapping("/deletePost/{boardId}/post/{postId}")
    public ResponseEntity<?> deletePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId,
            @PathVariable Long postId) {

        boardService.deletePost(userPrincipal.getUser().getId(), boardId, postId);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    //:::::::::::::::::// user //::::::::::::::::://

    @GetMapping("/user/posts")
    public ResponseEntity<Map<String, Object>> readUserPost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "offset", defaultValue = "0") long offset,
            @RequestParam(value = "pagesize", defaultValue = "20") int pagesize) {

        List<PostResponseDto> posts = boardService.readUserPost(
                userPrincipal.getUser().getId(),
                offset,
                pagesize);
        long totalPosts = boardService.getTotalPostsByBoard(userPrincipal.getUser().getId());
        return getMapResponseEntity(pagesize, (double) totalPosts, posts);
    }

    // TODO : toFront
    @GetMapping("/user/recommend")
    public ResponseEntity<List<PostResponseDto>> recommendContentsByUserHashtag (
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") long offset,
            @RequestParam(defaultValue = "20") int pagesize) {

        List<PostResponseDto> res = hashtagService.recommendContentByUserHashtag(userPrincipal.getUser().getId(), offset, pagesize);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    //:::::::::::::::::// content //::::::::::::::::://

/*
crwaling -> createContent
* */


    //:::::::::::::::::// TOOL BOX //::::::::::::::::://

    private static ResponseEntity<Map<String, Object>> getMapResponseEntity(int pagesize, double totalPosts, List<PostResponseDto> posts) {
        int totalPages = (int) Math.ceil(totalPosts / pagesize);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts);
        response.put("totalPages", totalPages);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
