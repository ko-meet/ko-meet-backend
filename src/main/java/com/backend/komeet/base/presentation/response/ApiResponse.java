package com.backend.komeet.base.presentation.response;

import lombok.*;

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
     * @param status 응답 상태
     */
    public ApiResponse(Integer status){
        this.status = status;
    }

    /**
     * 응답 DTO 생성자
     * @param status 응답 상태
     * @param message 응답 메시지
     */
    public ApiResponse(Integer status, String message){
        this.status = status;
        this.message = message;
    }

    /**
     * 응답 DTO 생성자
     * @param data 응답 데이터
     */
    public ApiResponse(Object data){
        this.status = 200;
        this.data = data;
    }

    /**
     * 응답 DTO 생성자
     * @param list 응답 데이터 리스트
     */
    public ApiResponse(List<Object> list){
        this.status = 200;
        this.list = list;
    }
}
