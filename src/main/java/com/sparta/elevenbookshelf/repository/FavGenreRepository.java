package com.sparta.elevenbookshelf.repository;

import com.sparta.elevenbookshelf.entity.FavGenre;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FavGenreRepository extends CrudRepository<FavGenre, Integer> {
    List<FavGenre> findAllByUserId(Long userId);
}
