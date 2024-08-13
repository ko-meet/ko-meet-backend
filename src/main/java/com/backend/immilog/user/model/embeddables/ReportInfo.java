package com.backend.immilog.user.model.embeddables;

import lombok.*;

import javax.persistence.Embeddable;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class ReportInfo {
    private Long reportedCount;
    private Date reportedDate;

    public static ReportInfo of(
            Long reportedCount,
            Date reportedDate
    ) {
        return ReportInfo.builder()
                .reportedCount(reportedCount)
                .reportedDate(reportedDate)
                .build();
    }
}
