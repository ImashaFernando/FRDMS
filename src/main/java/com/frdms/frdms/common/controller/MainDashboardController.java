package com.frdms.frdms.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainDashboardController {

    @GetMapping("/")
    public String showMainDashboard() {
        return "main-dashboard";
    }
}