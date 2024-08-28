package com.backend.immilog.user.model.services;

import com.backend.immilog.user.application.command.UserSignUpCommand;
import org.springframework.data.util.Pair;
import org.springframework.transaction.annotation.Transactional;

public interface UserSignUpService {
    @Transactional
    Pair<Long, String> signUp(
            UserSignUpCommand userSignUpCommand
    );

    @Transactional(readOnly = true)
    Boolean checkNickname(
            String nickname
    );

    @Transactional
    Pair<String, Boolean> verifyEmail(
            Long userSeq
    );
}
