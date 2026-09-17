package com.frdms.frdms.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FinanceOfficerController {

    // Finance Officer Login
    @GetMapping("/finance-officer/login")
    public String showFinanceOfficerLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute(
                    "error",
                    "Invalid email or password"
            );
        }

        if (logout != null) {
            model.addAttribute(
                    "message",
                    "You have been logged out successfully"
            );
        }

        return "finance-officer/finance-officer-login";
    }
}