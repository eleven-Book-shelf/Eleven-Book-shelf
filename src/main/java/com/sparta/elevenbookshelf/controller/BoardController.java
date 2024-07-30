package com.sparta.elevenbookshelf.controller;

import com.sparta.elevenbookshelf.dto.*;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import com.sparta.elevenbookshelf.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    //:::::::::::::::::// board //::::::::::::::::://

    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody BoardRequestDto req
            ) {

        BoardResponseDto res = boardService.createBoard(userPrincipal.getUser(), req);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> readBoards() {

        List<BoardResponseDto> res = boardService.readBoards();

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<List<PostResponseDto>> readBoard(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int pagesize) {

        List<PostResponseDto> res = boardService.readBoard(boardId, offset, pagesize);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> updateBoard(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId,
            @RequestBody BoardRequestDto req) {

        BoardResponseDto res = boardService.updateBoard(userPrincipal.getUser(), boardId, req);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> deleteBoard(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId
                                         ) {

        boardService.deleteBoard(userPrincipal.getUser(), boardId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    //:::::::::::::::::// post //::::::::::::::::://

    @PostMapping("/{boardId}/posts")
    public ResponseEntity<PostResponseDto> createPost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId,
            @RequestBody PostRequestDto req) {

        PostResponseDto res = boardService.createPost(userPrincipal.getUser(), boardId, req);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/{boardId}/posts/{postId}")
    public ResponseEntity<PostResponseDto> readPost(
            @PathVariable Long boardId,
            @PathVariable Long postId) {

        PostResponseDto res = boardService.readPost(boardId, postId);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PutMapping("/{boardId}/posts/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @RequestBody PostRequestDto req) {

        PostResponseDto res = boardService.updatePost(userPrincipal.getUser(), boardId, postId, req);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @DeleteMapping("/{boardId}/posts/{postId}")
    public ResponseEntity<?> deletePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId,
            @PathVariable Long postId) {

        boardService.deletePost(userPrincipal.getUser(), boardId, postId);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    //:::::::::::::::::// post //::::::::::::::::://

    @PostMapping("/content")
    public ResponseEntity<ContentResponseDto> createContent(@RequestBody ContentRequestDto req){

        ContentResponseDto res = boardService.createContent(req);

        PostRequestDto postReq = new PostRequestDto(res);

        boardService.createPost(null, null, postReq);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }


}
