package com.backend.immilog.notice.presentation.request;

import com.backend.immilog.notice.model.enums.Countries;
import com.backend.immilog.notice.model.enums.NoticeType;
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