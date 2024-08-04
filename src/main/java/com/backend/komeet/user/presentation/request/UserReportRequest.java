package com.backend.komeet.user.presentation.request;

import com.backend.komeet.user.enums.ReportReason;
import io.swagger.annotations.ApiModel;
import lombok.*;

/**
 * 사용자 신고 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "UserReportRequest", description = "사용자 신고 요청 DTO")
public class UserReportRequest {
    private ReportReason reason;
    private String description;
}
