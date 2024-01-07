package com.backend.komeet.enums;

/**
 * 이메일 관련 상수
 */
public class EmailComponents {
    public static final String EMAIL_SIGN_UP_SUBJECT =
            "ko-meet 회원가입 인증 메일";
    public static final String EMAIL_SIGN_UP_CONTENT =
            "아래 링크를 클릭하시면 회원가입 인증이 완료됩니다.\n" +
                    "http://localhost:8080/api/v1/users/%d/verification";
    public static final String PASSWORD_RESET_SUBJECT =
            "ko-meet 비밀번호가 초기화 되었습니다.";
    public static final String PASSWORD_RESET_CONTENT =
            "아래 임시 비밀번호를 통해 로그인 후 비밀번호를 변경해주세요.\n 임시 비밀번호 : %s";
}
