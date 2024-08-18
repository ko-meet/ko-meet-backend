package com.backend.immilog.user.presentation.request;

import com.backend.immilog.user.enums.ReportReason;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "UserReportRequest", description = "사용자 신고 요청 DTO")
public class UserReportRequest {
    private ReportReason reason;
    private String description;
}
