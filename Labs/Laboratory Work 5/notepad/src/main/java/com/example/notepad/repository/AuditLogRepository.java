package com.example.notepad.repository;

import com.example.notepad.model.AuditLog;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class AuditLogRepository {

    private final JdbcClient jdbcClient;

    public AuditLogRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void save(AuditLog log) {
        // JdbcClient
        jdbcClient.sql("INSERT INTO audit_logs(action, note_id, timestamp) VALUES(?, ?, ?)")
                .params(log.getAction(), log.getNoteId(), log.getTimestamp())
                .update();
    }
}