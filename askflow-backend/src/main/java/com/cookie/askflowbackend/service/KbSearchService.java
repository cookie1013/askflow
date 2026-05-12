package com.cookie.askflowbackend.service;

import com.cookie.askflowbackend.dto.KbSearchResponse;

import java.util.List;

public interface KbSearchService {

    List<KbSearchResponse> search(Long spaceId, String keyword, Integer limit);
}