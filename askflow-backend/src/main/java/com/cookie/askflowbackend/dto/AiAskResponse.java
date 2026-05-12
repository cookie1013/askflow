package com.cookie.askflowbackend.dto;

import java.util.List;
import java.util.Map;

public class AiAskResponse {

    private String answer;

    private List<CitationResponse> citations;

    private Map<String, Object> debug;

    public AiAskResponse() {
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<CitationResponse> getCitations() {
        return citations;
    }

    public void setCitations(List<CitationResponse> citations) {
        this.citations = citations;
    }

    public Map<String, Object> getDebug() {
        return debug;
    }

    public void setDebug(Map<String, Object> debug) {
        this.debug = debug;
    }
}