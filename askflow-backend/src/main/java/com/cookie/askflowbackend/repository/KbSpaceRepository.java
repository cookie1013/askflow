package com.cookie.askflowbackend.repository;

import com.cookie.askflowbackend.entity.KbSpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KbSpaceRepository extends JpaRepository<KbSpace, Long> {

    boolean existsByName(String name);

    List<KbSpace> findByStatusOrderByCreatedAtDesc(Integer status);
}