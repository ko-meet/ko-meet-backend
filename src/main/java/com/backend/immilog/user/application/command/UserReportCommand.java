package com.backend.immilog.user.application.command;

import com.backend.immilog.user.enums.ReportReason;
import lombok.Builder;

@Builder
public record UserReportCommand(
        ReportReason reason,
        String description
) {
}
