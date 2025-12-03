package com.example.finance_tracker.common.utils.alert;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public final class ConfirmDialog {

    private static final String MSG = "confirmMsg";
    private static final String YES_LABEL = "confirmYesLabel";
    private static final String NO_LABEL = "confirmNoLabel";
    private static final String ACTION_URL = "confirmActionUrl";
    private static final String METHOD = "confirmMethod";

    private ConfirmDialog() {}
    /* MODEL */
    public static void show(Model model,
                            String message,
                            String yesLabel,
                            String noLabel,
                            String actionUrl,
                            String method) {

        if (model == null) return;

        model.addAttribute(MSG, message);
        model.addAttribute(YES_LABEL, yesLabel);
        model.addAttribute(NO_LABEL, noLabel);
        model.addAttribute(ACTION_URL, actionUrl);
        model.addAttribute(METHOD, method);
    }

    /* REDIRECT ATTRIBUTES */
    public static void show(RedirectAttributes redirectAttributes,
                            String message,
                            String yesLabel,
                            String noLabel,
                            String actionUrl,
                            String method) {

        if (redirectAttributes == null) return;

        redirectAttributes.addFlashAttribute(MSG, message);
        redirectAttributes.addFlashAttribute(YES_LABEL, yesLabel);
        redirectAttributes.addFlashAttribute(NO_LABEL, noLabel);
        redirectAttributes.addFlashAttribute(ACTION_URL, actionUrl);
        redirectAttributes.addFlashAttribute(METHOD, method);
    }
}