package com.sparta.elevenbookshelf.common;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommonErrorResponse {
    private String msg;
    private int status;
    private String error;
    private String path;
    private LocalDateTime timestamp;
}
