package com.cookie.askflowbackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class BatchCreateRagEvalCaseRequest {

    @Valid
    @NotEmpty(message = "cases cannot be empty")
    private List<CreateRagEvalCaseRequest> cases;

    public List<CreateRagEvalCaseRequest> getCases() {
        return cases;
    }

    public void setCases(List<CreateRagEvalCaseRequest> cases) {
        this.cases = cases;
    }
}