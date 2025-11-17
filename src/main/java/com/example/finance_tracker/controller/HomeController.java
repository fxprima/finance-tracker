package com.example.finance_tracker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {

    @GetMapping({"/", "/index"})
    public String home (Model model) {
        return "redirect:/import/";
    }

}
