package com.backend.immilog.notice.model.entities;

import com.backend.immilog.global.model.BaseDateEntity;
import com.backend.immilog.notice.model.enums.NoticeCountry;
import com.backend.immilog.notice.model.enums.NoticeStatus;
import com.backend.immilog.notice.model.enums.NoticeType;
import com.backend.immilog.notice.presentation.request.NoticeRegisterRequest;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

import static com.backend.immilog.notice.model.enums.NoticeStatus.NORMAL;
import static javax.persistence.GenerationType.IDENTITY;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@DynamicUpdate
@Entity
public class Notice extends BaseDateEntity {
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

    public static Notice of(
            Long userSeq,
            NoticeRegisterRequest request
    ) {
        return Notice.builder()
                .title(request.getTitle())
                .userSeq(userSeq)
                .content(request.getContent())
                .type(request.getType())
                .targetCountries(request.getTargetCountries())
                .status(NORMAL)
                .build();
    }
}
