package com.sparta.elevenbookshelf.domain.hashtag.service;

import com.sparta.elevenbookshelf.domain.content.entity.Content;
import com.sparta.elevenbookshelf.domain.content.repository.ContentRepository;
import com.sparta.elevenbookshelf.domain.hashtag.dto.HashtagResponseDto;
import com.sparta.elevenbookshelf.domain.hashtag.entity.Hashtag;
import com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.ContentHashtag;
import com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.PostHashtag;
import com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.UserHashtag;
import com.sparta.elevenbookshelf.domain.hashtag.repository.ContentHashtagRepository;
import com.sparta.elevenbookshelf.domain.hashtag.repository.HashtagRepository;
import com.sparta.elevenbookshelf.domain.hashtag.repository.PostHashtagRepository;
import com.sparta.elevenbookshelf.domain.hashtag.repository.UserHashtagRepository;
import com.sparta.elevenbookshelf.domain.post.dto.PostResponseDto;
import com.sparta.elevenbookshelf.domain.post.entity.Post;
import com.sparta.elevenbookshelf.domain.post.repository.PostRepository;
import com.sparta.elevenbookshelf.domain.user.dto.UserHashtagRequestDto;
import com.sparta.elevenbookshelf.domain.user.dto.UserHashtagResponseDto;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import com.sparta.elevenbookshelf.domain.user.repository.UserRepository;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "HashtagService")
public class HashtagService {

    private static final double READ_WEIGHT = 1.0;
    private static final double READED_WEIGHT = 0.1;
    private static final double BOOKMARK_WEIGHT = 5.0;
    private static final double BOOKMARKED_WEIGHT = 0.5;
    private static final double COMMENT_WEIGHT = 2.0;
    private static final double COMMENTED_WEIGHT = 0.2;
    private static final double CREATE_WEIGHT = 10.0;
    private static final double INIT_WEIGHT = 100.0;


    private final HashtagRepository hashtagRepository;
    private final ContentHashtagRepository contentHashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final UserHashtagRepository userHashtagRepository;

    private final ContentRepository contentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<String> readTop10Hashtags() {

        List<Hashtag> topHashtags = hashtagRepository.findTop10ByCount();

        return topHashtags.stream().map(Hashtag::getTag).toList();
    }

    // Review 작성 시 해시태그 갱신 (+10.0)
    @Transactional
    public void updateHashtagsByCreateReview(Long userId, Long postId, Long contentId, String preHashtag) {

        User user = getUser(userId);

        Post post = getPost(postId);

        Content content = getContent(contentId);

        Set<String> tags = parseHashtag(preHashtag);

        // 사용자가 작성한 해시태그 추가 및 갱신
        tags.addAll(post.getPostHashtags().stream()
                .map(postHashtag -> postHashtag.getHashtag().getTag())
                .collect(Collectors.toSet()));

        for (String tag : tags) {

            Hashtag hashtag = createOrUpdateHashtag(tag);

            PostHashtag postHashtag = createOrUpdatePostHashtag(post, hashtag);
            post.addHashtag(postHashtag);
        }

        // 작품 해시태그 게시글에 추가 및 갱신
        tags.addAll(content.getContentHashtags().stream()
                .map(contentHashtag -> contentHashtag.getHashtag().getTag())
                .collect(Collectors.toSet()));

        for (String tag : tags) {

            Hashtag hashtag = createOrUpdateHashtag(tag);

            UserHashtag userHashtag = createOrUpdateUserHashtag(user, hashtag, CREATE_WEIGHT);
            user.addHashtag(userHashtag);

            ContentHashtag contentHashtag = createOrUpdateContentHashtag(content, hashtag, CREATE_WEIGHT);
            content.addHashtag(contentHashtag);
        }
    }

