package com.sparta.elevenbookshelf.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class UserRequestDto {

    private String username;
    private String password;
    private String email;
}
