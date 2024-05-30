package com.backend.komeet.notice.model.entities;

import com.backend.komeet.base.model.entities.BaseEntity;
import com.backend.komeet.notice.model.dtos.NoticeType;
import com.backend.komeet.notice.presentation.controller.NoticeRegisterRequest;
import com.backend.komeet.post.enums.PostStatus;
import com.backend.komeet.user.enums.Countries;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 공지사항 엔티티
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Entity
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long seq;

    @Setter
    private String title;

    @Setter
    private String content;

    @Setter
    private NoticeType type;

    @Setter
    private PostStatus status;

    @Setter
    @ElementCollection
    @Cascade(CascadeType.ALL)
    private List<Countries> targetCountries;

    @Setter
    @ElementCollection
    @Cascade(CascadeType.ALL)
    private List<Long> readUsers;

    /**
     * Notice 팩토리 메서드
     *
     * @param userSeq
     * @param request {@link NoticeRegisterRequest}
     */
    public static Notice from(Long userSeq, NoticeRegisterRequest request) {
        return Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .type(request.getType())
                .targetCountries(request.getTargetCountries())
                .readUsers(new ArrayList<>(Collections.singleton(userSeq)))
                .build();
    }
}
