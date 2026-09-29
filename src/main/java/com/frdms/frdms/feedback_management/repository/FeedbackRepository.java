package com.frdms.frdms.feedback_management.repository;

import com.frdms.frdms.feedback_management.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}