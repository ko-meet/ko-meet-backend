package com.backend.immilog.notice.exception;

import com.backend.immilog.global.exception.ErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("NoticeException 테스트")
class NoticeExceptionTest {

    @Test
    @DisplayName("NoticeException 생성 테스트")
    void noticeException_returnsCorrectErrorCode() {
        ErrorCode errorCode1 = NoticeErrorCode.NOTICE_NOT_FOUND;
        ErrorCode errorCode2 = NoticeErrorCode.NOT_AN_ADMIN_USER;
        NoticeException exception1 = new NoticeException(errorCode1);
        NoticeException exception2 = new NoticeException(errorCode2);
        Assertions.assertThat(exception1.getMessage()).isEqualTo(errorCode1.getMessage());
        Assertions.assertThat(exception2.getMessage()).isEqualTo(errorCode2.getMessage());
    }
}