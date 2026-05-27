package com.cookie.askflowbackend.repository;

import com.cookie.askflowbackend.entity.KbDocumentChunk;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KbDocumentChunkRepository extends JpaRepository<KbDocumentChunk, Long> {

    List<KbDocumentChunk> findByDocumentIdAndStatusOrderByChunkIndexAsc(Long documentId, Integer status);

    long countByDocumentIdAndStatus(Long documentId, Integer status);
    void deleteByDocumentId(Long documentId);
    @Query("""
            SELECT c
            FROM KbDocumentChunk c
            WHERE c.spaceId = :spaceId
              AND c.status = 1
              AND c.content LIKE CONCAT('%', :keyword, '%')
            ORDER BY c.documentId ASC, c.chunkIndex ASC
            """)
    List<KbDocumentChunk> searchByKeyword(@Param("spaceId") Long spaceId,
                                          @Param("keyword") String keyword,
                                          Pageable pageable);
}