package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.entity.Content;
import com.sparta.elevenbookshelf.entity.Hashtag;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.entity.mappingEntity.ContentHashtag;
import com.sparta.elevenbookshelf.entity.mappingEntity.PostHashtag;
import com.sparta.elevenbookshelf.entity.mappingEntity.UserHashtag;
import com.sparta.elevenbookshelf.entity.post.Post;
import com.sparta.elevenbookshelf.repository.contentRepository.ContentRepository;
import com.sparta.elevenbookshelf.repository.hashtagRepository.ContentHashtagRepository;
import com.sparta.elevenbookshelf.repository.hashtagRepository.HashtagRepository;
import com.sparta.elevenbookshelf.repository.hashtagRepository.PostHashtagRepository;
import com.sparta.elevenbookshelf.repository.hashtagRepository.UserHashtagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "HashtagService")
public class HashtagService {

    private final HashtagRepository hashtagRepository;
    private final ContentHashtagRepository contentHashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final UserHashtagRepository userHashtagRepository;

    private final ContentRepository contentRepository;

    // TODO: controller로 연결
    public List<Hashtag> readTop10Hashtags() {

        return hashtagRepository.findTop10ByCount();
    }


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
            hashtag.updateTier();
        } else {
            hashtag = Hashtag.builder()
                    .tag(tag)
                    .build();
        }

        return hashtagRepository.save(hashtag);
    }

    public Set<String> parseHashtag (String preHashtag) {

        return Arrays.stream(preHashtag.split("[#,]"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    // 해시태그 유사도 계산 부분
    public Map<Double, Content> calculateSimilarity (User user) {

        // 조회수가 높은 50개의 컨텐츠 가져오기
        List<Content> contents = contentRepository.findTop50ByView();

        // 차이를 저장할 맵
        Map<Double, Content> resultMap = new HashMap<>();

        // 비교군 : 사용자 해시태그
        List<UserHashtag> userHashtags = user.getUserHashtags().stream().toList();

        for(Content content : contents) {
            List<Hashtag> xTags = new ArrayList<>(userHashtags.stream()
                    .map(UserHashtag::getHashtag)
                    .toList());

            List<ContentHashtag> contentHashtags = content.getContentHashtags().stream().toList();
            List<Hashtag> yTags = new ArrayList<>(contentHashtags.stream()
                    .map(ContentHashtag::getHashtag)
                    .toList());

            xTags.retainAll(yTags);

            // 각 해시태그끼리의 거리 계산을 위한 리스트
            List<Double> aList = new ArrayList<>();
            List<Double> bList = new ArrayList<>();

            // 각 객체-해시태그에 할당된 점수값을 해당 해시태그의 티어로 나눠 개인맞춤을 가중
            for (Hashtag tag : xTags) {
                Double a = userHashtags.stream()
                        .filter(userHashtag -> userHashtag.getHashtag().equals(tag))
                        .findAny()
                        .get()
                        .getScore();

                Double aTier = userHashtags.stream()
                        .filter(userHashtag -> userHashtag.getHashtag().equals(tag))
                        .findAny()
                        .get()
                        .getHashtag()
                        .getTier();

                aList.add(a/aTier);

                Double b = contentHashtags.stream()
                        .filter(contentHashtag -> contentHashtag.getHashtag().equals(tag))
                        .findAny()
                        .get()
                        .getScore();

                Double bTier = contentHashtags.stream()
                        .filter(contentHashtag -> contentHashtag.getHashtag().equals(tag))
                        .findAny()
                        .get()
                        .getHashtag()
                        .getTier();

                bList.add(b/bTier);
            }

            // 단순 유클리안 거리 계산으로 유사도 측정 | 값이 작을 수록 유사도 높음
            resultMap.put(operateEuclideanDistance(aList, bList),content);
        }

        return sortMapByKey(resultMap);
    }

    private Double operateEuclideanDistance (List<Double> a, List<Double> b) {

        double result = 0.0;

         for (int i = 0; i < a.size(); i++) {
             result += Math.pow((a.get(i) - b.get(i)),2);
         }

         return Math.pow(result, (double) 1 /a.size());
    }

    private static LinkedHashMap<Double, Content> sortMapByKey(Map<Double, Content> map) {
        List<Map.Entry<Double, Content>> entries = new LinkedList<>(map.entrySet());
        entries.sort(Comparator.comparing(Map.Entry::getKey));

        LinkedHashMap<Double, Content> result = new LinkedHashMap<>();
        for (Map.Entry<Double, Content> entry : entries) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
