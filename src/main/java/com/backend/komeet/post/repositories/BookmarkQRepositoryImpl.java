package com.backend.komeet.post.repositories;

import com.backend.komeet.post.model.entities.*;
import com.backend.komeet.user.model.entities.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.backend.komeet.post.enums.PostStatus.NORMAL;

@RequiredArgsConstructor
@Repository
public class BookmarkQRepositoryImpl implements BookmarkQRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getBookmarkPostsByUserSeq(
            Long userSeq
    ) {
        QBookmark bookmark = QBookmark.bookmark;
        QPost post = QPost.post;
        QUser user = QUser.user;
        return jpaQueryFactory.selectFrom(post)
                .join(bookmark).on(post.bookmarklist.contains(bookmark)).fetchJoin()
                .join(post.user, user).fetchJoin()
                .where(bookmark.userSeq.eq(userSeq).and(post.postMetaData.status.eq(NORMAL)))
                .orderBy(bookmark.createdAt.desc())
                .fetch();
    }

    @Override
    public List<JobBoard> getBookmarkJobBoardsByUserSeq(
            Long userSeq
    ) {
        QBookmark bookmark = QBookmark.bookmark;
        QJobBoard jobBoard = QJobBoard.jobBoard;
        QUser user = QUser.user;
        return jpaQueryFactory.selectFrom(jobBoard)
                .join(bookmark).on(jobBoard.bookmarklist.contains(bookmark)).fetchJoin()
                .join(jobBoard.user, user).fetchJoin()
                .where(bookmark.userSeq.eq(userSeq).and(jobBoard.postMetaData.status.eq(NORMAL)))
                .orderBy(bookmark.createdAt.desc())
                .fetch();
    }
}
