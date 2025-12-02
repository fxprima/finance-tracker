package com.example.finance_tracker.common.utils.alert;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public final class ModalUtil {

    private static final String ATTR_MESSAGE = "modalMsg";
    private static final String ATTR_TYPE = "modalType";

    private ModalUtil() {
        // utility class, no instance
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
        if (redirectAttributes == null) {
            return;
        }
        redirectAttributes.addFlashAttribute(ATTR_MESSAGE, message);
        redirectAttributes.addFlashAttribute(ATTR_TYPE, type);
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
        if (model == null) {
            return;
        }
        model.addAttribute(ATTR_MESSAGE, message);
        model.addAttribute(ATTR_TYPE, type);
    }
}
