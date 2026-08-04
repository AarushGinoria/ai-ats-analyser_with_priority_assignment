package com.ai.project.dto;

import com.ai.project.entity.JobStatus;

public class StatusUpdateRequest {

    private JobStatus status;

    public StatusUpdateRequest() {
    }

    public StatusUpdateRequest(JobStatus status) {
        this.status = status;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }
}
