package com.cookie.askflowbackend.repository;

import com.cookie.askflowbackend.entity.KbDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KbDocumentRepository extends JpaRepository<KbDocument, Long> {

    List<KbDocument> findBySpaceIdAndStatusOrderByCreatedAtDesc(Long spaceId, Integer status);

    boolean existsBySpaceIdAndTitle(Long spaceId, String title);
}