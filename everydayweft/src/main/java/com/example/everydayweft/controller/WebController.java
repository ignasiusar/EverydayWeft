package com.example.everydayweft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        return "index"; // Asumsi kamu juga taruh index.html di templates
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin"; // Ini akan cari admin.html di folder templates
    }
}