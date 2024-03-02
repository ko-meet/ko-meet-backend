package com.backend.komeet.dto.request;

import com.backend.komeet.enums.Countries;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoUpdateRequest {
    private String nickName;
    private String profileImage;
    private Countries country;
    private Double latitude;
    private Double longitude;
}
