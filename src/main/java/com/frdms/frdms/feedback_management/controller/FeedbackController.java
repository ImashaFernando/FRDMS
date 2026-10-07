
package com.frdms.frdms.feedback_management.controller;

import com.frdms.frdms.feedback_management.entity.Feedback;
import com.frdms.frdms.feedback_management.service.FeedbackService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final Validator validator;

    public FeedbackController(
            FeedbackService feedbackService,
            Validator validator) {
        this.feedbackService = feedbackService;
        this.validator = validator;
    }

    // Feedback role selection page
    @GetMapping("/submit")
    public String showSubmitFeedbackPage() {
        return "feedback/submit-feedback";
    }
    // Compatibility route for the home page
    @GetMapping("")
    public String feedbackHome() {
        return "redirect:/feedback/submit";
    }
    // Compatibility route for the home page
    @GetMapping("/view")
    public String viewFeedback() {
        return "redirect:/feedback/list";
    }

    // Victim feedback form
    @GetMapping("/submit/victim")
    public String showVictimFeedbackPage(Model model) {
        Feedback feedback = new Feedback();
        feedback.setFeedbackType("VICTIM");
        model.addAttribute("feedback", feedback);
        return "feedback/victim-feedback";
    }

    // Donor feedback form
    @GetMapping("/submit/donor")
    public String showDonorFeedbackPage(Model model) {
        Feedback feedback = new Feedback();
        feedback.setFeedbackType("DONOR");
        model.addAttribute("feedback", feedback);
        return "feedback/donor-feedback";
    }

    // Volunteer feedback form
    @GetMapping("/submit/volunteer")
    public String showVolunteerFeedbackPage(Model model) {
        Feedback feedback = new Feedback();
        feedback.setFeedbackType("VOLUNTEER");
        model.addAttribute("feedback", feedback);
        return "feedback/volunteer-feedback";
    }

    // Save feedback
    @PostMapping("/save")
    public String saveFeedback(
            @ModelAttribute("feedback") Feedback feedback,
            BindingResult bindingResult,
            Model model) {

        String feedbackType = feedback.getFeedbackType();

        if (!"VICTIM".equals(feedbackType)
                && !"DONOR".equals(feedbackType)
                && !"VOLUNTEER".equals(feedbackType)) {
            return "redirect:/feedback/submit";
        }

        switch (feedbackType) {
            case "VICTIM":
                feedback.setService("Relief Assistance");
                break;
            case "DONOR":
                feedback.setService("Donation and Relief Support");
                break;
            case "VOLUNTEER":
                feedback.setService("Volunteer Relief Work");
                break;
            default:
                return "redirect:/feedback/submit";
        }

        Set<ConstraintViolation<Feedback>> violations =
                validator.validate(feedback);

        for (ConstraintViolation<Feedback> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            bindingResult.rejectValue(
                    fieldName,
                    "invalid." + fieldName,
                    violation.getMessage()
            );
        }

        if ("VICTIM".equals(feedbackType)
                && isBlank(feedback.getAssistanceProvided())) {
            bindingResult.rejectValue(
                    "assistanceProvided",
                    "required.assistanceProvided",
                    "Please indicate whether the required assistance was provided."
            );
        }

        if ("DONOR".equals(feedbackType)
                && isBlank(feedback.getPeopleReached())) {
            bindingResult.rejectValue(
                    "peopleReached",
                    "required.peopleReached",
                    "Please indicate whether your donation contributed to relief activities."
            );
        }

        if ("VOLUNTEER".equals(feedbackType)
                && isBlank(feedback.getResourcesAvailable())) {
            bindingResult.rejectValue(
                    "resourcesAvailable",
                    "required.resourcesAvailable",
                    "Please answer the volunteer resources question."
            );
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("feedback", feedback);

            switch (feedbackType) {
                case "VICTIM":
                    return "feedback/victim-feedback";
                case "DONOR":
                    return "feedback/donor-feedback";
                case "VOLUNTEER":
                    return "feedback/volunteer-feedback";
                default:
                    return "redirect:/feedback/submit";
            }
        }

        // New submissions always start pending review.
        feedback.setApprovalStatus("PENDING");
        feedback.setRejectionReason(null);
        feedbackService.saveFeedback(feedback);

        return "redirect:/feedback/submit?success";
    }

    // My Feedback: show lookup form
    @GetMapping("/my-feedback")
    public String showMyFeedbackPage() {
        return "feedback/my-feedback";
    }

    // My Feedback: search by name and NIC
    @PostMapping("/my-feedback")
    public String searchMyFeedback(
            @RequestParam("name") String name,
            @RequestParam("nic") String nic,
            Model model) {

        model.addAttribute("submittedName", name);
        model.addAttribute(
                "feedbackList",
                feedbackService.getMyFeedback(name, nic)
        );
        model.addAttribute("searched", true);

        return "feedback/my-feedback";
    }

    // Existing list of all submitted feedback

    @GetMapping("/list")
    public String viewFeedbackList(Model model) {
        model.addAttribute("feedbackList", feedbackService.getApprovedFeedback());
        return "feedback/feedback-list";
    }


    // Project Manager review page: pending feedback only
    @GetMapping("/manager")
    public String showManagerFeedback(Model model) {
        model.addAttribute(
                "feedbackList",
                feedbackService.getPendingFeedback()
        );
        return "feedback/feedback-manager";
    }

    // Approve feedback
    @PostMapping("/approve/{id}")
    public String approveFeedback(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        boolean approved = feedbackService.approveFeedback(id);

        if (approved) {
            redirectAttributes.addFlashAttribute(
                    "message", "Feedback approved successfully."
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Feedback record was not found."
            );
        }

        return "redirect:/feedback/manager";
    }

    // Reject feedback with a reason
    @PostMapping("/reject/{id}")
    public String rejectFeedback(
            @PathVariable Long id,
            @RequestParam("rejectionReason") String rejectionReason,
            RedirectAttributes redirectAttributes) {

        if (isBlank(rejectionReason)) {
            redirectAttributes.addFlashAttribute(
                    "error", "Please provide a reason for rejection."
            );
            return "redirect:/feedback/manager";
        }

        boolean rejected =
                feedbackService.rejectFeedback(id, rejectionReason);

        if (rejected) {
            redirectAttributes.addFlashAttribute(
                    "message", "Feedback rejected successfully."
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "error", "Feedback record was not found."
            );
        }

        return "redirect:/feedback/manager";
    }


    // Show the edit form after verifying ownership.
    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String nic,
            Model model,
            RedirectAttributes redirectAttributes) {

        return feedbackService.getOwnedFeedback(id, name, nic)
                .map(feedback -> {
                    model.addAttribute("feedback", feedback);
                    model.addAttribute("ownerName", name);
                    model.addAttribute("ownerNic", nic);
                    return "feedback/edit-feedback";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(
                            "errorMessage",
                            "Feedback not found or ownership could not be verified."
                    );
                    return "redirect:/feedback/my-feedback";
                });
    }

    // Update the existing feedback record.
    @PostMapping("/edit/{id}")
    public String updateFeedback(
            @PathVariable Long id,
            @RequestParam String ownerName,
            @RequestParam String ownerNic,
            @ModelAttribute("feedback") Feedback updatedFeedback,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (updatedFeedback.getRating() == null
                || updatedFeedback.getRating() < 1
                || updatedFeedback.getRating() > 5) {
            bindingResult.rejectValue(
                    "rating", "invalid.rating",
                    "Rating must be between 1 and 5."
            );
        }

        if (isBlank(updatedFeedback.getComments())) {
            bindingResult.rejectValue(
                    "comments", "required.comments",
                    "Please enter your feedback."
            );
        } else if (updatedFeedback.getComments().length() > 1000) {
            bindingResult.rejectValue(
                    "comments", "invalid.comments",
                    "Feedback must not exceed 1000 characters."
            );
        }

        if (updatedFeedback.getSuggestions() != null
                && updatedFeedback.getSuggestions().length() > 1000) {
            bindingResult.rejectValue(
                    "suggestions", "invalid.suggestions",
                    "Suggestions must not exceed 1000 characters."
            );
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("ownerName", ownerName);
            model.addAttribute("ownerNic", ownerNic);
            return "feedback/edit-feedback";
        }

        var updated = feedbackService.updateOwnedFeedback(
                id, ownerName, ownerNic, updatedFeedback
        );

        if (updated.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Feedback could not be updated. Please verify ownership."
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Feedback updated successfully. It is awaiting review again."
            );
        }

        return "redirect:/feedback/my-feedback";
    }

    // Delete feedback only after verifying ownership.
    @PostMapping("/delete/{id}")
    public String deleteFeedback(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String nic,
            RedirectAttributes redirectAttributes) {

        boolean deleted = feedbackService.deleteOwnedFeedback(id, name, nic);

        if (deleted) {
            redirectAttributes.addFlashAttribute(
                    "message", "Feedback deleted successfully."
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Feedback could not be deleted. Please verify ownership."
            );
        }

        return "redirect:/feedback/my-feedback";
    }


    @PostMapping("/edit/{id}/form")
    public String showEditFormFromMyFeedback(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String nic,
            Model model,
            RedirectAttributes redirectAttributes) {

        return feedbackService.getOwnedFeedback(id, name, nic)
                .map(feedback -> {
                    model.addAttribute("feedback", feedback);
                    model.addAttribute("ownerName", name);
                    model.addAttribute("ownerNic", nic);

                    return "feedback/edit-feedback";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(
                            "errorMessage",
                            "Feedback not found or ownership could not be verified."
                    );
                    return "redirect:/feedback/my-feedback";
                });
    }




    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
