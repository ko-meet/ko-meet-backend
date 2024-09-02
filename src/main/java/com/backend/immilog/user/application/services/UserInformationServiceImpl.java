package com.backend.immilog.user.application.services;

import com.backend.immilog.global.application.ImageService;
import com.backend.immilog.user.application.command.UserInfoUpdateCommand;
import com.backend.immilog.user.application.command.UserPasswordChangeCommand;
import com.backend.immilog.user.exception.UserException;
import com.backend.immilog.user.model.entities.User;
import com.backend.immilog.user.model.enums.UserCountry;
import com.backend.immilog.user.model.enums.UserStatus;
import com.backend.immilog.user.model.repositories.UserRepository;
import com.backend.immilog.user.model.services.UserInformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.backend.immilog.global.enums.UserRole.ROLE_ADMIN;
import static com.backend.immilog.user.exception.UserErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInformationServiceImpl implements UserInformationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;

    @Override
    public void updateInformation(
            Long userSeq,
            CompletableFuture<Pair<String, String>> country,
            UserInfoUpdateCommand userInfoUpdateCommand
    ) {
        User user = getUser(userSeq);
        setUserCountryIfItsChanged(country, userInfoUpdateCommand, user);
        setUserNickNameIfItsChanged(userInfoUpdateCommand.nickName(), user);
        setUserInterestCountryIfItsChanged(userInfoUpdateCommand.interestCountry(), user);
        setUserStatusIfItsChanged(userInfoUpdateCommand.status(), user);
        setUserImageProfileIfItsChanged(userInfoUpdateCommand.profileImage(), user);
    }

    @Override
    public void changePassword(
            Long userSeq,
            UserPasswordChangeCommand userPasswordChangeRequest
    ) {
        User user = getUser(userSeq);
        String existingPassword = userPasswordChangeRequest.existingPassword();
        String newPassword = userPasswordChangeRequest.newPassword();
        String currentPassword = user.getPassword();
        throwExceptionIfPasswordNotMatch(existingPassword, currentPassword);
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    @Override
    public void blockOrUnblockUser(
            Long userSeq,
            Long adminSeq,
            UserStatus userStatus
    ) {
        validateAdminUser(adminSeq);
        User user = getUser(userSeq);
        user.setUserStatus(userStatus);
    }

    private void validateAdminUser(
            Long userSeq
    ) {
        if (!getUser(userSeq).getUserRole().equals(ROLE_ADMIN)) {
            throw new UserException(NOT_AN_ADMIN_USER);
        }
        ;
    }

    private void throwExceptionIfPasswordNotMatch(
            String existingPassword,
            String currentPassword
    ) {
        if (!passwordEncoder.matches(existingPassword, currentPassword)) {
            throw new UserException(PASSWORD_NOT_MATCH);
        }
    }

    private static void setUserCountryIfItsChanged(
            CompletableFuture<Pair<String, String>> country,
            UserInfoUpdateCommand userInfoUpdateCommand,
            User user
    ) {
        try {
            if (userInfoUpdateCommand.country() != null && country.get() != null) {
                Pair<String, String> countryPair = country.join();
                user.getLocation().setRegion(countryPair.getSecond());
                user.getLocation().setCountry(userInfoUpdateCommand.country());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.info(e.getMessage());
        }
    }

    private static void setUserNickNameIfItsChanged(
            String userNickName,
            User user
    ) {
        if (userNickName != null) {
            user.setNickName(userNickName);
        }
    }

    private static void setUserInterestCountryIfItsChanged(
            UserCountry interestCountry,
            User user
    ) {
        if (interestCountry != null) {
            user.setInterestCountry(interestCountry);
        }
    }

    private static void setUserStatusIfItsChanged(
            UserStatus status,
            User user
    ) {
        if (status != null) {
            user.setUserStatus(status);
        }
    }

    private void setUserImageProfileIfItsChanged(
            String profileUrl,
            User user
    ) {
        if (profileUrl != null) {
            imageService.deleteFile(user.getImageUrl());
            user.setImageUrl(profileUrl.isEmpty() ? null : profileUrl);
        }
    }

    private User getUser(
            Long userSeq
    ) {
        return userRepository.findById(userSeq)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }

}