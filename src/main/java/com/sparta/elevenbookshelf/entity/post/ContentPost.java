package com.sparta.elevenbookshelf.entity.post;

import com.sparta.elevenbookshelf.entity.*;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@DiscriminatorValue("CONTENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class ContentPost extends Post{

}
