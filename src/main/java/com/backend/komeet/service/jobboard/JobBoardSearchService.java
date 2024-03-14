package com.backend.komeet.service.jobboard;

import com.backend.komeet.domain.JobBoard;
import com.backend.komeet.domain.User;
import com.backend.komeet.dto.JobBoardDto;
import com.backend.komeet.dto.request.JobBoardUploadRequest;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.repository.JobBoardRepository;
import com.backend.komeet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.backend.komeet.exception.ErrorCode.USER_INFO_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class JobBoardSearchService {
    private final JobBoardRepository jobBoardRepository;
    private final UserRepository userRepository;

}
