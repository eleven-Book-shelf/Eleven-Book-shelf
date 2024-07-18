package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    //::::::::::::::::::::::::// tool box  //:::::::::::::::::::::::://

    private User getUser(Long userId){
        return userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USERNAME_NOT_FOUND)
        );
    }

}
