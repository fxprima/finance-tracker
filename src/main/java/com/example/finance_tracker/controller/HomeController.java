package com.example.finance_tracker.controller;

import com.example.finance_tracker.dto.TransactionRowDto;
import com.example.finance_tracker.form.FilterTransactionsForm;
import com.example.finance_tracker.form.InsightExportPDFForm;
import com.example.finance_tracker.security.CustomUserDetails;
import com.example.finance_tracker.service.CSVImportService;
import com.example.finance_tracker.service.InsightSummaryService;
import com.example.finance_tracker.service.TransactionFilterService;
import com.example.finance_tracker.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.thymeleaf.TemplateEngine;

import java.util.List;

@Controller
@Slf4j
public class HomeController {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private CSVImportService csvImportService;

    @Autowired
    private TransactionFilterService transactionFilterService;

    @Autowired
    private InsightSummaryService insightSummaryService;


    @Autowired
    private TransactionService transactionService;

    @GetMapping({"/", "/index"})
    public String home (Model model) {
        return "redirect:/dashboard";
    }


    @ModelAttribute("filterTransactionsForm")
    public FilterTransactionsForm initFilterTransactionsForm(Model model) {
        if (!model.containsAttribute("filterTransactionsForm")) {
            return new FilterTransactionsForm();
        }
        return (FilterTransactionsForm) model.getAttribute("filterTransactionsForm");
    }


    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails user, Model model) {

        List<TransactionRowDto> transactions = transactionService.findByUserId(user.getId());
        if (transactions != null) {
            if (!model.containsAttribute("transactions")) {
                model.addAttribute("transactions", transactions);
                model.addAttribute("summary", insightSummaryService.get(transactions));
            }
            if (!model.containsAttribute("categories")) {
                List<String> categories = csvImportService.extractUniqueCategories(transactions);
                model.addAttribute("categories", categories);
            }

            if (!model.containsAttribute("subCategories")) {
                List<String> subCategories = csvImportService.extractUniqueSubCategories(transactions);
                model.addAttribute("subCategories", subCategories);
            }

            if (!model.containsAttribute("insightExportPDFForm"))
                model.addAttribute("insightExportPDFForm", new InsightExportPDFForm());

        }
        return "index.html";
    }
}
