package com.example.finance_tracker.controller;

import com.example.finance_tracker.common.utils.alert.ModalUtil;
import com.example.finance_tracker.form.AccountRegisterForm;
import com.example.finance_tracker.model.User;
import com.example.finance_tracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String viewRegister(Model model) {
        return "pages/auth/register";
    }

    @ModelAttribute("accountRegisterForm")
    public AccountRegisterForm initAccountRegisterForm() {
        return new AccountRegisterForm();
    }

    @PostMapping("/register")
    public String registerAccount(@ModelAttribute("accountRegisterForm") AccountRegisterForm form, Model model, RedirectAttributes ra) {

        try {
            User userBuffer = new User();
            userBuffer.setEmail(form.getEmail());
            userBuffer.setPassword(form.getPassword());
            userBuffer.setFirstName(form.getFirstName());
            userBuffer.setLastName(form.getLastName());

            User user = userService.createUser(userBuffer);

            log.info("User %s has been registered".formatted(user.getEmail()));
            ModalUtil.addSuccess(model, "Your account has been successfully registered");

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            log.error("Error Create User: %s".formatted(e.getMessage()));

            ModalUtil.addError(ra, "Email are already exists");
            return "redirect:/auth/register";
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error Create User: %s".formatted(e.getMessage()));
            ModalUtil.addError(ra, "Register Failed: %s".formatted(e.getMessage()));
            return "redirect:/auth/register";
        }

        return "pages/auth/login";
    }

    @GetMapping("/login")
    public String viewLogin(Model model) {
        return "pages/auth/login";
    }

}
