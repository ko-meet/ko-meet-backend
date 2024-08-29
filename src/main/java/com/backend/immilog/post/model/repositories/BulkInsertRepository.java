package com.backend.immilog.post.model.repositories;

import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.function.BiConsumer;

public interface BulkInsertRepository {
    @Transactional
    <T> void saveAll(
            List<T> entities,
            String sqlCommand,
            BiConsumer<PreparedStatement, T> setStatement
    );
}


