package com.cookie.askflowbackend.service.impl;

import com.cookie.askflowbackend.dto.*;
import com.cookie.askflowbackend.entity.QaMessage;
import com.cookie.askflowbackend.entity.QaSession;
import com.cookie.askflowbackend.repository.QaMessageRepository;
import com.cookie.askflowbackend.repository.QaSessionRepository;
import com.cookie.askflowbackend.repository.KbSpaceRepository;
import com.cookie.askflowbackend.service.QaSessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class QaSessionServiceImpl implements QaSessionService {

    private final QaSessionRepository qaSessionRepository;
    private final QaMessageRepository qaMessageRepository;
    private final KbSpaceRepository kbSpaceRepository;
    private final ObjectMapper objectMapper;

    public QaSessionServiceImpl(QaSessionRepository qaSessionRepository,
                                QaMessageRepository qaMessageRepository,
                                KbSpaceRepository kbSpaceRepository,
                                ObjectMapper objectMapper) {
        this.qaSessionRepository = qaSessionRepository;
        this.qaMessageRepository = qaMessageRepository;
        this.kbSpaceRepository = kbSpaceRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public QaSessionResponse createSession(CreateQaSessionRequest request) {
        if (!kbSpaceRepository.existsById(request.getSpaceId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "space not found");
        }

        QaSession session = new QaSession();
        session.setSpaceId(request.getSpaceId());
        session.setTitle(request.getTitle());

        QaSession saved = qaSessionRepository.save(session);
        return toSessionResponse(saved);
    }

    @Override
    public List<QaSessionResponse> listSessions() {
        return qaSessionRepository.findByStatusOrderByUpdatedAtDesc(1)
                .stream()
                .map(this::toSessionResponse)
                .toList();
    }

    @Override
    public List<QaMessageResponse> listMessages(Long sessionId) {
        if (!qaSessionRepository.existsById(sessionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "session not found");
        }

        return qaMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(this::toMessageResponse)
                .toList();
    }

    @Transactional
    @Override
    public Long resolveSession(Long sessionId, Long spaceId, String question) {
        if (sessionId != null) {
            QaSession session = qaSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "session not found"));

            return session.getId();
        }

        if (!kbSpaceRepository.existsById(spaceId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "space not found");
        }

        QaSession session = new QaSession();
        session.setSpaceId(spaceId);
        session.setTitle(buildTitleFromQuestion(question));

        QaSession saved = qaSessionRepository.save(session);
        return saved.getId();
    }

    @Transactional
    @Override
    public void saveChatMessages(Long sessionId, String question, AiAskResponse response) {
        if (!qaSessionRepository.existsById(sessionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "session not found");
        }

        QaMessage userMessage = new QaMessage();
        userMessage.setSessionId(sessionId);
        userMessage.setRole("USER");
        userMessage.setContent(question);
        qaMessageRepository.save(userMessage);

        QaMessage assistantMessage = new QaMessage();
        assistantMessage.setSessionId(sessionId);
        assistantMessage.setRole("ASSISTANT");
        assistantMessage.setContent(response.getAnswer());
        assistantMessage.setCitationsJson(toJson(response.getCitations()));
        assistantMessage.setDebugJson(toJson(response.getDebug()));
        qaMessageRepository.save(assistantMessage);
    }

    private String buildTitleFromQuestion(String question) {
        if (question == null || question.isBlank()) {
            return "新的问答会话";
        }

        String trimmed = question.trim();
        if (trimmed.length() <= 30) {
            return trimmed;
        }

        return trimmed.substring(0, 30) + "...";
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private QaSessionResponse toSessionResponse(QaSession session) {
        return new QaSessionResponse(
                session.getId(),
                session.getSpaceId(),
                session.getTitle(),
                session.getStatus(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }

    private QaMessageResponse toMessageResponse(QaMessage message) {
        return new QaMessageResponse(
                message.getId(),
                message.getSessionId(),
                message.getRole(),
                message.getContent(),
                message.getCitationsJson(),
                message.getDebugJson(),
                message.getCreatedAt()
        );
    }
}