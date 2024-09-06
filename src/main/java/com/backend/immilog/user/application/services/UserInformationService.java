package com.backend.immilog.user.application.services;

import com.backend.immilog.global.application.ImageService;
import com.backend.immilog.user.application.command.UserInfoUpdateCommand;
import com.backend.immilog.user.application.command.UserPasswordChangeCommand;
import com.backend.immilog.user.domain.model.User;
import com.backend.immilog.user.domain.model.enums.UserCountry;
import com.backend.immilog.user.domain.model.enums.UserStatus;
import com.backend.immilog.user.domain.repositories.UserRepository;
import com.backend.immilog.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.backend.immilog.global.enums.UserRole.ROLE_ADMIN;
import static com.backend.immilog.user.exception.UserErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInformationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;

    @Transactional
    public void updateInformation(
            Long userSeq,
            CompletableFuture<Pair<String, String>> country,
            UserInfoUpdateCommand userInfoUpdateCommand
    ) {
        User userDomain = getUser(userSeq);
        userDomain = setUserCountryIfItsChanged(country, userInfoUpdateCommand, userDomain);
        userDomain = setUserNickNameIfItsChanged(userInfoUpdateCommand.nickName(), userDomain);
        userDomain = setUserInterestCountryIfItsChanged(userInfoUpdateCommand.interestCountry(), userDomain);
        userDomain = setUserStatusIfItsChanged(userInfoUpdateCommand.status(), userDomain);
        userDomain = setUserImageProfileIfItsChanged(userInfoUpdateCommand.profileImage(), userDomain);
        userRepository.saveEntity(userDomain);
    }

    @Transactional
    public void changePassword(
            Long userSeq,
            UserPasswordChangeCommand userPasswordChangeRequest
    ) {
        User userDomain = getUser(userSeq);
        final String existingPassword = userPasswordChangeRequest.existingPassword();
        final String newPassword = userPasswordChangeRequest.newPassword();
        final String currentPassword = userDomain.password();
        throwExceptionIfPasswordNotMatch(existingPassword, currentPassword);
        User copiedUser = userDomain.copyWithNewPassword(passwordEncoder.encode(newPassword));
        userRepository.saveEntity(copiedUser);
    }

    @Transactional
    public void blockOrUnblockUser(
            Long userSeq,
            Long adminSeq,
            UserStatus userStatus
    ) {
        validateAdminUser(adminSeq);
        User userDomain = getUser(userSeq);
        userDomain = userDomain.copyWithNewUserStatus(userStatus);
        userRepository.saveEntity(userDomain);
    }

    private void validateAdminUser(
            Long userSeq
    ) {
        if (!getUser(userSeq).userRole().equals(ROLE_ADMIN)) {
            throw new UserException(NOT_AN_ADMIN_USER);
        }
    }

    private void throwExceptionIfPasswordNotMatch(
            String existingPassword,
            String currentPassword
    ) {
        if (!passwordEncoder.matches(existingPassword, currentPassword)) {
            throw new UserException(PASSWORD_NOT_MATCH);
        }
    }

    private static User setUserCountryIfItsChanged(
            CompletableFuture<Pair<String, String>> country,
            UserInfoUpdateCommand userInfoUpdateCommand,
            User userDomain
    ) {
        try {
            if (userInfoUpdateCommand.country() != null && country.get() != null) {
                Pair<String, String> countryPair = country.join();
                userDomain.location().setRegion(countryPair.getSecond());
                userDomain.location().setCountry(userInfoUpdateCommand.country());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.info(e.getMessage());
        }
        return userDomain;
    }

    private static User setUserNickNameIfItsChanged(
            String userNickName,
            User userDomain
    ) {
        if (userNickName != null) {
            return userDomain.copyWithNewNickName(userNickName);
        }
        return userDomain;
    }

    private static User setUserInterestCountryIfItsChanged(
            UserCountry interestCountry,
            User userDomain
    ) {
        if (interestCountry != null) {
            return userDomain.copyWithNewInterestCountry(interestCountry);
        }
        return userDomain;
    }

    private static User setUserStatusIfItsChanged(
            UserStatus status,
            User userDomain
    ) {
        if (status != null) {
            return userDomain.copyWithNewUserStatus(status);
        }
        return userDomain;
    }

    private User setUserImageProfileIfItsChanged(
            String profileUrl,
            User userDomain
    ) {
        if (profileUrl != null) {
            imageService.deleteFile(userDomain.imageUrl());
            return userDomain.copyWithNewImageUrl(profileUrl.isEmpty() ? null : profileUrl);
        }
        return userDomain;
    }

    private User getUser(
            Long userSeq
    ) {
        return userRepository.getById(userSeq)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }

}