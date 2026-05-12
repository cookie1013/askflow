package com.cookie.askflowbackend.service;

import com.cookie.askflowbackend.dto.CreateKbChunkRequest;
import com.cookie.askflowbackend.dto.KbChunkResponse;

import java.util.List;

public interface KbChunkService {

    KbChunkResponse createChunk(Long documentId, CreateKbChunkRequest request);

    List<KbChunkResponse> listChunks(Long documentId);

    KbChunkResponse getChunkDetail(Long id);
}