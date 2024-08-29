package com.backend.immilog.user.model.services;

import com.backend.immilog.user.application.command.UserSignInCommand;
import com.backend.immilog.user.model.dtos.UserSignInDTO;
import org.springframework.data.util.Pair;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

public interface UserSignInService {
    @Transactional(readOnly = true)
    UserSignInDTO signIn(
            UserSignInCommand userSignInRequest,
            CompletableFuture<Pair<String, String>> country
    );

    @Transactional(readOnly = true)
    UserSignInDTO getUserSignInDTO(
            Long userSeq,
            Pair<String, String> country
    );
}
