package com.example.everydayweft.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Test OK";
    }

    @PostMapping("/test-post")
    public String testPost() {
        return "POST OK";
    }
}