package com.backend.komeet.service.reply;

import com.backend.komeet.domain.Comment;
import com.backend.komeet.domain.Reply;
import com.backend.komeet.domain.User;
import com.backend.komeet.dto.request.CommentUploadRequest;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.exception.ErrorCode;
import com.backend.komeet.repository.CommentRepository;
import com.backend.komeet.repository.ReplyRepository;
import com.backend.komeet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.exception.ErrorCode.USER_INFO_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReplyUploadService {
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    /**
     * 대댓글을 업로드하는 메서드
     * @param userId 사용자 식별자
     * @param commentSeq 댓글 식별자
     * @param commentUploadRequest 대댓글 업로드 요청 데이터
     */
    @Transactional
    public void uploadComment(Long userId,
                              Long commentSeq,
                              CommentUploadRequest commentUploadRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        Comment comment = commentRepository.findById(commentSeq)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        Reply reply = replyRepository.save(
                Reply.from(user, comment, commentUploadRequest.getContent())
        );

        comment.getReplies().add(reply);
    }

}
