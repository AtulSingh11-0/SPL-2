package com.example.spl2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }

    @GetMapping("/teams-page")
    public String teamsPage() {
        return "team-management";
    }

    @GetMapping("/auction-page")
    public String auctionPage() {
        return "auction";
    }

    @GetMapping("/player-registration-page")
    public String playerRegistrationPage() {
        return "player-registration";
    }

    @GetMapping("/add-team-page")
    public String addTeamPage() {
        return "add-team";
    }
}

