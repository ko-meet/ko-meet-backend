package com.backend.komeet.post.model.entities.metadata;

import com.backend.komeet.post.enums.Experience;
import com.backend.komeet.post.enums.Industry;
import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class CompanyMetaData {
    @Enumerated(EnumType.STRING)
    private Industry industry;

    private LocalDateTime deadline;
    private Experience experience;
    private String salary;
    private String company;
    private String companyEmail;
    private String companyPhone;
    private String companyAddress;
    private String companyHomepage;
    private String companyLogo;
}
