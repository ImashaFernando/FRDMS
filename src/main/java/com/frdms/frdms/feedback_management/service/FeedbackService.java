
package com.frdms.frdms.feedback_management.service;

import com.frdms.frdms.feedback_management.entity.Feedback;
import com.frdms.frdms.feedback_management.repository.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    // Save a new feedback submission.
    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }


    public List<Feedback> getApprovedFeedback() {
        return feedbackRepository.findByApprovalStatusIgnoreCase("APPROVED");
    }


    // View all feedback records.
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    // Find feedback belonging to a person.
    public List<Feedback> getMyFeedback(String name, String nic) {
        if (isBlank(name) || isBlank(nic)) {
            return List.of();
        }

        return feedbackRepository.findByNameIgnoreCaseAndNicIgnoreCase(
                name.trim(),
                nic.trim()
        );
    }

    // Find one feedback record only if its name and NIC match.
    public Optional<Feedback> getOwnedFeedback(
            Long id,
            String name,
            String nic) {

        if (id == null || isBlank(name) || isBlank(nic)) {
            return Optional.empty();
        }

        return feedbackRepository.findByIdAndNameIgnoreCaseAndNicIgnoreCase(
                id,
                name.trim(),
                nic.trim()
        );
    }

    // Update an existing record without creating a duplicate.
    @Transactional
    public Optional<Feedback> updateOwnedFeedback(
            Long id,
            String name,
            String nic,
            Feedback updatedFeedback) {

        Optional<Feedback> result = getOwnedFeedback(id, name, nic);

        if (result.isEmpty() || updatedFeedback == null) {
            return Optional.empty();
        }

        Feedback existing = result.get();

        // Keep the original owner's identity and feedback type.
        // Update the submitted content on the existing database record.
        existing.setRating(updatedFeedback.getRating());
        existing.setComments(updatedFeedback.getComments());
        existing.setSuggestions(updatedFeedback.getSuggestions());

        // Update the answer relevant to the original feedback role.
        switch (existing.getFeedbackType()) {
            case "VICTIM":
                existing.setAssistanceProvided(
                        updatedFeedback.getAssistanceProvided()
                );
                break;

            case "DONOR":
                existing.setPeopleReached(
                        updatedFeedback.getPeopleReached()
                );
                break;

            case "VOLUNTEER":
                existing.setResourcesAvailable(
                        updatedFeedback.getResourcesAvailable()
                );
                break;

            default:
                return Optional.empty();
        }

        // Edited feedback must be reviewed again by the manager.
        existing.setApprovalStatus("PENDING");
        existing.setRejectionReason(null);

        return Optional.of(feedbackRepository.save(existing));
    }

    // Delete an existing record only after checking ownership.
    @Transactional
    public boolean deleteOwnedFeedback(
            Long id,
            String name,
            String nic) {

        Optional<Feedback> result = getOwnedFeedback(id, name, nic);

        if (result.isEmpty()) {
            return false;
        }

        feedbackRepository.delete(result.get());
        return true;
    }

    // Get feedback waiting for manager review.
    public List<Feedback> getPendingFeedback() {
        return feedbackRepository.findByApprovalStatusIgnoreCase("PENDING");
    }

    // Approve feedback.
    @Transactional
    public boolean approveFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id).orElse(null);

        if (feedback == null) {
            return false;
        }

        feedback.setApprovalStatus("APPROVED");
        feedback.setRejectionReason(null);
        feedbackRepository.save(feedback);

        return true;
    }

    // Reject feedback with a reason.
    @Transactional
    public boolean rejectFeedback(Long id, String reason) {
        if (isBlank(reason) || reason.trim().length() > 1000) {
            return false;
        }

        Feedback feedback = feedbackRepository.findById(id).orElse(null);

        if (feedback == null) {
            return false;
        }

        feedback.setApprovalStatus("REJECTED");
        feedback.setRejectionReason(reason.trim());
        feedbackRepository.save(feedback);

        return true;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
