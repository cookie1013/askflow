package com.cookie.askflowbackend.repository;

import com.cookie.askflowbackend.entity.QaMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QaMessageRepository extends JpaRepository<QaMessage, Long> {

    List<QaMessage> findBySessionIdOrderByCreatedAtAsc(Long sessionId);

    List<QaMessage> findBySessionIdAndStatusOrderByCreatedAtAsc(Long sessionId, Integer status);

    List<QaMessage> findBySessionIdAndStatus(Long sessionId, Integer status);

    List<QaMessage> findBySessionId(Long sessionId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE QaMessage m
            SET m.status = 0
            WHERE m.sessionId = :sessionId
              AND m.status = 1
            """)
    int softDeleteBySessionId(@Param("sessionId") Long sessionId);
}