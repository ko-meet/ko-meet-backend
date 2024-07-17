package com.backend.komeet.notice.model.entities;

import com.backend.komeet.base.model.entities.BaseEntity;
import com.backend.komeet.notice.enums.NoticeType;
import com.backend.komeet.notice.presentation.request.NoticeRegisterRequest;
import com.backend.komeet.post.enums.PostStatus;
import com.backend.komeet.user.enums.Countries;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

import static com.backend.komeet.post.enums.PostStatus.NORMAL;
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

    private Long authorUserSeq;

    @Setter
    private String title;

    @Setter
    private String content;

    @Setter
    private NoticeType type;

    @Setter
    private PostStatus status;

    @ElementCollection(fetch = FetchType.LAZY)
    @Cascade(CascadeType.ALL)
    private List<Countries> targetCountries;

    @ElementCollection(fetch = FetchType.LAZY)
    @Cascade(CascadeType.ALL)
    private List<Long> readUsers;

    /**
     * Notice 팩토리 메서드
     *
     * @param userSeq 사용자 고유번호
     * @param request {@link NoticeRegisterRequest}
     */
    public static Notice from(Long userSeq, NoticeRegisterRequest request) {
        return Notice.builder()
                .title(request.getTitle())
                .authorUserSeq(userSeq)
                .content(request.getContent())
                .type(request.getType())
                .targetCountries(request.getTargetCountries())
                .status(NORMAL)
                .build();
    }
}
