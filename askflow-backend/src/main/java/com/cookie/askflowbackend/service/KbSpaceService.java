package com.cookie.askflowbackend.service;

import com.cookie.askflowbackend.dto.CreateKbSpaceRequest;
import com.cookie.askflowbackend.dto.KbSpaceResponse;

import java.util.List;

public interface KbSpaceService {

    KbSpaceResponse createSpace(CreateKbSpaceRequest request);

    List<KbSpaceResponse> listSpaces();

    KbSpaceResponse getSpaceDetail(Long id);

    void deleteSpace(Long id);
}