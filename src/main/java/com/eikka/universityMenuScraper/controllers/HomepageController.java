package com.eikka.universityMenuScraper.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {

    @GetMapping("/")
    public String getHomePage() {
        return "homepage";
    }
}
