package com.cookie.askflowbackend.repository;

import com.cookie.askflowbackend.entity.QaSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QaSessionRepository extends JpaRepository<QaSession, Long> {

    List<QaSession> findByStatusOrderByUpdatedAtDesc(Integer status);
}