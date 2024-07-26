package com.sparta.elevenbookshelf.entity.post;

import com.sparta.elevenbookshelf.entity.Board;
import com.sparta.elevenbookshelf.entity.Content;
import com.sparta.elevenbookshelf.entity.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@DiscriminatorValue("CONTENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class ContentPost extends Post{

}
