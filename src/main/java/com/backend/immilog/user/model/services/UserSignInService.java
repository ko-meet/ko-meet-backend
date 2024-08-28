package com.backend.immilog.user.model.services;

import com.backend.immilog.user.application.command.UserSignInCommand;
import com.backend.immilog.user.model.dtos.UserSignInDTO;
import org.springframework.data.util.Pair;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

public interface UserSignInService {
    @Transactional
    UserSignInDTO signIn(
            UserSignInCommand userSignInRequest,
            CompletableFuture<Pair<String, String>> country
    );
}
