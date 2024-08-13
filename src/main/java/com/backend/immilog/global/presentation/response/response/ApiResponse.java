package com.backend.immilog.global.presentation.response.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse {
    private Integer status;
    private String message = "success";
    private Object data;
    private List<Object> list;

    public ApiResponse(
            Integer status
    ) {
        this.status = status;
    }

    public ApiResponse(
            Integer status,
            String message
    ) {
        this.status = status;
        this.message = message;
    }

    public ApiResponse(
            Object data
    ) {
        this.status = 200;
        this.data = data;
    }

    public ApiResponse(
            List<Object> list
    ) {
        this.status = 200;
        this.list = list;
    }
}
