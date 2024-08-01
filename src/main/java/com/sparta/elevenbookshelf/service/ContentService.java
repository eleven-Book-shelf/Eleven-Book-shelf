package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.ContentDataResponseDto;
import com.sparta.elevenbookshelf.dto.ContentResponseDto;
import com.sparta.elevenbookshelf.entity.Content;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.contentRepository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    public List<ContentDataResponseDto> readContent(int offset, int pagesize, String genre) {
        List<Content> contents = contentRepository.getContent(offset, pagesize,genre);

        return contents.stream()
                .map(ContentDataResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<ContentDataResponseDto> readContentWebtoon(int offset, int pagesize, String genre) {
        List<Content> contents = contentRepository.getContentByConic(offset, pagesize, genre);

        return contents.stream()
                .map(ContentDataResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<ContentDataResponseDto> readContentWebnovel(int offset, int pagesize, String genre) {
        List<Content> contents = contentRepository.getContentByNovel(offset, pagesize, genre);

        return contents.stream()
                .map(ContentDataResponseDto::new)
                .collect(Collectors.toList());
    }

    public ContentResponseDto readContent(Long cardId) {
        Content content = getContent(cardId);
        return new ContentResponseDto(content);
    }

    //::::::::::::::::::::::::// User BookMark //:::::::::::::::::::::::://

    public List<ContentDataResponseDto> readContentWebtoonUser(Long userId, int offset, int pagesize, String genre) {
        List<Content> contents = contentRepository.getContentByConicUser(userId, offset, pagesize ,genre);

        return contents.stream()
                .map(ContentDataResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<ContentDataResponseDto> readContentWebnovelUser(Long userId, int offset, int pagesize, String genre) {
        List<Content> contents = contentRepository.getContentByNovelUser(userId, offset, pagesize , genre);

        return contents.stream()
                .map(ContentDataResponseDto::new)
                .collect(Collectors.toList());
    }

    public void viewCount(Long cardId) {
        Content content = contentRepository.findById(cardId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CONTENT));
        content.setViewCount(content.getView() + 1);
        contentRepository.save(content);
    }

    //::::::::::::::::::::::::// TOOL BOX //:::::::::::::::::::::::://

    private Content getContent(Long cardId) {
        return contentRepository.findById(cardId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_CONTENT)
        );
    }


}
