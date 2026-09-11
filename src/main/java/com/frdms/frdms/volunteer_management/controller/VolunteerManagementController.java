package com.frdms.frdms.volunteer_management.controller;

import com.frdms.frdms.volunteer_management.entity.VolunteerProfile;
import com.frdms.frdms.volunteer_management.service.VolunteerProfileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
}
