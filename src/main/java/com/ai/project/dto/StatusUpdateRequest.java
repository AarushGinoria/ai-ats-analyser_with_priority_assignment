package com.ai.project.dto;

import com.ai.project.entity.JobStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public class StatusUpdateRequest {

    @Schema(
            description = "New status of the job",
            allowableValues = {
                    "APPLIED",
                    "OA",
                    "TECHNICAL",
                    "HR",
                    "OFFER",
                    "REJECTED"

            }
    )
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
