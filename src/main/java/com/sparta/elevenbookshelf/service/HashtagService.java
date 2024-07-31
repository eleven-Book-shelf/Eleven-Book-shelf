package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.entity.Content;
import com.sparta.elevenbookshelf.entity.Hashtag;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.entity.mappingEntity.ContentHashtag;
import com.sparta.elevenbookshelf.entity.mappingEntity.PostHashtag;
import com.sparta.elevenbookshelf.entity.mappingEntity.UserHashtag;
import com.sparta.elevenbookshelf.entity.post.Post;
import com.sparta.elevenbookshelf.repository.hashtagRepository.ContentHashtagRepository;
import com.sparta.elevenbookshelf.repository.hashtagRepository.HashtagRepository;
import com.sparta.elevenbookshelf.repository.hashtagRepository.PostHashtagRepository;
import com.sparta.elevenbookshelf.repository.hashtagRepository.UserHashtagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "HashtagService")
public class HashtagService {

    private final HashtagRepository hashtagRepository;
    private final ContentHashtagRepository contentHashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final UserHashtagRepository userHashtagRepository;


    @Transactional
    public UserHashtag createOrUpdateUserHashtag(User user, Hashtag hashtag, double score) {

        Optional<UserHashtag> optionalUserHashtag = userHashtagRepository.findByUserIdAndHashtagId(user.getId(), hashtag.getId());
        UserHashtag userHashtag;

        if(optionalUserHashtag.isPresent()) {
            userHashtag = optionalUserHashtag.get();
            userHashtag.incrementScore(score);
        } else {
            userHashtag = UserHashtag.builder()
                    .user(user)
                    .hashtag(hashtag)
                    .build();

            userHashtag.createId();
        }

        return userHashtagRepository.save(userHashtag);
    }

    @Transactional
    public PostHashtag createOrUpdatePostHashtag(Post post, Hashtag hashtag) {

        Optional<PostHashtag> optionalPostHashtag = postHashtagRepository.findByPostIdAndHashtagId(post.getId(), hashtag.getId());
        PostHashtag postHashtag;

        postHashtag = optionalPostHashtag.orElseGet(()->PostHashtag.builder()
                .post(post)
                .hashtag(hashtag)
                .build());

        postHashtag.createId();

        return postHashtagRepository.save(postHashtag);
    }

    @Transactional
    public ContentHashtag createOrUpdateContentHashtag(Content content, Hashtag hashtag, double score) {

        Optional<ContentHashtag> optionalContentHashtag = contentHashtagRepository.findByContentIdAndHashtagId(content.getId(), hashtag.getId());
        ContentHashtag contentHashtag;

        if(optionalContentHashtag.isPresent()) {
            contentHashtag = optionalContentHashtag.get();
            contentHashtag.incrementScore(score);
        } else {
            contentHashtag = ContentHashtag.builder()
                    .content(content)
                    .hashtag(hashtag)
                    .build();

            contentHashtag.createId();
        }

        return contentHashtagRepository.save(contentHashtag);
    }

    @Transactional
    public Hashtag createOrUpdateHashtag(String tag) {

        Optional<Hashtag> optionalHashtag = hashtagRepository.findByTag(tag);
        Hashtag hashtag;

        if (optionalHashtag.isPresent()) {
            hashtag = optionalHashtag.get();
            hashtag.incrementCount();
        } else {
            hashtag = Hashtag.builder()
                    .tag(tag)
                    .build();
        }

        return hashtagRepository.save(hashtag);
    }

    public Set<String> parseHashtag (String preHashtag) {

        return Arrays.stream(preHashtag.split("[#,]]"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
}
