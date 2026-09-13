package com.frdms.frdms.victim_management.controller;

import com.frdms.frdms.victim_management.entity.GNReport;
import com.frdms.frdms.victim_management.entity.Victim;
import com.frdms.frdms.victim_management.service.GNReportService;
import com.frdms.frdms.victim_management.service.VictimService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VictimDashboardController {

    private final VictimService victimService;
    private final GNReportService gnReportService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public VictimDashboardController(
            VictimService victimService,
            GNReportService gnReportService) {

        this.victimService = victimService;
        this.gnReportService = gnReportService;
    }


    // =========================================================
    // VICTIM DASHBOARD
    // =========================================================

    @GetMapping("/victim/dashboard")
    public String showDashboard(
            HttpSession session,
            Model model) {


        // =====================================================
        // CHECK VICTIM LOGIN
        // =====================================================

        Long victimId =
                (Long) session.getAttribute("victimId");


        if (victimId == null) {

            return "redirect:/victim-login";
        }


        // =====================================================
        // GET LOGGED-IN VICTIM
        // =====================================================

        Victim victim =
                victimService.getVictimById(victimId);


        // =====================================================
        // GET LATEST GN REPORT
        // =====================================================

        GNReport gnReport =
                gnReportService.getLatestGNReportByVictim(victim);


        // =====================================================
        // SEND VICTIM INFORMATION TO DASHBOARD
        // =====================================================

        model.addAttribute(
                "victim",
                victim
        );


        // =====================================================
        // SEND GN REPORT INFORMATION TO DASHBOARD
        // =====================================================

        model.addAttribute(
                "gnReport",
                gnReport
        );


        // =====================================================
        // OPEN VICTIM DASHBOARD
        // =====================================================

        return "victim/dashboard";
    }
}