    // 작품이 크롤링 후 서비스에 등록될때 해시태그 갱신 (+100.0)
    @Transactional
    public void updateHashtagByCreateContent(Long contentId, Long postId) {

        Post post = getPost(postId);

        Content content = getContent(contentId);

        Set<String> tags = parseHashtag(content.getGenre() + content.getContentHashTag());

        for (String tag : tags) {
            Hashtag hashtag = createOrUpdateHashtag(tag);

            ContentHashtag contentHashtag = createOrUpdateContentHashtag(content, hashtag, 0.0);
            contentHashtag.incrementScore(INIT_WEIGHT);
            content.addHashtag(contentHashtag);

            PostHashtag postHashtag = createOrUpdatePostHashtag(post, hashtag);
            post.addHashtag(postHashtag);
        }

        contentRepository.save(content);
        contentHashtagRepository.saveAll(content.getContentHashtags());
        postHashtagRepository.saveAll(post.getPostHashtags());
    }

    // 사용자가 선택한 해시태그 갱신 (+100.0)
    @Transactional
    public List<UserHashtagResponseDto> updateUserHashtags (Long userId, UserHashtagRequestDto req) {

        User user = getUser(userId);

        Set<String> tags = parseHashtag(req.getTags());

        Set<UserHashtag> userHashtags = new HashSet<>();

        for (String tag : tags) {

            Hashtag hashtag = createOrUpdateHashtag(tag);

            UserHashtag userHashtag = createOrUpdateUserHashtag(user, hashtag, 0.0);
            userHashtag.incrementScore(INIT_WEIGHT);
            user.addHashtag(userHashtag);
            userHashtags.add(userHashtag);
        }

        userRepository.save(user);
        userHashtagRepository.saveAll(userHashtags);

        return userHashtags.stream()
                .map(userHashtag -> new UserHashtagResponseDto(userHashtag.getHashtag()))
                .toList();
    }

    @Transactional
    public void updateHashtagBySearchContent(Long userId, Long contentId) {

        User user = getUser(userId);

        Content content = getContent(contentId);

        Set<ContentHashtag> contentHashtags = content.getContentHashtags();
        Set<ContentHashtag> newContentHashtags = new HashSet<>();
        Set<UserHashtag> userHashtags = new HashSet<>();

        for (ContentHashtag tag : contentHashtags) {

            Hashtag hashtag = createOrUpdateHashtag(tag.getHashtag().getTag());

            UserHashtag userHashtag = createOrUpdateUserHashtag(user, hashtag, READ_WEIGHT);
            user.addHashtag(userHashtag);
            userHashtags.add(userHashtag);

            ContentHashtag contentHashtag = createOrUpdateContentHashtag(content, hashtag, READED_WEIGHT);
            content.addHashtag(contentHashtag);
            newContentHashtags.add(contentHashtag);
        }

        userHashtagRepository.saveAll(userHashtags);
        contentHashtagRepository.saveAll(newContentHashtags);
    }

    @Transactional
    public void updateHashtagByPost(Long userId, Long postId, String type) {

        double userWeight = 0.0;
        double contentWeight = 0.0;

        switch (type) {
            case "read" -> {
                userWeight = READ_WEIGHT;
                contentWeight = READED_WEIGHT;
            }

            case "comment" -> {
                userWeight = COMMENT_WEIGHT;
                contentWeight = COMMENTED_WEIGHT;
            }
        }

        User user = getUser(userId);

        Post post = getPost(postId);

        Content content = post.getContent();

        Set<ContentHashtag> contentHashtags = content.getContentHashtags();
        Set<PostHashtag> postHashtags = post.getPostHashtags();

        Set<Hashtag> hashtags = contentHashtags.stream().map(ContentHashtag::getHashtag).collect(Collectors.toSet());
        hashtags.addAll(postHashtags.stream().map(PostHashtag::getHashtag).collect(Collectors.toSet()));

        for (Hashtag tag : hashtags) {

            Hashtag hashtag = createOrUpdateHashtag(tag.getTag());

            UserHashtag userHashtag = createOrUpdateUserHashtag(user, hashtag, userWeight);
            user.addHashtag(userHashtag);

            ContentHashtag contentHashtag = createOrUpdateContentHashtag(content, hashtag, contentWeight);
            content.addHashtag(contentHashtag);
        }

        userRepository.save(user);
        userHashtagRepository.saveAll(user.getUserHashtags());
        contentRepository.save(content);
        contentHashtagRepository.saveAll(content.getContentHashtags());
    }

