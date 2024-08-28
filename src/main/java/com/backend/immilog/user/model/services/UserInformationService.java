package com.backend.immilog.user.model.services;

import com.backend.immilog.user.application.command.UserInfoUpdateCommand;
import com.backend.immilog.user.application.command.UserPasswordChangeCommand;
import com.backend.immilog.user.model.enums.UserStatus;
import com.backend.immilog.user.model.dtos.UserSignInDTO;
import org.springframework.data.util.Pair;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

public interface UserInformationService {
    @Transactional(readOnly = true)
    UserSignInDTO getUserSignInDTO(
            Long userSeq,
            Pair<String, String> country
    );

    @Transactional
    void updateInformation(
            Long userSeq,
            CompletableFuture<Pair<String, String>> country,
            UserInfoUpdateCommand userInfoUpdateCommand
    );

    @Transactional
    void changePassword(
            Long userSeq,
            UserPasswordChangeCommand userPasswordChangeCommand
    );

    @Transactional
    void blockOrUnblockUser(
            Long userSeq,
            Long adminSeq,
            UserStatus userStatus
    );
}
