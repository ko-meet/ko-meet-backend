package com.backend.komeet.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * 에러 코드
 */
@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    //undefined
    UNDEFINED_EXCEPTION(BAD_REQUEST, "알 수 없는 오류가 발생하였습니다."),

    //user
    USER_INFO_NOT_FOUND(NOT_FOUND, "존재하지 않는 사용자입니다."),
    EXISTING_USER(BAD_REQUEST, "이미 존재하는 사용자입니다."),
    PASSWORD_NOT_MATCH(BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    USER_STATUS_NOT_ACTIVE(BAD_REQUEST, "사용자 상태가 활성화되어 있지 않습니다."),
    LOCATION_NOT_MATCH(BAD_REQUEST, "현재 위치가 입력하신 위치와 일치하지 않습니다."),

    //image
    IMAGE_UPLOAD_FAILED(BAD_REQUEST, "이미지 업로드에 실패하였습니다."),

    //post
    POST_NOT_FOUND(NOT_FOUND, "존재하지 않는 게시물입니다."),
    NO_AUTHORITY(BAD_REQUEST, "본인이 작성한 글만 수정하거나 삭제할 수 있습니다."),
    ALREADY_DELETED_POST(BAD_REQUEST, "이미 삭제된 게시물입니다."),

    COMMENT_NOT_FOUND(NOT_FOUND, "존재하지 않는 댓글입니다.");

    private final HttpStatus status;
    private final String message;

}