    @Transactional
    public void updateHashtagByBookmark(Long userId, Long contentId) {

        User user = getUser(userId);

        Content content = getContent(contentId);

        Set<ContentHashtag> contentHashtags = content.getContentHashtags();

        for (ContentHashtag tag : contentHashtags) {

            Hashtag hashtag = createOrUpdateHashtag(tag.getHashtag().getTag());

            UserHashtag userHashtag = createOrUpdateUserHashtag(user, hashtag, BOOKMARK_WEIGHT);
            user.addHashtag(userHashtag);

            ContentHashtag contentHashtag = createOrUpdateContentHashtag(content, hashtag, BOOKMARKED_WEIGHT);
            content.addHashtag(contentHashtag);
        }

        userRepository.save(user);
        userHashtagRepository.saveAll(user.getUserHashtags());
        contentRepository.save(content);
        contentHashtagRepository.saveAll(content.getContentHashtags());
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

        if(optionalPostHashtag.isPresent()) {
            postHashtag = optionalPostHashtag.get();
        } else {
                postHashtag = PostHashtag.builder()
                        .post(post)
                        .hashtag(hashtag)
                        .build();

                postHashtag.createId();
        }

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

    // 문자열 해시태그화
    public Set<String> parseHashtag (String preHashtag) {

        return Arrays.stream(preHashtag.split("[#/]"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    // 사용자 해시태그 상위 10개
    @Transactional
    public List<UserHashtagResponseDto> readUserHashtags (Long userId, int limit) {

        User user = getUser(userId);

        return user.getUserHashtags().stream()
                .sorted(Comparator.comparing(UserHashtag::getScore))
                .limit(limit)
                .map(userHashtag -> new UserHashtagResponseDto(userHashtag.getHashtag()))
                .toList();
    }



    @Transactional
    public List<PostResponseDto> recommendContentByUserHashtag (Long userId, long offset, int pagesize) {

        User user = getUser(userId);

        List<Content> contents = calculateSimilarity(user);
        List<PostResponseDto> result = new ArrayList<>();

        for (int i = (int) offset; i < i + pagesize; i++) {
            Content content = contents.get(i);
            Post post = postRepository.findByContentId(content.getId()).orElseGet(null);
            result.add(new PostResponseDto(post));
        }

        return result;

    }

    // 해시태그 유사도 계산 부분
    public List<Content> calculateSimilarity (User user) {

        // 컨텐츠 가져오기
        List<Content> contents = contentRepository.findAll();

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

        Map<Double, Content> sortedMap = sortMapByKey(resultMap);
        Collection<Content> values = sortedMap.values();
        return new ArrayList<>(values);
    }

    private Double operateEuclideanDistance (List<Double> a, List<Double> b) {

        double result = 0.0;

         for (int i = 0; i < a.size(); i++) {
             result += Math.pow((a.get(i) - b.get(i)),2);
         }

         return Math.pow(result, (double) 1 /a.size());
    }

    // 유사도로 맵 정렬
    private static LinkedHashMap<Double, Content> sortMapByKey(Map<Double, Content> map) {
        List<Map.Entry<Double, Content>> entries = new LinkedList<>(map.entrySet());
        entries.sort(Comparator.comparing(Map.Entry::getKey));

        LinkedHashMap<Double, Content> result = new LinkedHashMap<>();
        for (Map.Entry<Double, Content> entry : entries) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private User getUser(Long userId) {

        return userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }

    private Post getPost(Long postId) {

        return postRepository.findById(postId).orElseThrow(
                () -> new BusinessException(ErrorCode.POST_NOT_FOUND)
        );
    }

    private Content getContent(Long contentId) {

        return contentRepository.findById(contentId).orElseThrow(
                () -> new BusinessException(ErrorCode.POST_NOT_FOUND)
        );
    }

    public Page<HashtagResponseDto> getAdminPage(int page, int size, String sortBy, boolean asc) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Hashtag> hashtags = hashtagRepository.findAll(pageable);

        return hashtags.map(HashtagResponseDto::new);
    }

    public void deleteHashtag(Long hashtagId) {
        hashtagRepository.deleteById(hashtagId);
    }
}
