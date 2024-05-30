package com.backend.komeet.notice.presentation.controller;

import com.backend.komeet.notice.model.dtos.NoticeType;
import com.backend.komeet.user.enums.Countries;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeRegisterRequest {
    private String title;
    private String content;
    private NoticeType type;
    private List<Countries> targetCountries;
}
