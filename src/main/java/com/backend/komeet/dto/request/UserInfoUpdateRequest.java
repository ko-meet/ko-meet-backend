package com.backend.komeet.dto.request;

import com.backend.komeet.enums.Countries;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoUpdateRequest {

    private String nickName;
    private Countries country;
    private Double latitude;
    private Double longitude;
}
