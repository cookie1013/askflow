package com.cookie.askflowbackend.repository;

import com.cookie.askflowbackend.entity.KbSpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KbSpaceRepository extends JpaRepository<KbSpace, Long> {

    List<KbSpace> findByStatusOrderByCreatedAtDesc(Integer status);

    boolean existsByNameAndStatus(String name, Integer status);
}