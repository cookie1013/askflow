package com.cookie.askflowbackend.repository;

import com.cookie.askflowbackend.entity.QaSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QaSessionRepository extends JpaRepository<QaSession, Long> {

    List<QaSession> findByStatusOrderByUpdatedAtDesc(Integer status);

    List<QaSession> findBySpaceIdAndStatusOrderByUpdatedAtDesc(Long spaceId, Integer status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE QaSession s
            SET s.status = 0
            WHERE s.id = :sessionId
              AND s.status = 1
            """)
    int softDeleteActiveSession(@Param("sessionId") Long sessionId);
}