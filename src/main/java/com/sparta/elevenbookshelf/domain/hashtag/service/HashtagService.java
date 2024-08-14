package com.sparta.elevenbookshelf.domain.hashtag.service;

import com.sparta.elevenbookshelf.domain.content.dto.ContentResponseDto;
import com.sparta.elevenbookshelf.domain.content.entity.Content;
import com.sparta.elevenbookshelf.domain.content.service.ContentService;
import com.sparta.elevenbookshelf.domain.hashtag.dto.HashtagRequestDto;
import com.sparta.elevenbookshelf.domain.hashtag.dto.HashtagResponseDto;
import com.sparta.elevenbookshelf.domain.hashtag.entity.Hashtag;
import com.sparta.elevenbookshelf.domain.hashtag.entity.Scorable;
import com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.ContentHashtag;
import com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.PostHashtag;
import com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.UserHashtag;
import com.sparta.elevenbookshelf.domain.hashtag.repository.ContentHashtagRepository;
import com.sparta.elevenbookshelf.domain.hashtag.repository.HashtagRepository;
import com.sparta.elevenbookshelf.domain.hashtag.repository.PostHashtagRepository;
import com.sparta.elevenbookshelf.domain.hashtag.repository.UserHashtagRepository;
import com.sparta.elevenbookshelf.domain.post.entity.Post;
import com.sparta.elevenbookshelf.domain.post.repository.PostRepository;
import com.sparta.elevenbookshelf.domain.post.service.PostService;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import com.sparta.elevenbookshelf.domain.user.service.UserService;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "HashtagService")
public class HashtagService {

    @Value("${READ_WEIGHT}")
    public double READ_WEIGHT;
    @Value("${READED_WEIGHT}")
    public double READED_WEIGHT;
    @Value("${BOOKMARK_WEIGHT}")
    public double BOOKMARK_WEIGHT;
    @Value("${BOOKMARKED_WEIGHT}")
    public double BOOKMARKED_WEIGHT;
    @Value("${COMMENT_WEIGHT}")
    public double COMMENT_WEIGHT;
    @Value("${COMMENTED_WEIGHT}")
    public double COMMENTED_WEIGHT;
    @Value("${CREATE_WEIGHT}")
    public double CREATE_WEIGHT;
    @Value("${INIT_WEIGHT}")
    public double INIT_WEIGHT;

//    private final PostService postService;
    private final ContentService contentService;
    private final UserService userService;

    private final HashtagRepository hashtagRepository;
    private final PostRepository postRepository;

    private final ContentHashtagRepository contentHashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final UserHashtagRepository userHashtagRepository;


    public List<String> readTop10Hashtags() {

        List<Hashtag> topHashtags = hashtagRepository.findTop10ByCount();

        return topHashtags.stream().map(Hashtag::getTag).toList();
    }

    public List<String> readHashtags() {

        return  hashtagRepository.findAll().stream()
                .map(Hashtag::getTag)
                .toList();
    }

    // 사용자 해시태그 상위 limit개
    public List<HashtagResponseDto> readUserHashtags (Long userId, int limit) {

        User user = getUser(userId);

        return user.getUserHashtags().stream()
                .sorted(Comparator.comparing(UserHashtag::getScore))
                .limit(limit)
                .map(userHashtag -> new HashtagResponseDto(userHashtag.getHashtag()))
                .toList();
    }

    // 사용자의 입력을 받아 사용자의 해시태그를 업데이트
    public List<HashtagResponseDto> updateUserHashtags(Long userId, HashtagRequestDto req) {

        User user = getUser(userId);

        List<Hashtag> hashtags = updateAndSaveHashtags(req.getTags());
        List<Hashtag> userHashtags = updateAndSaveHashtags(user, hashtags, this::createOrGetHashtag, INIT_WEIGHT);

        return userHashtags.stream()
                .map(HashtagResponseDto::new)
                .toList();
    }

    public Page<HashtagResponseDto> getAdminPage(int page, int size, String sortBy, boolean asc) {
        Sort.Direction direction = asc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Hashtag> hashtags = hashtagRepository.findAll(pageable);

        return hashtags.map(HashtagResponseDto::new);
    }

    public void deleteHashtag(Long hashtagId) {
        hashtagRepository.deleteById(hashtagId);
    }


