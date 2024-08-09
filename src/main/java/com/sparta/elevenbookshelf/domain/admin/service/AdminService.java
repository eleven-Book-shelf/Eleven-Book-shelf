package com.sparta.elevenbookshelf.domain.admin.service;

import com.sparta.elevenbookshelf.domain.admin.dto.AdminUserStatusUpdateDto;
import com.sparta.elevenbookshelf.domain.content.dto.ContentAdminResponseDto;
import com.sparta.elevenbookshelf.domain.content.service.ContentService;
import com.sparta.elevenbookshelf.domain.hashtag.dto.HashtagResponseDto;
import com.sparta.elevenbookshelf.domain.hashtag.service.HashtagService;
import com.sparta.elevenbookshelf.domain.post.dto.PostResponseDto;
import com.sparta.elevenbookshelf.domain.post.service.PostService;
import com.sparta.elevenbookshelf.domain.user.dto.UserResponseDto;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import com.sparta.elevenbookshelf.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserService userService;
    private final HashtagService hashtagService;
    private final ContentService contentService;
    private final PostService postService;

    @Transactional
    public void updateUserStatus(Long targetUserId, AdminUserStatusUpdateDto status) {
        User user = userService.getUser(targetUserId);
        user.changeStatus(User.Status.valueOf(status.getStatus()));
    }

    public List<UserResponseDto> getAdminUserPage(int page, int size,String sortBy, boolean asc) {
        return userService.getUserPage(page,size,sortBy,asc).getContent();
    }

    public UserResponseDto getUser(Long userId) {

        return new UserResponseDto(userService.getUser(userId));
    }

    public List<PostResponseDto> getPostPage(int page, int size, String sortBy, boolean asc) {
        return postService.getAdminPage(page,size,sortBy,asc).getContent();
    }

    public void deletePostAdmin(Long postId) {

        postService.deletePostAdmin(postId);
    }

    public List<HashtagResponseDto> getHashtagPage(int page, int size, String sortBy, boolean asc) {
        return hashtagService.getAdminPage(page,size,sortBy,asc).getContent();
    }

    public void updateHashtagPage(Long hashtagId) {
        hashtagService.deleteHashtag(hashtagId);
    }

    public List<ContentAdminResponseDto> getContentPage(int page, int size, String sortBy, boolean asc) {
        return contentService.getContentPage(page,size,sortBy,asc).getContent();
    }

    public void updateContentPage(Long contentId) {
        contentService.updateContentPage(contentId);
    }
}

