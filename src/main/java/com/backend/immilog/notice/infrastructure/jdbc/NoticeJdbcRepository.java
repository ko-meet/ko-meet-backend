package com.backend.immilog.notice.infrastructure.jdbc;

import com.backend.immilog.notice.application.result.NoticeResult;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeJdbcRepository {
    private final JdbcClient jdbcClient;

    public List<NoticeResult> getNotices(
            long userSeq,
            int pageSize,
            long offset
    ) {
        String sql = """
                     SELECT n.* 
                     FROM notice n
                     LEFT JOIN notice_entity_target_countries ntc ON n.seq = ntc.notice_entity_seq
                     LEFT JOIN user u ON JSON_CONTAINS(ntc.target_countries, JSON_QUOTE(u.country))
                     LEFT JOIN notice_entity_read_users nru ON n.seq = nru.notice_entity_seq
                     WHERE (JSON_CONTAINS(ntc.target_countries, JSON_QUOTE(u.country))
                        OR JSON_CONTAINS(ntc.target_countries, JSON_QUOTE('ALL')))
                        AND n.status = 'NORMAL'
                        AND NOT (JSON_CONTAINS(nru.read_users, CAST(? AS JSON)))
                     ORDER BY n.created_at DESC
                     LIMIT ? OFFSET ?
                     """;

        return jdbcClient.sql(sql)
                .param(userSeq)
                .param(pageSize)
                .param(offset)
                .query((rs, rowNum) -> new NoticeResult(rs))
                .list();
    }

    public Long getTotal(
            Long userSeq
    ) {
        String sql = """
                     SELECT COUNT(*)
                     FROM notice n
                     LEFT JOIN notice_entity_target_countries ntc ON n.seq = ntc.notice_entity_seq
                     LEFT JOIN user u ON JSON_CONTAINS(ntc.target_countries, JSON_QUOTE(u.country))
                     LEFT JOIN notice_entity_read_users nru ON n.seq = nru.notice_entity_seq
                     WHERE (JSON_CONTAINS(ntc.target_countries, JSON_QUOTE(u.country))
                        OR JSON_CONTAINS(ntc.target_countries, JSON_QUOTE('ALL')))
                        AND n.status = 'NORMAL'
                        AND NOT (JSON_CONTAINS(nru.read_users, CAST(? AS JSON)))
                     """;
        return jdbcClient.sql(sql)
                .param(userSeq)
                .query((rs, rowNum) -> rs.getLong(1))
                .single();
    }

}