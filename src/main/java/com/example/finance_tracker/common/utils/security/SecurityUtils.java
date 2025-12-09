package com.example.finance_tracker.common.utils.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static boolean isLoggedIn() {
        return SecurityContextHolder.getContext().getAuthentication() != null && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser");
    }

}
