package com.sparta.elevenbookshelf.security.principal;

import com.sparta.elevenbookshelf.domain.user.entity.User;
import com.sparta.elevenbookshelf.domain.user.repository.UserRepository;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return new UserPrincipal(user);
    }
}
