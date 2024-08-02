package com.sparta.elevenbookshelf.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class FavGenre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fav_genre_id")
    private Long id;

    @Column(nullable = false)
    private String genre;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public FavGenre(String genre, User user) {
        this.genre = genre;
        this.user = user;
    }
    
}
