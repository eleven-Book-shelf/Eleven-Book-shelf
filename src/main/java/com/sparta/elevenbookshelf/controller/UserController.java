package com.sparta.elevenbookshelf.controller;

import com.sparta.elevenbookshelf.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "UserController")
public class UserController {

    private final UserService userService;
}
