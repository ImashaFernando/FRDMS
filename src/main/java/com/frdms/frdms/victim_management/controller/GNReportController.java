package com.frdms.frdms.victim_management.controller;

import com.frdms.frdms.victim_management.entity.GNReport;
import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.service.GNReportService;
import com.frdms.frdms.victim_management.service.VictimService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/gn-reports")
public class GNReportController {

    private final GNReportService gnReportService;
    private final VictimService victimService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public GNReportController(
            GNReportService gnReportService,
            VictimService victimService) {

        this.gnReportService = gnReportService;
        this.victimService = victimService;
    }


    // =========================================================
    // VM-03
    // SHOW GN REPORT FORM
    // =========================================================

    @GetMapping("/submit/{victimId}")
    public String showGNReportForm(
            @PathVariable Long victimId,
            Model model) {

        // Find victim

        Victim victim =
                victimService.getVictimById(victimId);


        // Create empty GN report

        GNReport gnReport =
                new GNReport();


        // Connect report to victim

        gnReport.setVictim(victim);


        // Send information to form

        model.addAttribute(
                "gnReport",
                gnReport
        );

        model.addAttribute(
                "victim",
                victim
        );


        return "gn-report/form";
    }


    // =========================================================
    // VM-03
    // SUBMIT GN REPORT
    // =========================================================

    @PostMapping("/submit/{victimId}")
    public String submitGNReport(

            @PathVariable Long victimId,

            @RequestParam("reportNumber")
            String reportNumber,

            @RequestParam("gnOfficerName")
            String gnOfficerName,

            @RequestParam("gnDivision")
            String gnDivision,

            @RequestParam("reportDescription")
            String reportDescription,

            @RequestParam("reportFile")
            MultipartFile reportFile,

            Model model) throws IOException {


        // =====================================================
        // FIND VICTIM
        // =====================================================

        Victim victim =
                victimService.getVictimById(victimId);


        model.addAttribute(
                "victim",
                victim
        );


        // =====================================================
        // CHECK REQUIRED TEXT FIELDS
        // =====================================================

        if (reportNumber == null ||
                reportNumber.trim().isEmpty()) {

            model.addAttribute(
                    "formError",
                    "GN Report Number is required."
            );

            return "gn-report/form";
        }


        if (gnOfficerName == null ||
                gnOfficerName.trim().isEmpty()) {

            model.addAttribute(
                    "formError",
                    "GN Officer Name is required."
            );

            return "gn-report/form";
        }


        if (gnDivision == null ||
                gnDivision.trim().isEmpty()) {

            model.addAttribute(
                    "formError",
                    "GN Division is required."
            );

            return "gn-report/form";
        }


        if (reportDescription == null ||
                reportDescription.trim().isEmpty()) {

            model.addAttribute(
                    "formError",
                    "Report Description is required."
            );

            return "gn-report/form";
        }


        // =====================================================
        // CHECK FILE
        // =====================================================

        if (reportFile == null ||
                reportFile.isEmpty()) {

            model.addAttribute(
                    "fileError",
                    "Please upload the GN report as a PDF or image."
            );

            return "gn-report/form";
        }


        // =====================================================
        // CHECK FILE TYPE
        // =====================================================

        String contentType =
                reportFile.getContentType();


        boolean validFileType =

                "application/pdf".equals(contentType)

                        ||

                        "image/jpeg".equals(contentType)

                        ||

                        "image/png".equals(contentType);


        if (!validFileType) {

            model.addAttribute(
                    "fileError",
                    "Only PDF, JPG, JPEG, and PNG files are allowed."
            );

            return "gn-report/form";
        }


        // =====================================================
        // CHECK DUPLICATE REPORT NUMBER
        // =====================================================

        GNReport existingReport =
                gnReportService.getGNReportByNumber(
                        reportNumber.trim()
                );


        if (existingReport != null) {

            model.addAttribute(
                    "reportNumberError",
                    "A GN report with this report number already exists. Please enter a different number."
            );

            return "gn-report/form";
        }


        // =====================================================
        // CREATE GN REPORT
        // =====================================================

        GNReport gnReport =
                new GNReport();


        gnReport.setReportNumber(
                reportNumber.trim()
        );

        gnReport.setGnOfficerName(
                gnOfficerName.trim()
        );

        gnReport.setGnDivision(
                gnDivision.trim()
        );

        gnReport.setReportDescription(
                reportDescription.trim()
        );


        // =====================================================
        // CONNECT REPORT TO VICTIM
        // =====================================================

        gnReport.setVictim(victim);


        // =====================================================
        // STORE UPLOADED FILE
        // =====================================================

        gnReport.setReportFile(
                reportFile.getBytes()
        );

        gnReport.setFileName(
                reportFile.getOriginalFilename()
        );

        gnReport.setFileType(
                reportFile.getContentType()
        );


        // =====================================================
        // SAVE GN REPORT
        // =====================================================

        gnReportService.submitGNReport(
                gnReport
        );


        // =====================================================
        // CONSOLE CONFIRMATION
        // =====================================================

        System.out.println(
                "========================================"
        );

        System.out.println(
                "GN REPORT SUCCESSFULLY SAVED"
        );

        System.out.println(
                "Report Number: "
                        + gnReport.getReportNumber()
        );

        System.out.println(
                "Victim ID: "
                        + victim.getId()
        );

        System.out.println(
                "File: "
                        + gnReport.getFileName()
        );

        System.out.println(
                "========================================"
        );


        // =====================================================
        // GO TO SUCCESS PAGE
        // =====================================================

        return "redirect:/gn-reports/success";
    }


    // =========================================================
    // GN REPORT SUCCESS PAGE
    // =========================================================

    @GetMapping("/success")
    public String showSuccessPage() {

        return "gn-report/success";
    }
    // =========================================================
// VICTIM - GN REPORT STATUS
// =========================================================

    @GetMapping("/status/{victimId}")
    public String showGNReportStatus(
            @PathVariable Long victimId,
            Model model) {

        Victim victim =
                victimService.getVictimById(victimId);

        GNReport gnReport =
                gnReportService.getLatestGNReportByVictim(victim);

        model.addAttribute(
                "victim",
                victim
        );

        model.addAttribute(
                "gnReport",
                gnReport
        );

        return "gn-report/status";
    }

}