package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.FavGenreRequestDto;
import com.sparta.elevenbookshelf.entity.FavGenre;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.FavGenreRepository;
import com.sparta.elevenbookshelf.repository.userRepository.UserRepository;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavGenreService {

    private final FavGenreRepository favGenreRepository;
    private final UserRepository userRepository;

    public FavGenreRequestDto get(Long userId) {
        User user = getUser(userId);

        List<String> favGenres = favGenreRepository.findAllByUserId(userId)
                .stream().map(FavGenre::getGenre).collect(Collectors.toList());

        return new FavGenreRequestDto(favGenres);
    }

    @Transactional
    public void post(Long userId, FavGenreRequestDto favGenreRequestDto) {
        User user = getUser(userId);

        List<FavGenre> favGenres = favGenreRequestDto.getGenre().stream()
                .map(genre -> new FavGenre(genre, user))
                .toList();

        user.addFavGenre(favGenres);
    }

    public void delete(Long userId, FavGenreRequestDto favGenreRequestDto) {
        User user = getUser(userId);

        List<FavGenre> favGenres = favGenreRequestDto.getGenre().stream()
                .map(genre -> new FavGenre(genre, user))
                .toList();

        user.removeFavGenre(favGenres);
    }

    //::::::::::::::::::::::::// TOOL BOX  //:::::::::::::::::::::::://

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }
}
