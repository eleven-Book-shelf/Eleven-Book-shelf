package com.sparta.elevenbookshelf.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Basic
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 접근입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근이 금지 되었습니다."),
    NOTFOUND(HttpStatus.NOT_FOUND, "페이지를 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 오류가 발생했습니다."),

    // Entity Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "게시판을 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시물을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),

    //Token
    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "토큰이 없습니다."),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "토큰이 만료 되었습니다."),
    TOKEN_INVALID(HttpStatus.BAD_REQUEST, "토큰이 유효하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "리프레쉬 토큰이 만료 되었습니다."),

    //User
    ALREADY_EXISTING_USER(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디 입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다."),
    ALREADY_DELETED(HttpStatus.FORBIDDEN, "이미 탈퇴한 사용자 입니다."),
    ALREADY_BLOCK(HttpStatus.FORBIDDEN, "이미 차단된 사용자 입니다."),

    //Like
    LIKE_ME(HttpStatus.BAD_REQUEST, "자기 자신이 작성한 글에 좋아요 할 수 없습니다."),
    ALREADY_LIKE(HttpStatus.BAD_REQUEST, "이미 좋아요 하였습니다."),
    NOT_LIKE(HttpStatus.BAD_REQUEST, "좋아요 하지 않았습니다."),

    POST_INVALID(HttpStatus.BAD_REQUEST, "잘못된 게시글 요청입니다." ),

    //Content
    NOT_FOUND_CONTENT(HttpStatus.BAD_REQUEST, "컨탠츠를 찾을 수 없습니다." ),
    BOOKMARK_NOT_FOUND(HttpStatus.BAD_REQUEST, "북마크를 찾을 수 없습니다." ),

    //payment
    PAYMENT_FAILURE(HttpStatus.BAD_REQUEST, "결제를 실패 하였습니다." ),
    PAYMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "잘못된 아이디 정보입니다." ),
    PAYMENT_APPROVAL_FAILURE(HttpStatus.BAD_REQUEST, "결제 승인을 실패했습니다." ),
    PAYMENT_CANCELLATION_FAILURE(HttpStatus.BAD_REQUEST, "결제 취소를 실패하였습니다."),

    //Report
    NOT_FOUND_REPORT(HttpStatus.BAD_REQUEST, "신고내용을 찾을 수 없습니다." )

    ;

    private final HttpStatus status;
    private final String message;
}
