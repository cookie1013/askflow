package com.cookie.askflowbackend.repository;

import com.cookie.askflowbackend.entity.KbDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KbDocumentRepository extends JpaRepository<KbDocument, Long> {

    List<KbDocument> findBySpaceIdAndStatusOrderByCreatedAtDesc(Long spaceId, Integer status);

    boolean existsBySpaceIdAndTitle(Long spaceId, String title);

    boolean existsBySpaceIdAndTitleAndStatus(Long spaceId, String title, Integer status);

    @Modifying
    @Query("""
            UPDATE KbDocument d
            SET d.status = 0
            WHERE d.spaceId = :spaceId
              AND d.status = 1
            """)
    int softDeleteBySpaceId(@Param("spaceId") Long spaceId);
}