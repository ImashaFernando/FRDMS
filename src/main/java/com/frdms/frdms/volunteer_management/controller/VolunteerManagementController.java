package com.frdms.frdms.volunteer_management.controller;

import com.frdms.frdms.volunteer_management.entity.VolunteerProfile;
import com.frdms.frdms.volunteer_management.service.VolunteerProfileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class VolunteerManagementController {

    private final VolunteerProfileService volunteerProfileService;

    public VolunteerManagementController(
            VolunteerProfileService volunteerProfileService
    ) {
        this.volunteerProfileService = volunteerProfileService;
    }

    @GetMapping("/project-manager/volunteers")
    public String viewSubmittedVolunteers(Model model) {

        List<VolunteerProfile> volunteerProfiles =
                volunteerProfileService.getAllVolunteerProfiles();

        model.addAttribute("volunteerProfiles", volunteerProfiles);

        return "volunteer/manager-volunteer-list";
    }

    @GetMapping("/project-manager/volunteers/{id}")
    public String viewVolunteerDetails(
            @PathVariable("id") Long id,
            Model model
    ) {

        VolunteerProfile volunteerProfile =
                volunteerProfileService.getVolunteerProfileById(id);

        model.addAttribute("volunteerProfile", volunteerProfile);

        return "volunteer/manager-volunteer-view";
    }
    @PostMapping("/project-manager/volunteers/{id}/review")
    public String saveReviewNotes(
            @PathVariable("id") Long id,
            @RequestParam("reviewNotes") String reviewNotes,
            RedirectAttributes redirectAttributes
    ) {

        if (reviewNotes == null || reviewNotes.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "Review notes cannot be empty."
            );

            return "redirect:/project-manager/volunteers/" + id;
        }

        volunteerProfileService.saveReviewNotes(
                id,
                reviewNotes.trim()
        );

        redirectAttributes.addFlashAttribute(
                "reviewSuccess",
                "Review notes saved successfully."
        );

        return "redirect:/project-manager/volunteers/" + id;
    }
    @PostMapping("/project-manager/volunteers/{id}/approve")
    public String approveVolunteer(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes
    ) {

        volunteerProfileService.approveVolunteer(id);

        redirectAttributes.addFlashAttribute(
                "approvalSuccess",
                "Volunteer registration approved successfully."
        );

        return "redirect:/project-manager/volunteers/" + id;
    }
    @PostMapping("/project-manager/volunteers/{id}/reject")
    public String rejectVolunteer(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes
    ) {

        volunteerProfileService.rejectVolunteer(id);

        redirectAttributes.addFlashAttribute(
                "rejectionSuccess",
                "Volunteer registration rejected successfully."
        );

        return "redirect:/project-manager/volunteers/" + id;
    }
}
