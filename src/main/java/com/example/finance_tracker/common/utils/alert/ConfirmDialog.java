package com.example.finance_tracker.common.utils.alert;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ConfirmDialog {

    private static final String MSG = "confirmMsg";
    private static final String YES_LABEL = "confirmYesLabel";
    private static final String NO_LABEL = "confirmNoLabel";
    private static final String ACTION_URL = "confirmActionUrl";
    private static final String METHOD = "confirmMethod";

    private static final String ACTIONS = "confirmActions";

    public static final class ConfirmAction {
        private final String label;  // text di tombol (Yes1, Save & Replace, dll)
        private final String url;    // action URL
        private final String method; // "GET", "POST", "PUT", "DELETE" (boleh null = POST)

        public ConfirmAction(String label, String url, String method) {
            this.label = label;
            this.url = url;
            this.method = method;
        }

        public String getLabel() {
            return label;
        }

        public String getUrl() {
            return url;
        }

        public String getMethod() {
            return method;
        }
    }


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

    public static void show(Model model,
                            String message,
                            String noLabel,
                            ConfirmAction... actions) {
        if (model == null) return;

        List<ConfirmAction> list = Arrays.stream(actions)
                .filter(Objects::nonNull)
                .limit(3) // max 3 Yes
                .toList();

        model.addAttribute(MSG, message);
        model.addAttribute(NO_LABEL, noLabel);
        model.addAttribute(ACTIONS, list);
    }

    public static void show(RedirectAttributes redirectAttributes,
                            String message,
                            String noLabel,
                            ConfirmAction... actions) {

        if (redirectAttributes == null) return;

        List<ConfirmAction> list = Arrays.stream(actions)
                .filter(Objects::nonNull)
                .limit(3) // max 3 Yes
                .toList();

        redirectAttributes.addFlashAttribute(MSG, message);
        redirectAttributes.addFlashAttribute(NO_LABEL, noLabel);
        redirectAttributes.addFlashAttribute(ACTIONS, list);
    }


}