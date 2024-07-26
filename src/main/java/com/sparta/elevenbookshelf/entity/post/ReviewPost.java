package com.sparta.elevenbookshelf.entity.post;

import com.sparta.elevenbookshelf.entity.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@DiscriminatorValue("REVIEW")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class ReviewPost extends Post {

    private Double rating;
}
