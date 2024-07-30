package com.sparta.elevenbookshelf.service;

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

    public List<ContentResponseDto> readContentWebtoon(int offset, int pagesize) {
        List<Content> contents = contentRepository.findByType(Content.ContentType.COMICS);

        return contents.stream()
                .map(ContentResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<ContentResponseDto> readContentWebnovel(int offset, int pagesize) {
        List<Content> contents = contentRepository.findByType(Content.ContentType.NOVEL);

        return contents.stream()
                .map(ContentResponseDto::new)
                .collect(Collectors.toList());
    }


    public List<ContentResponseDto> readContent(int offset, int pagesize) {
//        List<Content> contents = contentRepository.findAllBy("COMICS", offset, pagesize)
        List<Content> contents = contentRepository.findAll();

        return contents.stream()
                .map(ContentResponseDto::new)
                .collect(Collectors.toList());
    }

    public ContentResponseDto readContent(Long cardId) {
        Content content = getContent(cardId);
        return new ContentResponseDto(content);
    }


    //::::::::::::::::::::::::// TOOL BOX //:::::::::::::::::::::::://

    private Content getContent(Long cardId) {
        return contentRepository.findById(cardId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_CONTENT)
        );
    }



}
