package com.backend.komeet.user.repositories;

import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@RequiredArgsConstructor
public class UserBulkRepositoryImpl implements UserBulkRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public long updateStatusToDeletedForReportedUsers() {
        return entityManager.createQuery(
                "UPDATE User u " +
                        "SET u.userStatus = 'DELETED' " +
                        "WHERE u.reportedCount >= 10"
        ).executeUpdate();
    }
}
