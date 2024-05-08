package com.backend.komeet.service.post;

import com.backend.komeet.domain.Post;
import com.backend.komeet.domain.User;
import com.backend.komeet.dto.request.PostUpdateRequest;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.exception.ErrorCode;
import com.backend.komeet.repository.PostRepository;
import com.backend.komeet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.backend.komeet.enums.PostStatus.*;
import static com.backend.komeet.exception.ErrorCode.*;
import static com.backend.komeet.exception.ErrorCode.USER_INFO_NOT_FOUND;

/**
 * 게시물 업로드 관련 서비스
 */
@RequiredArgsConstructor
@Service
public class PostUpdateService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 게시물을 수정하는 메서드
     *
     * @param userId            사용자 식별자
     * @param postUpdateRequest 게시물 수정 요청 데이터
     */
    @Transactional
    public void updatePost(Long userId,
                           Long postSeq,
                           PostUpdateRequest postUpdateRequest) {

        User user = getUser(userId);
        Post post = getPost(postSeq);

        if (!post.getUser().equals(user)) {
            throw new CustomException(NO_AUTHORITY);
        }
        if (postUpdateRequest.getContent() != null) {
            post.setContent(postUpdateRequest.getContent());
        }
        if (postUpdateRequest.getTitle() != null) {
            post.setTitle(postUpdateRequest.getTitle());
        }
        updateList(
                post.getTags(),
                postUpdateRequest.getAddTags(),
                postUpdateRequest.getDeleteTags()
        );

        updateList(
                post.getAttachments(),
                postUpdateRequest.getAddAttachments(),
                postUpdateRequest.getDeleteAttachments()
        );

        if (postUpdateRequest.getIsPublic() != null) {
            post.setIsPublic(postUpdateRequest.getIsPublic() ? "Y" : "N");
        }

        post.setStatus(MODIFIED);
    }

    /**
     * 리스트를 업데이트하는 메서드
     *
     * @param originList 기존 리스트
     * @param addList    추가 리스트
     * @param deleteList 삭제 리스트
     */
    private void updateList(List<String> originList,
                            List<String> addList,
                            List<String> deleteList) {

        if (addList != null && !addList.isEmpty()) {
            originList.addAll(addList);
        }
        if (deleteList != null && !deleteList.isEmpty()) {
            deleteList.forEach(originList::remove);
        }
    }

    /**
     * 사용자 식별자로 사용자를 조회하는 메서드
     *
     * @param userId 사용자 식별자
     * @return 사용자
     */
    private User getUser(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }

    /**
     * 게시물 식별자로 게시물을 조회하는 메서드
     *
     * @param seq 게시물 식별자
     * @return 게시물
     */
    private Post getPost(Long seq) {
        return postRepository
                .findById(seq)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
    }

    /**
     * 조회수를 증가시키는 메서드
     *
     * @param postSeq 게시물 식별자
     */
    @Transactional
    public void increaseViewCount(Long postSeq) {
        Post post = postRepository
                .findById(postSeq)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

        post.setViewCount(post.getViewCount() + 1);
    }
}
