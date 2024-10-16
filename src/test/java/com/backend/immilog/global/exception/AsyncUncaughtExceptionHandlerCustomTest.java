package com.backend.immilog.global.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("AsyncUncaughtExceptionHandlerCustom 클래스 테스트")
class AsyncUncaughtExceptionHandlerCustomTest {

    private final AsyncUncaughtExceptionHandlerCustom handler = new AsyncUncaughtExceptionHandlerCustom();

    @Test
    @DisplayName("비동기 예외 처리 - 정상 동작")
    void handleUncaughtException_logsErrorAndThrowsRuntimeException() throws NoSuchMethodException {
        // given
        Throwable ex = new RuntimeException("Test Exception");
        Method method = this.getClass().getDeclaredMethod("handleUncaughtException_logsErrorAndThrowsRuntimeException");
        Object[] params = {"param1", 123};

        // when & then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> handler.handleUncaughtException(ex, method, params));
        assertThat(thrown.getMessage()).isEqualTo("Test Exception");
    }

    @Test
    @DisplayName("비동기 예외 처리 - 파라미터가 없는 경우")
    void handleUncaughtException_noParams() throws NoSuchMethodException {
        // given
        Throwable ex = new RuntimeException("Test Exception");
        Method method = this.getClass().getDeclaredMethod("handleUncaughtException_noParams");

        // when & then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> handler.handleUncaughtException(ex, method));
        assertThat(thrown.getMessage()).isEqualTo("Test Exception");
    }

    @Test
    @DisplayName("비동기 예외 처리 - 예외 메시지가 없는 경우")
    void handleUncaughtException_noMessage() throws NoSuchMethodException {
        // given
        Throwable ex = new RuntimeException();
        Method method = this.getClass().getDeclaredMethod("handleUncaughtException_noMessage");
        Object[] params = {"param1", 123};

        // when & then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> handler.handleUncaughtException(ex, method, params));
        assertThat(thrown.getMessage()).isNull();
    }
}
