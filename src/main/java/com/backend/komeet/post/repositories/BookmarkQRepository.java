package com.backend.komeet.post.repositories;

import com.backend.komeet.post.model.entities.JobBoard;
import com.backend.komeet.post.model.entities.Post;

import java.util.List;

public interface BookmarkQRepository {
    List<Post> getBookmarkPostsByUserSeq(
            Long userSeq
    );

    List<JobBoard> getBookmarkJobBoardsByUserSeq(
            Long userSeq
    );
}
