package com.backend.immilog.post.infrastructure.jpa.entities;

import com.backend.immilog.global.model.BaseDateEntity;
import com.backend.immilog.post.domain.model.JobBoard;
import com.backend.immilog.post.domain.vo.CompanyMetaData;
import com.backend.immilog.post.domain.vo.PostMetaData;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@DynamicUpdate
@Entity
@Table(name = "job_board")
public class JobBoardEntity extends BaseDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    private Long userSeq;

    @Embedded
    private PostMetaData postMetaData;

    @Embedded
    private CompanyMetaData companyMetaData;

    public static JobBoardEntity from(
            JobBoard jobBoard
    ) {
        return JobBoardEntity.builder()
                .seq(jobBoard.seq())
                .userSeq(jobBoard.userSeq())
                .postMetaData(jobBoard.postMetaData())
                .companyMetaData(jobBoard.companyMetaData())
                .build();
    }
}
