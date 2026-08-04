package com.ai.project.service;

import com.ai.project.entity.JobStatus;
import org.springframework.stereotype.Service;
import com.ai.project.entity.Job;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class PriorityService {
    public int getStatusBonus(JobStatus status) {

        switch (status) {

            case APPLIED:
                return 10;

            case OA:
                return 30;

            case TECHNICAL:
                return 50;

            case HR:
                return 70;

            case OFFER:
                return 100;

            case REJECTED:
                return 0;

            default:
                return 0;
        }}
        public int getFreshnessBonus(LocalDateTime createdAt) {

            long days = ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());

            if (days <= 2) {
                return 30;
            }

            if (days <= 7) {
                return 20;
            }

            if (days <= 15) {
                return 10;
            }

            return 0;
        }
        public int calculatePriority(Job job) {

            return job.getAtsScore()
                    + getStatusBonus(job.getStatus())
                    + getFreshnessBonus(job.getCreatedAt());
        }
    }

