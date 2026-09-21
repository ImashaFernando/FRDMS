package com.frdms.frdms.feedback_management.controller;

import com.frdms.frdms.feedback_management.entity.Feedback;
import com.frdms.frdms.feedback_management.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    // --------------------------------------------------
    // PUBLIC - SUBMIT FEEDBACK
    // --------------------------------------------------

    @GetMapping("/submit")
    public String showSubmitFeedbackPage() {
        return "feedback/submit-feedback";
    }

    // --------------------------------------------------
    // VICTIM / AFFECTED FAMILY
    // --------------------------------------------------

    @GetMapping("/submit/victim")
    public String showVictimFeedbackPage(Model model) {

        Feedback feedback = new Feedback();
        feedback.setFeedbackType("VICTIM");

        model.addAttribute("feedback", feedback);

        return "feedback/victim-feedback";
    }

    // --------------------------------------------------
    // COMMUNITY MEMBER
    // --------------------------------------------------

    @GetMapping("/submit/community")
    public String showCommunityFeedbackPage(Model model) {

        Feedback feedback = new Feedback();
        feedback.setFeedbackType("COMMUNITY_MEMBER");

        model.addAttribute("feedback", feedback);

        return "feedback/community-feedback";
    }

    // --------------------------------------------------
    // VOLUNTEER
    // --------------------------------------------------

    @GetMapping("/submit/volunteer")
    public String showVolunteerFeedbackPage(Model model) {

        Feedback feedback = new Feedback();
        feedback.setFeedbackType("VOLUNTEER");

        model.addAttribute("feedback", feedback);

        return "feedback/volunteer-feedback";
    }

    // --------------------------------------------------
    // SAVE FEEDBACK
    // --------------------------------------------------

    @PostMapping("/save")
    public String saveFeedback(
            @Valid @ModelAttribute("feedback") Feedback feedback,
            BindingResult bindingResult) {

        // Victim-specific validation
        if ("VICTIM".equals(feedback.getFeedbackType())) {

            if (feedback.getAffectedArea() == null ||
                    feedback.getAffectedArea().trim().isEmpty()) {

                bindingResult.rejectValue(
                        "affectedArea",
                        "error.affectedArea",
                        "Affected area is required"
                );
            }

            if (feedback.getServiceDate() == null) {

                bindingResult.rejectValue(
                        "serviceDate",
                        "error.serviceDate",
                        "Service date is required"
                );
            }

            if (feedback.getAssistanceProvided() == null ||
                    feedback.getAssistanceProvided().trim().isEmpty()) {

                bindingResult.rejectValue(
                        "assistanceProvided",
                        "error.assistanceProvided",
                        "Please indicate whether the required assistance was provided"
                );
            }
        }

        // Community-specific validation
        if ("COMMUNITY_MEMBER".equals(feedback.getFeedbackType())) {

            if (feedback.getCommunityArea() == null ||
                    feedback.getCommunityArea().trim().isEmpty()) {

                bindingResult.rejectValue(
                        "communityArea",
                        "error.communityArea",
                        "Community area is required"
                );
            }

            if (feedback.getActivityLocation() == null ||
                    feedback.getActivityLocation().trim().isEmpty()) {

                bindingResult.rejectValue(
                        "activityLocation",
                        "error.activityLocation",
                        "Activity location is required"
                );
            }

            if (feedback.getServiceDate() == null) {

                bindingResult.rejectValue(
                        "serviceDate",
                        "error.serviceDate",
                        "Date of activity is required"
                );
            }
        }

        // Volunteer-specific validation
        if ("VOLUNTEER".equals(feedback.getFeedbackType())) {

            if (feedback.getServiceArea() == null ||
                    feedback.getServiceArea().trim().isEmpty()) {

                bindingResult.rejectValue(
                        "serviceArea",
                        "error.serviceArea",
                        "Service area is required"
                );
            }

            if (feedback.getServiceDate() == null) {

                bindingResult.rejectValue(
                        "serviceDate",
                        "error.serviceDate",
                        "Service date is required"
                );
            }
        }

        // Return to the correct form if validation fails
        if (bindingResult.hasErrors()) {

            if ("VICTIM".equals(feedback.getFeedbackType())) {
                return "feedback/victim-feedback";
            }

            if ("COMMUNITY_MEMBER".equals(feedback.getFeedbackType())) {
                return "feedback/community-feedback";
            }

            if ("VOLUNTEER".equals(feedback.getFeedbackType())) {
                return "feedback/volunteer-feedback";
            }

            return "feedback/submit-feedback";
        }

        // Save feedback
        feedbackService.saveFeedback(feedback);

        // Return to submit page with success message
        return "redirect:/feedback/submit?success";
    }

    // --------------------------------------------------
    // MANAGEMENT - VIEW SUBMITTED FEEDBACK
    // --------------------------------------------------

    @GetMapping("/list")
    public String viewFeedback(Model model) {

        model.addAttribute(
                "feedbackList",
                feedbackService.getAllFeedback()
        );

        return "feedback/feedback-list";
    }
}