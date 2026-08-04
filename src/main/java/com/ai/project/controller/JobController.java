package com.ai.project.controller;

import com.ai.project.dto.JobRequestDto;
import com.ai.project.dto.JobResponseDto;
import com.ai.project.dto.StatusUpdateRequest;
import com.ai.project.service.JobService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public JobResponseDto createJob(@Valid  @RequestBody JobRequestDto dto)  throws Exception{
        return jobService.createJob(dto);
    }
    @GetMapping("/{id}")
    public JobResponseDto getJobById(@PathVariable Long id) {
        return jobService.getJobById(id);
    }
    @PatchMapping("/{id}/status")
    public JobResponseDto updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest statusUpdateRequest
    ) throws Exception {
        return jobService.updateStatus(id, statusUpdateRequest);
    }
    @DeleteMapping("/{id}")
    public String deleteJob(@PathVariable Long id) {

        return jobService.deleteJob(id);
    }
    @GetMapping("/top3")
    public List<JobResponseDto> getTop3Jobs() {
        return jobService.getTop3Jobs();
    }
}