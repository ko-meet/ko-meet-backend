package com.backend.komeet.base.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 응답 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse {
    private Integer status;
    private String message = "success";
    private Object data;
    private List<Object> list;

    /**
     * 응답 DTO 생성자
     */
    public ApiResponse(
            Integer status
    ) {
        this.status = status;
    }

    /**
     * 응답 DTO 생성자
     */
    public ApiResponse(
            Integer status,
            String message
    ) {
        this.status = status;
        this.message = message;
    }

    /**
     * 응답 DTO 생성자
     */
    public ApiResponse(
            Object data
    ) {
        this.status = 200;
        this.data = data;
    }

    /**
     * 응답 DTO 생성자
     */
    public ApiResponse(
            List<Object> list
    ) {
        this.status = 200;
        this.list = list;
    }
}
