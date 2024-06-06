package com.backend.komeet.user.repositories;

import javax.transaction.Transactional;

public interface UserBulkRepository {
    @Transactional
    long updateStatusToDeletedForReportedUsers();
}
