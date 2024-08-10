package com.backend.komeet.post.application.post;

import com.backend.komeet.global.exception.CustomException;
import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.post.presentation.request.PostUpdateRequest;
import com.backend.komeet.post.repositories.PostRepository;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.backend.komeet.global.exception.ErrorCode.*;
import static com.backend.komeet.post.enums.PostStatus.MODIFIED;

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
     */
    @Transactional
    public void updatePost(
            Long userId,
            Long postSeq,
            PostUpdateRequest postUpdateRequest
    ) {

        User user = getUser(userId);
        Post post = getPost(postSeq);

        if (!post.getUser().equals(user)) {
            throw new CustomException(NO_AUTHORITY);
        }
        if (postUpdateRequest.getContent() != null) {
            post.getPostMetaData().setContent(postUpdateRequest.getContent());
        }
        if (postUpdateRequest.getTitle() != null) {
            post.getPostMetaData().setTitle(postUpdateRequest.getTitle());
        }
        updateList(
                post.getPostMetaData().getTags(),
                postUpdateRequest.getAddTags(),
                postUpdateRequest.getDeleteTags()
        );

        updateList(
                post.getPostMetaData().getAttachments(),
                postUpdateRequest.getAddAttachments(),
                postUpdateRequest.getDeleteAttachments()
        );

        if (postUpdateRequest.getIsPublic() != null) {
            post.setIsPublic(postUpdateRequest.getIsPublic() ? "Y" : "N");
        }

        post.getPostMetaData().setStatus(MODIFIED);
    }

    /**
     * 리스트를 업데이트하는 메서드
     */
    private void updateList(
            List<String> originList,
            List<String> addList,
            List<String> deleteList
    ) {

        if (addList != null && !addList.isEmpty()) {
            originList.addAll(addList);
        }
        if (deleteList != null && !deleteList.isEmpty()) {
            deleteList.forEach(originList::remove);
        }
    }

    /**
     * 사용자 식별자로 사용자를 조회하는 메서드
     */
    private User getUser(
            Long userId
    ) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }

    /**
     * 게시물 식별자로 게시물을 조회하는 메서드
     */
    private Post getPost(
            Long seq
    ) {
        return postRepository
                .findById(seq)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
    }

    /**
     * 조회수를 증가시키는 메서드
     */
    @Transactional
    public void increaseViewCount(
            Long postSeq
    ) {
        Post post = postRepository
                .findById(postSeq)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

        post.getPostMetaData().setViewCount(post.getPostMetaData().getViewCount() + 1);
    }
}
