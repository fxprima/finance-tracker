package com.example.finance_tracker.controller;

import com.example.finance_tracker.form.AccountRegisterForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/register")
    public String viewRegister(Model model) {
        return "pages/auth/register";
    }

    @ModelAttribute("accountRegisterForm")
    public AccountRegisterForm initAccountRegisterForm() {
        return new AccountRegisterForm();
    }

    @PostMapping("/register")
    public String registerAccount(@ModelAttribute("accountRegisterForm") AccountRegisterForm form, Model model) {

        return "pages/auth/login";
    }

    @GetMapping("/login")
    public String viewLogin(Model model) {
        return "pages/auth/login";
    }

}
