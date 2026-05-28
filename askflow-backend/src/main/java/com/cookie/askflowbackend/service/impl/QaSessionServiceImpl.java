package com.cookie.askflowbackend.service.impl;

import com.cookie.askflowbackend.dto.*;
import com.cookie.askflowbackend.entity.KbSpace;
import com.cookie.askflowbackend.entity.QaMessage;
import com.cookie.askflowbackend.entity.QaSession;
import com.cookie.askflowbackend.repository.KbSpaceRepository;
import com.cookie.askflowbackend.repository.QaMessageRepository;
import com.cookie.askflowbackend.repository.QaSessionRepository;
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
        KbSpace space = kbSpaceRepository.findById(request.getSpaceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "space not found"));

        if (!Integer.valueOf(1).equals(space.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "space not found");
        }

        QaSession session = new QaSession();
        session.setSpaceId(request.getSpaceId());
        session.setTitle(request.getTitle());
        session.setStatus(1);
        session.setMessageCount(0);

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

    /**
     * 新增：按 Space 查询会话列表。
     * 这个方法用于 GET /api/qa/sessions?spaceId=1 这种接口。
     */
    @Override
    public List<QaSessionResponse> listSessions(Long spaceId) {
        if (spaceId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "spaceId cannot be null");
        }

        KbSpace space = kbSpaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "space not found"));

        if (!Integer.valueOf(1).equals(space.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "space not found");
        }

        return qaSessionRepository.findBySpaceIdAndStatusOrderByUpdatedAtDesc(spaceId, 1)
                .stream()
                .map(this::toSessionResponse)
                .toList();
    }

    @Override
    public List<QaMessageResponse> listMessages(Long sessionId) {
        QaSession session = qaSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "session not found"));

        if (!Integer.valueOf(1).equals(session.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "session not found");
        }

        return qaMessageRepository.findBySessionIdAndStatusOrderByCreatedAtAsc(sessionId, 1)
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

            if (!Integer.valueOf(1).equals(session.getStatus())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "session not found");
            }

            return session.getId();
        }

        KbSpace space = kbSpaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "space not found"));

        if (!Integer.valueOf(1).equals(space.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "space not found");
        }

        QaSession session = new QaSession();
        session.setSpaceId(spaceId);
        session.setTitle(buildTitleFromQuestion(question));
        session.setStatus(1);
        session.setMessageCount(0);

        QaSession saved = qaSessionRepository.save(session);
        return saved.getId();
    }

    @Transactional
    @Override
    public void saveChatMessages(Long sessionId, String question, AiAskResponse response) {
        QaSession session = qaSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "session not found"));

        if (!Integer.valueOf(1).equals(session.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "session not found");
        }

        QaMessage userMessage = new QaMessage();
        userMessage.setSessionId(sessionId);
        userMessage.setSpaceId(session.getSpaceId());
        userMessage.setRole("USER");
        userMessage.setContent(question);
        userMessage.setStatus(1);

        QaMessage assistantMessage = new QaMessage();
        assistantMessage.setSessionId(sessionId);
        assistantMessage.setSpaceId(session.getSpaceId());
        assistantMessage.setRole("ASSISTANT");
        assistantMessage.setContent(response.getAnswer());
        assistantMessage.setCitationsJson(toJson(response.getCitations()));
        assistantMessage.setDebugJson(toJson(response.getDebug()));
        assistantMessage.setStatus(1);

        qaMessageRepository.save(userMessage);
        qaMessageRepository.save(assistantMessage);

        Integer currentCount = session.getMessageCount() == null ? 0 : session.getMessageCount();
        session.setMessageCount(currentCount + 2);
        qaSessionRepository.save(session);
    }

    /**
     * 新增：软删除会话。
     * 删除会话时，同时把该会话下的消息也置为无效。
     */
    @Transactional
    @Override
    public void deleteSession(Long sessionId) {
        QaSession session = qaSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "session not found"));

        if (!Integer.valueOf(1).equals(session.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "session not found");
        }

        int updatedSessionCount = qaSessionRepository.softDeleteActiveSession(sessionId);

        if (updatedSessionCount == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "session not found");
        }

        qaMessageRepository.softDeleteBySessionId(sessionId);
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