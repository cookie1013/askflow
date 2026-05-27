package com.cookie.askflowbackend.service;

import com.cookie.askflowbackend.dto.*;

import java.util.List;

public interface QaSessionService {

    QaSessionResponse createSession(CreateQaSessionRequest request);

    List<QaSessionResponse> listSessions();

    List<QaMessageResponse> listMessages(Long sessionId);

    Long resolveSession(Long sessionId, Long spaceId, String question);

    void saveChatMessages(Long sessionId, String question, AiAskResponse response);
}