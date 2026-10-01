package com.frdms.frdms.shelter_management.controller;

import com.frdms.frdms.disaster_management.service.DisasterService;
import com.frdms.frdms.shelter_management.entity.Shelter;
import com.frdms.frdms.shelter_management.entity.ShelterAllocation;
import com.frdms.frdms.shelter_management.service.ShelterAllocationService;
import com.frdms.frdms.shelter_management.service.ShelterService;
import com.frdms.frdms.victim_management.entity.AffectedFamilyInformation;
import com.frdms.frdms.victim_management.service.AffectedFamilyInformationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ShelterController {

    private final ShelterService shelterService;
    private final DisasterService disasterService;

    // SM-US8
    private final ShelterAllocationService shelterAllocationService;
    private final AffectedFamilyInformationService
            affectedFamilyInformationService;

    public ShelterController(
            ShelterService shelterService,
            DisasterService disasterService,
            ShelterAllocationService shelterAllocationService,
            AffectedFamilyInformationService
                    affectedFamilyInformationService
    ) {
        this.shelterService = shelterService;
        this.disasterService = disasterService;

        // SM-US8
        this.shelterAllocationService =
                shelterAllocationService;

        this.affectedFamilyInformationService =
                affectedFamilyInformationService;
    }

    @GetMapping("/staff/shelters/register")
    public String showRegisterShelterForm(Model model) {
        model.addAttribute("shelter", new Shelter());
        model.addAttribute("disasters", disasterService.getAllDisasters());
        return "shelter/shelter-form";
    }

    @PostMapping("/staff/shelters/register")
    public String registerShelter(
            @Valid Shelter shelter,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "disasters",
                    disasterService.getAllDisasters()
            );
            return "shelter/shelter-form";
        }

        try {
            shelterService.registerShelter(shelter);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Shelter registered successfully."
            );

            return "redirect:/staff/shelters/register";

        } catch (IllegalArgumentException exception) {

            bindingResult.reject(
                    "shelterError",
                    exception.getMessage()
            );

            model.addAttribute(
                    "disasters",
                    disasterService.getAllDisasters()
            );

            return "shelter/shelter-form";
        }
    }

    @GetMapping("/staff/shelters/{id}")
    public String viewShelterDetails(
            @PathVariable("id") Long id,
            Model model
    ) {
        Shelter shelter =
                shelterService.getShelterById(id);

        model.addAttribute(
                "shelter",
                shelter
        );

        return "shelter/shelter-view";
    }

    @GetMapping("/staff/shelters")
    public String viewShelters(Model model) {

        model.addAttribute(
                "shelters",
                shelterService.getAllShelters()
        );

        return "shelter/shelter-list";
    }

    @GetMapping("/staff/shelters/{id}/edit")
    public String showEditShelterForm(
            @PathVariable("id") Long id,
            Model model
    ) {
        Shelter shelter =
                shelterService.getShelterById(id);

        model.addAttribute(
                "shelter",
                shelter
        );

        model.addAttribute(
                "disasters",
                disasterService.getAllDisasters()
        );

        return "shelter/shelter-edit";
    }

    @PostMapping("/staff/shelters/{id}/update")
    public String updateShelterDetails(
            @PathVariable("id") Long id,
            @RequestParam("contactPerson") String contactPerson,
            @RequestParam("contactNumber") String contactNumber,
            @RequestParam("disasterName") String disasterName,
            @RequestParam(
                    value = "currentOccupancy",
                    required = false
            )
            Integer currentOccupancy,
            @RequestParam("status") Shelter.ShelterStatus status,
            RedirectAttributes redirectAttributes
    ) {
        try {

            shelterService.updateShelterDetails(
                    id,
                    contactPerson,
                    contactNumber,
                    disasterName,
                    currentOccupancy,
                    status
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Shelter details updated successfully."
            );

            return "redirect:/staff/shelters/" + id;

        } catch (IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            return "redirect:/staff/shelters/" + id + "/edit";
        }
    }

    @PostMapping("/staff/shelters/{id}/delete")
    public String deleteShelter(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {

            shelterService.deleteShelter(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Shelter deleted successfully."
            );

        } catch (IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/staff/shelters";
    }


    // =========================================================
    // SM-US8
    // ALLOCATE FAMILY TO SHELTER
    // =========================================================

    @GetMapping("/staff/eligible-families/{familyId}/allocate")
    public String showAllocationPage(
            @PathVariable("familyId") Long familyId,
            Model model
    ) {

        AffectedFamilyInformation family =
                affectedFamilyInformationService.getById(
                        familyId
                );

        List<Shelter> suitableShelters =
                shelterAllocationService.getSuitableShelters(
                        familyId
                );

        model.addAttribute(
                "family",
                family
        );

        model.addAttribute(
                "suitableShelters",
                suitableShelters
        );

        return "shelter/shelter-allocation";
    }


    @PostMapping("/staff/eligible-families/{familyId}/allocate")
    public String allocateFamily(
            @PathVariable("familyId") Long familyId,
            @RequestParam("shelterId") Long shelterId,
            RedirectAttributes redirectAttributes
    ) {

        try {

            shelterAllocationService.allocateFamily(
                    familyId,
                    shelterId
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Family allocated to the shelter successfully."
            );

            return "redirect:/staff/eligible-families";

        } catch (IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            return "redirect:/staff/eligible-families";
        }
    }


    // =========================================================
    // SM-US8
    // VIEW ALL ALLOCATIONS
    // =========================================================

    @GetMapping("/staff/allocations")
    public String viewAllocations(Model model) {

        model.addAttribute(
                "allocations",
                shelterAllocationService.getAllAllocations()
        );

        return "shelter/allocations";
    }


    // =========================================================
    // SM-US9
    // VIEW CURRENT FAMILY ALLOCATION
    // =========================================================

    @GetMapping(
            "/staff/eligible-families/{familyId}/allocation"
    )
    public String showCurrentAllocation(
            @PathVariable("familyId") Long familyId,
            Model model
    ) {

        AffectedFamilyInformation family =
                affectedFamilyInformationService.getById(
                        familyId
                );

        ShelterAllocation allocation =
                shelterAllocationService
                        .getAllocationByFamilyId(
                                familyId
                        );

        List<Shelter> suitableShelters =
                shelterAllocationService.getSuitableShelters(
                        familyId
                );

        model.addAttribute(
                "family",
                family
        );

        model.addAttribute(
                "allocation",
                allocation
        );

        model.addAttribute(
                "suitableShelters",
                suitableShelters
        );

        return "shelter/allocation-update";
    }
    // =========================================================
// SM-US9
// UPDATE FAMILY SHELTER ALLOCATION
// =========================================================

    @PostMapping(
            "/staff/eligible-families/{familyId}/allocation/update"
    )
    public String updateFamilyAllocation(
            @PathVariable("familyId") Long familyId,
            @RequestParam("shelterId") Long shelterId,
            RedirectAttributes redirectAttributes
    ) {

        try {

            shelterAllocationService.updateFamilyAllocation(
                    familyId,
                    shelterId
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Family shelter allocation updated successfully."
            );

            return "redirect:/staff/eligible-families";

        } catch (IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            return "redirect:/staff/eligible-families/"
                    + familyId
                    + "/allocation";
        }
    }
}