package com.sparta.elevenbookshelf.domain.admin.service;

import com.sparta.elevenbookshelf.domain.admin.dto.AdminUserRoleUpdateDto;
import com.sparta.elevenbookshelf.domain.admin.dto.AdminUserStatusUpdateDto;
import com.sparta.elevenbookshelf.domain.user.dto.UserResponseDto;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import com.sparta.elevenbookshelf.domain.user.repository.UserRepository;
import com.sparta.elevenbookshelf.domain.user.service.UserService;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserService userService;

    @Transactional
    public void updateUserStatus(Long targetUserId, AdminUserStatusUpdateDto status) {
        User user = userService.getUser(targetUserId);
        user.changeStatus(User.Status.valueOf(status.getStatus()));
    }

    public UserResponseDto getUserById(Long userId) {
        return new UserResponseDto(userService.getUser(userId));
    }
}

