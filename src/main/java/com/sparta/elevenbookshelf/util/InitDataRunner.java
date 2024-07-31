package com.sparta.elevenbookshelf.util;

import com.sparta.elevenbookshelf.entity.Board;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.repository.BoardRepository;
import com.sparta.elevenbookshelf.repository.userRepository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InitDataRunner implements ApplicationRunner {

    @Autowired
    UserRepository userRepository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {

        // 관리자 생성
        if(userRepository.findById(1L).isEmpty()) {
            User admin = new User("ADMIN","관리자", passwordEncoder.encode("1234"), "admin@admin.com", null, User.Status.NORMAL, User.Role.ADMIN);
            userRepository.save(admin);
        }


        // 기본 게시판 생성
        if (boardRepository.findById(1L).isEmpty()) {
            List<Board> boards = new ArrayList<>();

            Board ContentBoard = new Board("ContentBoard");
            boards.add(ContentBoard);
            Board ReviewBoard = new Board("ReviewBoard");
            boards.add(ReviewBoard);
            Board FreeBoard = new Board("FreeBoard");
            boards.add(FreeBoard);

            boardRepository.saveAll(boards);
        }

    }
}
