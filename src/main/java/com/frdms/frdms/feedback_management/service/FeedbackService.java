package com.frdms.frdms.feedback_management.service;

import com.frdms.frdms.feedback_management.entity.Feedback;
import com.frdms.frdms.feedback_management.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    // Save submitted feedback
    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    // Get all submitted feedback for management
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }
}