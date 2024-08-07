package com.sparta.elevenbookshelf.domain.board.controller;

import com.sparta.elevenbookshelf.domain.board.dto.BoardRequestDto;
import com.sparta.elevenbookshelf.domain.board.dto.BoardResponseDto;
import com.sparta.elevenbookshelf.domain.board.service.BoardService;
import com.sparta.elevenbookshelf.domain.hashtag.service.HashtagService;
import com.sparta.elevenbookshelf.domain.post.dto.PostRequestDto;
import com.sparta.elevenbookshelf.domain.post.dto.PostResponseDto;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;
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

    @GetMapping("/title/{boardId}")
    public ResponseEntity<String> readBoardTitle(
            @PathVariable Long boardId) {
        return ResponseEntity.status(HttpStatus.OK).body(boardService.readBoardTitle(boardId));
    }

    // TODO: 우선순위
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

        Long userId = 0L;
        if (userPrincipal != null) {
            userId = userPrincipal.getUser().getId();
        }

        PostResponseDto res = boardService.readPost(userId, boardId, postId);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

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

    //:::::::::::::::::// TOOL BOX //::::::::::::::::://

    private static ResponseEntity<Map<String, Object>> getMapResponseEntity(int pagesize, double totalPosts, List<PostResponseDto> posts) {
        int totalPages = (int) Math.ceil(totalPosts / pagesize);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts);
        response.put("totalPages", totalPages);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
