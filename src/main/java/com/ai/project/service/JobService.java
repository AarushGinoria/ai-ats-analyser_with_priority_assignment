package com.ai.project.service;
import com.ai.project.dto.JobRequestDto;
import com.ai.project.dto.JobResponseDto;
import com.ai.project.dto.StatusUpdateRequest;
import com.ai.project.dto.gemini.ATSResponse;
import com.ai.project.entity.Job;
import com.ai.project.entity.JobStatus;
import com.ai.project.entity.User;
import com.ai.project.repository.JobRepository;
import com.ai.project.exception.JobNotFoundException;
import com.ai.project.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final AIService aiService;
    private final PriorityService priorityService;
    private final UserRepository userRepository;
    public JobService(
            JobRepository jobRepository,
            AIService aiService,
            PriorityService priorityService,
            UserRepository userRepository
    ) {
        this.jobRepository = jobRepository;
        this.aiService = aiService;
        this.priorityService = priorityService;
        this.userRepository = userRepository;
    }

    public JobResponseDto createJob(JobRequestDto dto) throws Exception {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        // 1. DTO → Entity
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = new Job();
        job.setCompany(dto.getCompany());
        job.setRole(dto.getRole());
        job.setJobDescription(dto.getJobDescription());
        job.setResumeText(dto.getResumeText());
        // 2. Backend logic
        ATSResponse atsResponse = aiService.analyzeResume(
                dto.getResumeText(),
                dto.getJobDescription()
        );
        job.setAtsScore(atsResponse.getAtsScore());
        job.setStatus(JobStatus.APPLIED);
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        job.setUser(user);
        // 3. Save to DB
        Job savedJob = jobRepository.save(job);

        // 4. Entity → Response DTO
        JobResponseDto response = new JobResponseDto();
        response.setId(savedJob.getId());
        response.setCompany(savedJob.getCompany());
        response.setRole(savedJob.getRole());
        response.setStatus(savedJob.getStatus().name());
        response.setAtsScore(savedJob.getAtsScore());
        response.setCreatedAt(savedJob.getCreatedAt());
        response.setSuggestions(atsResponse.getSuggestions());
        response.setMissingSkills(atsResponse.getMissingSkills());
        return response;
    }
    public JobResponseDto getJobById(Long id) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
        Job job = jobRepository.findByIdAndUser(id,user)
                .orElseThrow(() -> new JobNotFoundException("Job not found"));

        JobResponseDto response = new JobResponseDto();
        response.setId(job.getId());
        response.setCompany(job.getCompany());
        response.setRole(job.getRole());
        response.setStatus(job.getStatus().name());
        response.setAtsScore(job.getAtsScore());
        response.setCreatedAt(job.getCreatedAt());

        return response;
    }
    public JobResponseDto updateStatus(
            Long id,
            StatusUpdateRequest statusUpdateRequest
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = jobRepository.findByIdAndUser(id,user)
                .orElseThrow(() ->
                        new JobNotFoundException("Job not found"));

        job.setStatus(statusUpdateRequest.getStatus());
        job.setUpdatedAt(LocalDateTime.now());

        Job updatedJob = jobRepository.save(job);

        JobResponseDto response = new JobResponseDto();
        response.setId(updatedJob.getId());
        response.setCompany(updatedJob.getCompany());
        response.setRole(updatedJob.getRole());
        response.setStatus(updatedJob.getStatus().name());
        response.setAtsScore(updatedJob.getAtsScore());
        response.setCreatedAt(updatedJob.getCreatedAt());

        return response;
    }
    public String deleteJob(Long id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findByIdAndUser(id,user)
                .orElseThrow(() ->
                        new JobNotFoundException("Job not found"));

        jobRepository.delete(job);

        return "Job deleted successfully.";
    }
    public List<JobResponseDto> getTop3Jobs() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Job> jobs = jobRepository.findByUser(user);
        jobs.sort((job1, job2) -> {

            int priority1 = priorityService.calculatePriority(job1);
            int priority2 = priorityService.calculatePriority(job2);

            return Integer.compare(priority2, priority1);

        });
        List<JobResponseDto> response = new ArrayList<>();
        int limit = Math.min(3, jobs.size());
        for (int i = 0; i < limit; i++) {

            Job job = jobs.get(i);
            JobResponseDto r = new JobResponseDto();

            r.setId(job.getId());
            r.setCompany(job.getCompany());
            r.setRole(job.getRole());
            r.setStatus(job.getStatus().name());
            r.setAtsScore(job.getAtsScore());
            r.setCreatedAt(job.getCreatedAt());
            r.setPriorityScore(
                    priorityService.calculatePriority(job)
            );
            response.add(r);
        }
        return response;
    }
}
