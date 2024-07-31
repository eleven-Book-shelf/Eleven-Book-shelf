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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagRepository hashtagRepository;
    private final UserHashtagRepository userHashtagRepository;
    private final ContentHashtagRepository contentHashtagRepository;
    private final PostHashtagRepository postHashtagRepository;

    // TODO: 해시태그 갱신이 필요한 지점 :
    //  BoardService
    //  컨텐츠 생성 시 : createContent
    //  사용자가 게시글 조회시 : readPost 가중치 작게
    //  사용자가 리뷰 작성 시 : createPost 가중치 크게
    //  BookmarkService
    //  사용자가 컨텐츠 북마크 할 시 : addBookmark, removeBookmark 가중치 크게

    public Set<Hashtag> createHashtags(String preHashtag) {

        return inspectHashtag(parseHashtags(preHashtag));
    }

    public Set<UserHashtag> createUserHashtags (User user, Set<Hashtag> hashtags, double weight) {

        Set<UserHashtag> userHashtags = hashtags.stream().map(hashtag -> UserHashtag.builder()
                .user(user)
                .hashtag(hashtag)
                .build())
                .collect(Collectors.toSet());

        return inspectUserHashtag(user, userHashtags, weight);
    }

    public Set<PostHashtag> createPostHashtags (Post post, Set<Hashtag> hashtags) {

        Set<PostHashtag> postHashtags = hashtags.stream().map(hashtag -> PostHashtag.builder()
                        .post(post)
                        .hashtag(hashtag)
                        .build())
                .collect(Collectors.toSet());

        return inspectPostHashtag(post, postHashtags);
    }

    public Set<ContentHashtag> createContentHashtags (Content content, Set<Hashtag> hashtags) {

        Set<ContentHashtag> contentHashtags = hashtags.stream().map(hashtag -> ContentHashtag.builder()
                .content(content)
                .hashtag(hashtag)
                .build())
                .collect(Collectors.toSet());

        return inspectContentHashtag(content, contentHashtags);
    }

    //::::::::::::::::::::::::// TOOL BOX  //:::::::::::::::::::::::://

    private Set<Hashtag> parseHashtags(String preHashtag) {

        List<String> strings = Arrays.stream(preHashtag.split("[#,]"))
                .filter(s -> !s.isEmpty()) // 빈 문자열 제거
                .toList();

        return strings.stream().map(tag -> Hashtag.builder()
                .tag(tag)
                .build()).collect(Collectors.toSet());
    }

    private Set<Hashtag> inspectHashtag(Set<Hashtag> hashtags) {

        Set<Hashtag> newHashtags = hashtags.stream()
                .filter(hashtag -> {
                    Optional<Hashtag> existingHashtag = hashtagRepository.findByTag(hashtag.getTag());
                    if (existingHashtag.isPresent()) {
                        Hashtag existing = existingHashtag.get();
                        existing.incrementCount();
                        existing.updateTier();
                        hashtagRepository.save(existing);
                        return false;
                    } else {
                        return true;
                    }
                }).collect(Collectors.toSet());

        if (!newHashtags.isEmpty()) {
            hashtagRepository.saveAll(newHashtags);
        }

        return hashtags;
    }

    private Set<UserHashtag> inspectUserHashtag(User user, Set<UserHashtag> userHashtags, double weight) {

        Set<UserHashtag> newUserHashtags = userHashtags.stream()
                .filter(userHashtag -> {
                    Optional<UserHashtag> existingHashtag = userHashtagRepository.findByUserIdAndHashtagId(user.getId(), userHashtag.getHashtag().getId());
                    if (existingHashtag.isPresent()) {
                        UserHashtag existing = existingHashtag.get();
                        existing.incrementScore(weight);
                        return false;
                    } else {
                        return true;
                    }
                }).collect(Collectors.toSet());

        if (!newUserHashtags.isEmpty()) {
            userHashtagRepository.saveAll(newUserHashtags);
        }

        return userHashtags;
    }

    private Set<PostHashtag> inspectPostHashtag(Post post, Set<PostHashtag> postHashtags) {

        Set<PostHashtag> newPostHashtags = postHashtags.stream()
                .filter(postHashtag -> {
                    Optional<PostHashtag> existingHashtag = postHashtagRepository.findByPostIdAndHashtagId(post.getId(), postHashtag.getHashtag().getId());
                    return existingHashtag.isEmpty();
                }).collect(Collectors.toSet());

        if (!newPostHashtags.isEmpty()) {
            postHashtagRepository.saveAll(newPostHashtags);
        }

        return postHashtags;
    }

    private Set<ContentHashtag> inspectContentHashtag(Content content, Set<ContentHashtag> contentHashtags) {

        Set<ContentHashtag> newContentHashtags = contentHashtags.stream()
                .filter(contentHashtag -> {
                    Optional<ContentHashtag> existingHashtag = contentHashtagRepository.findByContentIdAndHashtagId(content.getId(), contentHashtag.getHashtag().getId());
                    return existingHashtag.isEmpty();
                }).collect(Collectors.toSet());

        if (!newContentHashtags.isEmpty()) {
            contentHashtagRepository.saveAll(newContentHashtags);
        }

        return contentHashtags;
    }
}
