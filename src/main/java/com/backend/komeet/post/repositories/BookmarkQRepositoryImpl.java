package com.backend.komeet.post.repositories;

import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.post.model.entities.QBookmark;
import com.backend.komeet.post.model.entities.QPost;
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
        return jpaQueryFactory.selectFrom(post)
                .join(bookmark).on(post.bookmarklist.contains(bookmark)).fetchJoin()
                .where(bookmark.userSeq.eq(userSeq).and(post.postMetaData.status.eq(NORMAL)))
                .orderBy(bookmark.createdAt.desc())
                .fetch();
    }
}
