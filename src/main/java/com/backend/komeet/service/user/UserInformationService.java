package com.backend.komeet.service.user;

import com.backend.komeet.config.JwtProvider;
import com.backend.komeet.domain.User;
import com.backend.komeet.dto.TokenIssuanceDto;
import com.backend.komeet.dto.UserDto;
import com.backend.komeet.dto.UserSignInDto;
import com.backend.komeet.dto.request.UserInfoUpdateRequest;
import com.backend.komeet.dto.request.UserPasswordChangeRequest;
import com.backend.komeet.dto.request.UserPasswordResetRequest;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.repository.UserRepository;
import com.backend.komeet.service.external.RedisService;
import com.backend.komeet.util.CountryUtil;
import com.backend.komeet.util.UUIDUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

import static com.backend.komeet.exception.ErrorCode.PASSWORD_NOT_MATCH;
import static com.backend.komeet.exception.ErrorCode.USER_INFO_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class UserInformationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final JwtProvider jwtProvider;
    final int REFRESH_TOKEN_EXPIRE_TIME = 5 * 29 * 24 * 60;
    final String TOKEN_PREFIX = "Refresh: ";

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
        if (userInfoUpdateRequest.getProfileImage() != null) {
            user.setImageUrl(userInfoUpdateRequest.getProfileImage());
        }
        if (userInfoUpdateRequest.getProfileImage() != null) {
            user.setImageUrl(userInfoUpdateRequest.getProfileImage());
        }
    }

    /**
     * 사용자 비밀번호 초기화
     *
     * @param userPasswordResetRequest 사용자 번호
     * @return 사용자 번호
     */
    @Transactional
    public String resetPassword(UserPasswordResetRequest userPasswordResetRequest) {
        User user = userRepository.findByEmail(userPasswordResetRequest.getEmail())
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        if (!user.getCountry().equals(userPasswordResetRequest.getCountry())) {
            throw new CustomException(USER_INFO_NOT_FOUND);
        }

        String temporaryPassword = UUIDUtil.generateUUID(10);
        user.setPassword(passwordEncoder.encode(temporaryPassword));

        return temporaryPassword;
    }

    @Transactional
    public void changePassword(Long userSeq,
                               UserPasswordChangeRequest userPasswordChangeRequest) {
        User user = userRepository.findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        if (!passwordEncoder.matches(
                userPasswordChangeRequest.getExistingPassword(), user.getPassword())
        ) {
            throw new CustomException(PASSWORD_NOT_MATCH);
        }

        user.setPassword(passwordEncoder.encode(
                userPasswordChangeRequest.getNewPassword()));

    }

    /**
     * 사용자 닉네임 중복 확인
     *
     * @param nickname 닉네임
     * @return 중복 여부
     */
    @Transactional(readOnly = true)
    public Boolean checkNickname(String nickname) {
        return !userRepository.findByNickName(nickname).isPresent();
    }

    /**
     * 사용자 정보 조회
     *
     * @param userSeq 사용자 번호
     * @param country CompletableFuture 객체
     * @return 사용자 정보
     */
    @Transactional(readOnly = true)
    public UserSignInDto getUser(Long userSeq, CompletableFuture<Pair<String, String>> country) {

        User user = userRepository.findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        boolean isLocationMatch = false;

        Pair<String, String> countryPair = CountryUtil.fetchLocation(country);

        if (user.getCountry().getCountryName().equals(countryPair.getFirst()) &&
                user.getRegion().equals(countryPair.getSecond())) {
            isLocationMatch = true;
        }

        String accessToken = jwtProvider.issueAccessToken(TokenIssuanceDto.from(user));
        String refreshToken = jwtProvider.issueRefreshToken();

        redisService.saveKeyAndValue(
                TOKEN_PREFIX + refreshToken, user.getEmail(), REFRESH_TOKEN_EXPIRE_TIME
        );

        return UserSignInDto.from(
                user, accessToken, refreshToken, isLocationMatch
        );
    }

    /**
     * 토큰 재발급
     *
     * @param token 토큰
     * @return 토큰
     */
    public Pair<String, String> refreshToken(String token) {
        String userEmail = redisService.getValueByKey(TOKEN_PREFIX + token);
        if (userEmail == null) {
            throw new CustomException(USER_INFO_NOT_FOUND);
        }
        String accessToken = jwtProvider.issueAccessToken(TokenIssuanceDto.from(
                userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND))));

        String refreshToken = jwtProvider.issueRefreshToken();

        redisService.deleteValueByKey(TOKEN_PREFIX + token);
        redisService.saveKeyAndValue(
                TOKEN_PREFIX + refreshToken, userEmail, REFRESH_TOKEN_EXPIRE_TIME);

        return Pair.of(accessToken, refreshToken);
    }
}