    /* Hashtag 비즈니스 로직 -> 비동기 처리 예정 (실시간 반영 필요없으므로 부하를 줄이기 위해서)

       - 작품 크롤링 : 컨텐츠 (문자열) -> 해시태그 생성 및 저장 -> 컨텐츠-해시태그 생성 및 저장 [O]

       - 북마크 시 : 사용자 <-> 컨텐츠 [O]
       - 컨텐츠 단건 조회 시 : 사용자 <-> 컨텐츠 [O]

       - 리뷰 작성 시 : 사용자 <-> 컨텐츠 | 사용자 입력 해시태그 + 컨텐츠 해시태그 -> 게시글 해시태그 생성 [O]
       - 게시글 단건 조회 시 : 사용자 <-> 컨텐츠 | 게시글 -> 사용자 [O]
       - 댓글 작성 시 : 사용자 <-> 컨텐츠 | 게시글 -> 사용자

---------------------------------------------------------------------------------------------------------------------

       - 컨텐츠 (문자열) -> 해시태그 생성 및 저장 -> 컨텐츠-해시태그 생성 및 저장 :
                GenerateContentHashtag
       - 사용자 <-> 컨텐츠 :
                userContentHashtagInteraction
       - 게시글 -> 사용자 :
                userPostHashtagInteraction
       - 사용자 입력 해시태그 + 컨텐츠 해시태그 -> 게시글 해시태그 생성 :
                generatePostHashtags
     */

    @Async(value = "asyncThreadPool")
    @Transactional
    public void userContentHashtagInteraction (Long userId, Long contentId, double userScore, double contentScore) {

        User user = getUser(userId);

        Content content = getContent(contentId);

        // 사용자의 해시태그 해시태그로 불러오기
        Set<Hashtag> hashtags = user.getUserHashtags().stream()
                .map(UserHashtag::getHashtag)
                .collect(Collectors.toSet());

        log.info("getUserHashtags" + hashtags.stream().map(Hashtag::getTag).toString());

        // 콘텐츠의 해시태그 해시태그로 불러와서 추가하기
        hashtags.addAll(
                content.getContentHashtags().stream()
                        .map(ContentHashtag::getHashtag)
                        .collect(Collectors.toSet())
        );

        log.info("getUserAndContentHashtags" + hashtags.stream().map(Hashtag::getTag).toString());

        // 사용자 | 컨텐츠 -해시태그 각각 갱신하기
        updateAndSaveHashtags(user, hashtags.stream().toList(), this::createOrGetHashtag, userScore);
        updateAndSaveHashtags(content, hashtags.stream().toList(), this::createOrGetHashtag, contentScore);
    }

    @Async(value = "asyncThreadPool")
    @Transactional
    public void userPostHashtagInteraction(Long userId, Long postId, double userScore) {

        User user = getUser(userId);

        Post post = getPost(postId);

        // 사용자의 해시태그 해시태그로 불러오기
        Set<Hashtag> hashtags = user.getUserHashtags().stream()
                .map(UserHashtag::getHashtag)
                .collect(Collectors.toSet());

        hashtags.addAll(
                post.getPostHashtags().stream()
                        .map(PostHashtag::getHashtag)
                        .collect(Collectors.toSet())
        );

        updateAndSaveHashtags(user, hashtags.stream().toList(), this::createOrGetHashtag, userScore);
    }

    @Transactional
    public void generateContentHashtags(String preHashtag, Long contentId) {

        Content content = getContent(contentId);

        List<Hashtag> hashtags = updateAndSaveHashtags(preHashtag);
        updateAndSaveHashtags(content, hashtags, this::createOrGetHashtag, INIT_WEIGHT);
    }

    @Transactional
    public void generatePostHashtags(Long userId, Long postId, String preHashtag) {

        Post post = getPost(postId);

        List<String> contentTags = post.getContent().getContentHashtags().stream()
                .map(hashtag -> hashtag.getHashtag().getTag())
                .toList();

        for (String tag : contentTags) {

            preHashtag += ("#" + tag);
        }

        List<Hashtag> hashtags = updateAndSaveHashtags(preHashtag);

        userContentHashtagInteraction(userId, post.getContent().getId(), CREATE_WEIGHT, CREATE_WEIGHT);
        updateAndSaveHashtags(post, hashtags, this::createOrGetHashtag, 0);
        post.addHashtag();
    }


    /* UPDATE & SAVE hashtag
    * 해시태그를 입력받아 생성하거나 갱신한 후 저장
    * */

    private <T, U> List<Hashtag> updateAndSaveHashtags(T entity, List<Hashtag> hashtags, BiFunction<T, Hashtag, U> createOrGetHashtagFunc, double score) {

        Set<U> res = new HashSet<>();

        for (Hashtag tag : hashtags) {
            U entityHashtag = createOrGetHashtagFunc.apply(entity, tag);
            incrementScoreIfPossible(entityHashtag, score);
            res.add(entityHashtag);
        }

        addHashtags(entity, res);

        return res.stream()
                .map(this::extractHashtag)
                .toList();
    }

    private <U> void incrementScoreIfPossible(U entityHashtag, double score) {
        if (entityHashtag instanceof Scorable) {
            ((Scorable) entityHashtag).incrementScore(score);
        }
    }

    private <T, U> void addHashtags(T entity, Set<U> res) {
        if (entity instanceof User) {
            ((User) entity).addHashtags((Set<UserHashtag>) res);
        } else if (entity instanceof Content) {
            ((Content) entity).addHashtags((Set<ContentHashtag>) res);
        } else if (entity instanceof Post) {
            ((Post) entity).addHashtags((Set<PostHashtag>) res);
        }
    }

