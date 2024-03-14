package com.backend.komeet.service.jobboard;

import com.backend.komeet.repository.JobBoardRepository;
import com.backend.komeet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class JobBoardDetailService {
    private final JobBoardRepository jobBoardRepository;
    private final UserRepository userRepository;

}
