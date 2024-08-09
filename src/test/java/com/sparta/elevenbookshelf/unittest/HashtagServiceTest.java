package com.sparta.elevenbookshelf.unittest;

import com.sparta.elevenbookshelf.domain.hashtag.entity.Hashtag;
import com.sparta.elevenbookshelf.domain.hashtag.repository.HashtagRepository;
import com.sparta.elevenbookshelf.domain.hashtag.service.HashtagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HashtagServiceTest {

    @Mock
    private HashtagRepository hashtagRepository;

    @InjectMocks
    private HashtagService hashtagService;


    @Test
    @DisplayName("해시태그 생성 테스트")
    void updateAndSaveHashtags_shouldParseTagsAndIncrementCountAndSave() {
        // Given
        String preHashtag = "#spring #boot";
        Set<String> parsedTags = new HashSet<>();
        parsedTags.add("spring");
        parsedTags.add("boot");

        Hashtag springHashtag = Hashtag.builder().tag("spring").build();
        Hashtag bootHashtag = Hashtag.builder().tag("boot").build();

        lenient().when(hashtagRepository.findByTag("spring")).thenReturn(Optional.of(springHashtag));
        lenient().when(hashtagRepository.findByTag("boot")).thenReturn(Optional.of(bootHashtag));

        when(hashtagRepository.saveAll(anySet())).thenReturn(List.of(springHashtag, bootHashtag));

        // When
        List<Hashtag> result = hashtagService.updateAndSaveHashtags(preHashtag);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(springHashtag));
        assertTrue(result.contains(bootHashtag));
        assertEquals(1, springHashtag.getCount()); // incrementCount가 호출된 후
        assertEquals(1, bootHashtag.getCount());   // incrementCount가 호출된 후
    }
}