    private Hashtag extractHashtag(Object entityHashtag) {
        if (entityHashtag instanceof UserHashtag) {
            return ((UserHashtag) entityHashtag).getHashtag();
        } else if (entityHashtag instanceof ContentHashtag) {
            return ((ContentHashtag) entityHashtag).getHashtag();
        } else if (entityHashtag instanceof PostHashtag) {
            return ((PostHashtag) entityHashtag).getHashtag();
        }
        throw new IllegalArgumentException("Unsupported entity hashtag type");
    }

    private List<Hashtag> updateAndSaveHashtags (String preHashtag) {

        Set<String> parsedTags = parseHashtag(preHashtag);
        Set<Hashtag> res = new HashSet<>();

        for (String tag : parsedTags) {
            Hashtag hashtag = createOrGetHashtag(tag);
            hashtag.incrementCount();
            res.add(hashtag);
        }

        return hashtagRepository.saveAll(res);
    }


    /* CREATE & GET Hashtags
    * 해시태그와 연관시킬 객체를 입력 받아 연관시켜주는 메서드
    * 매핑 엔티티가 이미 연관되어 있다면 GET, 연관되어 있지 않다면 CREATE & GET
    * */

    private UserHashtag createOrGetHashtag(User user, Hashtag hashtag) {

        Optional<UserHashtag> optionalUserHashtag = userHashtagRepository.findByUserIdAndHashtagId(user.getId(), hashtag.getId());
        UserHashtag userHashtag;

        if(optionalUserHashtag.isPresent()) {
            userHashtag = optionalUserHashtag.get();
        } else {
            userHashtag = UserHashtag.builder()
                    .user(user)
                    .hashtag(hashtag)
                    .build();

            userHashtag.createId();
        }

        return userHashtag;
    }


    private PostHashtag createOrGetHashtag(Post post, Hashtag hashtag) {

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

        return postHashtag;
    }

    private ContentHashtag createOrGetHashtag(Content content, Hashtag hashtag) {

        Optional<ContentHashtag> optionalContentHashtag = contentHashtagRepository.findByContentIdAndHashtagId(content.getId(), hashtag.getId());

        if(optionalContentHashtag.isPresent()) {
            return optionalContentHashtag.get();
        } else {
            ContentHashtag contentHashtag = ContentHashtag.builder()
                    .content(content)
                    .hashtag(hashtag)
                    .build();

            contentHashtag.createId();

            return contentHashtag;
        }
    }


    // 문자를 입력받아 해시태그 객체를 생성
    private Hashtag createOrGetHashtag(String tag) {

        return hashtagRepository.findByTag(tag).orElseGet(
                        () -> Hashtag.builder()
                                .tag(tag)
                                .build());
    }

    // 문자열을 받아 각각의 단어를 분해 : Set를 이용해 예상치 못한 중복 제거
    private Set<String> parseHashtag (String preHashtag) {

        return Arrays.stream(preHashtag.split("[#,/]"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    // 해시태그를 바탕으로 추천해주기
    public List<ContentResponseDto> recommendContentByUserHashtag (Long userId, List<Content> contents, long offset, int pagesize) {

        User user = getUser(userId);

        List<Content> recommendedContents = calculateSimilarity(user, contents);
        List<ContentResponseDto> res = new ArrayList<>();

        for (int i = (int) offset; i< offset + pagesize; i++) {
            res.add(new ContentResponseDto(recommendedContents.get(i)));
        }

        return res;
    }

    // 해시태그 유사도 계산 부분
    private List<Content> calculateSimilarity (User user, List<Content> contents) {

        // 차이를 저장할 맵
        Map<Double, Content> resultMap = new HashMap<>();

        // 비교군 : 사용자 해시태그
        List<UserHashtag> userHashtags = new ArrayList<>(user.getUserHashtags());

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

                UserHashtag aTag = userHashtags.stream()
                        .filter(userHashtag -> userHashtag.getHashtag().equals(tag))
                        .findAny()
                        .get();

                ContentHashtag bTag = contentHashtags.stream()
                        .filter(contentHashtag -> contentHashtag.getHashtag().equals(tag))
                        .findAny()
                        .get();

                aList.add(aTag.getScore()/aTag.getHashtag().getTier());
                bList.add(bTag.getScore()/bTag.getHashtag().getTier());
            }

            // 단순 유클리안 거리 계산으로 유사도 측정 | 값이 작을 수록 유사도 높음
            resultMap.put(operateEuclideanDistance(aList, bList), content);
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

    //::::::::::::::::::::::::// TOOL BOX //:::::::::::::::::::::::://
    // get Entity
    private User getUser (Long userId) {

        return userService.getUser(userId);
    }

    private Content getContent(Long contentId) {

        return contentService.getContent(contentId);
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new BusinessException(ErrorCode.POST_NOT_FOUND)
        );
    }
}
