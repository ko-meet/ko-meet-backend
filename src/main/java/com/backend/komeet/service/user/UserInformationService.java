package com.backend.komeet.service.user;

import com.backend.komeet.domain.User;
import com.backend.komeet.dto.request.UserInfoUpdateRequest;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.repository.UserRepository;
import com.backend.komeet.util.CountryUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

import static com.backend.komeet.exception.ErrorCode.USER_INFO_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class UserInformationService {
    private final UserRepository userRepository;

    /**
     * 사용자 정보 수정
     *
     * @param userSeq               사용자 번호
     * @param country               나라 정보
     * @param userInfoUpdateRequest 사용자 정보 수정 요청
     */
    @Transactional
    public void updateInformation(Long userSeq,
                                  CompletableFuture<Pair<String, String>> country,
                                  UserInfoUpdateRequest userInfoUpdateRequest) {

        User user = userRepository.findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        if (userInfoUpdateRequest.getNickName() != null) {
            user.setNickName(userInfoUpdateRequest.getNickName());
        }

        if (userInfoUpdateRequest.getCountry() != null) {
            Pair<String, String> countryPair =
                    CountryUtil.fetchLocation(country);
            user.setRegion(countryPair.getSecond());
            user.setCountry(userInfoUpdateRequest.getCountry());
        }
    }
}