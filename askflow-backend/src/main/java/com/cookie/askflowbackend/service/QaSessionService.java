package com.cookie.askflowbackend.service;

import com.cookie.askflowbackend.dto.AiAskResponse;
import com.cookie.askflowbackend.dto.CreateQaSessionRequest;
import com.cookie.askflowbackend.dto.QaMessageResponse;
import com.cookie.askflowbackend.dto.QaSessionResponse;

import java.util.List;

public interface QaSessionService {

    QaSessionResponse createSession(CreateQaSessionRequest request);

    List<QaSessionResponse> listSessions();

    List<QaSessionResponse> listSessions(Long spaceId);

    List<QaMessageResponse> listMessages(Long sessionId);

    Long resolveSession(Long sessionId, Long spaceId, String question);

    void saveChatMessages(Long sessionId, String question, AiAskResponse response);

    void deleteSession(Long sessionId);
}