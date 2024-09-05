package com.backend.immilog.notice.infrastructure.jpa.entities;

import com.backend.immilog.global.model.BaseDateEntity;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.model.enums.NoticeCountry;
import com.backend.immilog.notice.domain.model.enums.NoticeStatus;
import com.backend.immilog.notice.domain.model.enums.NoticeType;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@DynamicUpdate
@Entity
@Table(name = "notice")
public class NoticeEntity extends BaseDateEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long seq;

    private Long userSeq;

    @Setter
    private String title;

    @Setter
    private String content;

    @Setter
    private NoticeType type;

    @Setter
    private NoticeStatus status;

    @ElementCollection(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<NoticeCountry> targetCountries;

    @ElementCollection(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Long> readUsers;

    public static NoticeEntity from(
            Notice notice
    ) {
        return NoticeEntity.builder()
                .seq(notice.seq())
                .userSeq(notice.userSeq())
                .title(notice.title())
                .content(notice.content())
                .type(notice.type())
                .status(notice.status())
                .targetCountries(notice.targetCountries())
                .readUsers(notice.readUsers())
                .build();
    }

    public Notice toDomain(
    ) {
        return Notice.builder()
                .seq(this.getSeq())
                .userSeq(this.getUserSeq())
                .title(this.getTitle())
                .content(this.getContent())
                .type(this.getType())
                .status(this.getStatus())
                .targetCountries(this.getTargetCountries())
                .readUsers(this.getReadUsers())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }
}

