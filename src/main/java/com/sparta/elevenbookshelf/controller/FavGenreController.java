package com.sparta.elevenbookshelf.controller;

import com.sparta.elevenbookshelf.dto.FavGenreRequestDto;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import com.sparta.elevenbookshelf.service.FavGenreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favgenres")
@Slf4j(topic = "UserController")
public class FavGenreController {

    private final FavGenreService favGenreService;

    @GetMapping
    public ResponseEntity<FavGenreRequestDto> get(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok().body(favGenreService.get(userPrincipal.getUser().getId()));
    }

    @PostMapping
    public ResponseEntity<?> post(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody FavGenreRequestDto favGenreRequestDto) {
        favGenreService.post(userPrincipal.getUser().getId(),favGenreRequestDto);
        return ResponseEntity.noContent().build();

    }

    @DeleteMapping
    public ResponseEntity<?> delete(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody FavGenreRequestDto favGenreRequestDto) {
        favGenreService.delete(userPrincipal.getUser().getId(),favGenreRequestDto);
        return ResponseEntity.noContent().build();

    }

}
