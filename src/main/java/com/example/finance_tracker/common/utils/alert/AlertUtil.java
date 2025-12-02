package com.example.finance_tracker.common.utils.alert;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public final class AlertUtil {

    private static final String MSG = "alertMsg";
    private static final String TYPE = "alertType";

    private AlertUtil() {
    }

    public static void addSuccess(RedirectAttributes redirectAttributes, String message) {
        add(redirectAttributes, message, "success");
    }

    public static void addError(RedirectAttributes redirectAttributes, String message) {
        add(redirectAttributes, message, "error");
    }

    public static void addInfo(RedirectAttributes redirectAttributes, String message) {
        add(redirectAttributes, message, "info");
    }

    private static void add(RedirectAttributes redirectAttributes, String message, String type) {
        if (redirectAttributes == null) return;
        redirectAttributes.addFlashAttribute(MSG, message);
        redirectAttributes.addFlashAttribute(TYPE, type);
    }

    public static void addSuccess(Model model, String message) {
        add(model, message, "success");
    }

    public static void addError(Model model, String message) {
        add(model, message, "error");
    }

    public static void addInfo(Model model, String message) {
        add(model, message, "info");
    }

    private static void add(Model model, String message, String type) {
        if (model == null) return;
        model.addAttribute(MSG, message);
        model.addAttribute(TYPE, type);
    }
}