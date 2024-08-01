package com.sparta.elevenbookshelf.entity.post;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@DiscriminatorValue("NORMAL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class NormalPost extends Post {

}
