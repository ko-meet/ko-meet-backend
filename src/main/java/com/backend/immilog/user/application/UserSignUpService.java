package com.backend.immilog.user.application;

import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.user.infrastructure.UserRepository;
import com.backend.immilog.user.model.entities.User;
import com.backend.immilog.user.presentation.request.UserSignUpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.immilog.global.exception.ErrorCode.EXISTING_USER;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserSignUpService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Pair<Long, String> signUp(
            UserSignUpRequest userSignUpRequest
    ) {
        validateUserNotExists(userSignUpRequest.getEmail());
        String password = passwordEncoder.encode(userSignUpRequest.getPassword());
        User user = userRepository.save(User.of(userSignUpRequest, password));
        return Pair.of(user.getSeq(), user.getNickName());
    }

    private void validateUserNotExists(
            String email
    ) {
        userRepository
                .findByEmail(email)
                .ifPresent(user -> {
                    throw new CustomException(EXISTING_USER);
                });
    }

}

