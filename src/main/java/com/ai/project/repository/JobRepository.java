package com.ai.project.repository;


import com.ai.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ai.project.entity.Job;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job,Long> {
    Optional<Job> findByIdAndUser(Long id, User user);
    List<Job> findByUser(User user);

}
