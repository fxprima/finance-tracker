package com.example.finance_tracker.controller;

import com.example.finance_tracker.common.utils.alert.Alert;
import com.example.finance_tracker.common.utils.alert.Modal;
import com.example.finance_tracker.form.CredentialsForm;
import com.example.finance_tracker.form.AccountRegisterForm;
import com.example.finance_tracker.model.User;
import com.example.finance_tracker.security.CustomUserDetails;
import com.example.finance_tracker.service.AuthService;
import com.example.finance_tracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.security.auth.login.CredentialNotFoundException;

@Controller
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @GetMapping("/register")
    public String viewRegister(Model model) {
        return "pages/auth/register";
    }

    @ModelAttribute("accountRegisterForm")
    public AccountRegisterForm initAccountRegisterForm() {
        return new AccountRegisterForm();
    }

    @ModelAttribute("accountLoginForm")
    public CredentialsForm initAccountLoginForm() {
        return new CredentialsForm();
    }

    @PostMapping("/register")
    public String registerAccount(
            @ModelAttribute("accountRegisterForm") AccountRegisterForm form,
            Model model,
            RedirectAttributes ra,
            HttpSession session
    ) {

        try {
            User userBuffer = new User();
            String hashedPassword = passwordEncoder.encode(form.getPassword());

            userBuffer.setEmail(form.getEmail());
            userBuffer.setPassword(hashedPassword);
            userBuffer.setFirstName(form.getFirstName());
            userBuffer.setLastName(form.getLastName());

            User user = userService.createUser(userBuffer);
            log.info("User %s has been registered".formatted(user.getEmail()));

            Modal.addSuccess(model, "Your account has been successfully registered");

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            log.error("Error Create User: %s".formatted(e.getMessage()));

            Alert.addError(ra, "Email are already exists.");
            return "redirect:/auth/register";
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error Create User: %s".formatted(e.getMessage()));
            Alert.addError(ra, "Register Failed: %s".formatted(e.getMessage()));
            return "redirect:/auth/register";
        }

        return "pages/auth/login";
    }

    @GetMapping("/login")
    public String loginPage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (userDetails != null)
            return "redirect:/";


        if (error != null)
            Modal.addError(model, "Username or password is not correct");

        if (logout != null)
            Modal.addSuccess(model, "You have successfully logged out your account.");

        return "pages/auth/login";
    }

}
