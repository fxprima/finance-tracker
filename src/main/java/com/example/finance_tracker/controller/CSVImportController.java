package com.example.finance_tracker.controller;

import com.example.finance_tracker.common.contants.CSVFormatOption;
import com.example.finance_tracker.dto.ImportFormatOptionsDto;
import com.example.finance_tracker.dto.TransactionRowDto;
import com.example.finance_tracker.exception.InvalidCSVFormatException;
import com.example.finance_tracker.form.FilterTransactionsForm;
import com.example.finance_tracker.form.ImportCSVForm;
import com.example.finance_tracker.service.CSVImportService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/import")
public class CSVImportController {

    @Autowired
    private CSVImportService csvImportService;

    @ModelAttribute("formatOptions")
    public List<ImportFormatOptionsDto> populateFormatOptions() {
        return List.of(
                new ImportFormatOptionsDto("Mony", CSVFormatOption.MONY),
                new ImportFormatOptionsDto("Money Tracker", CSVFormatOption.MONEY_TRACKER)
        );
    }

    @ModelAttribute("importCSVForm")
    public ImportCSVForm initImportCSVForm() {
        return new ImportCSVForm();
    }

    @ModelAttribute("filterTransactionsForm")
    public FilterTransactionsForm initFilterTransactionsForm(Model model) {
        if (!model.containsAttribute("filterTransactionsForm")) {
            return new FilterTransactionsForm();
        }
        return (FilterTransactionsForm) model.getAttribute("filterTransactionsForm");
    }


    @GetMapping({"/", "/index"})
    public String view(Model model, HttpSession session) {
        List<TransactionRowDto> transactions =
                (List<TransactionRowDto>) session.getAttribute("IMPORTED_TRANSACTIONS");

        if (transactions != null) {
            if (!model.containsAttribute("transactions")) {
                model.addAttribute("transactions", transactions);
                model.addAttribute("summary", csvImportService.getInsightSummary(transactions));
            }
            if (!model.containsAttribute("categories")) {
                List<String> categories = csvImportService.extractUniqueCategories(transactions);
                model.addAttribute("categories", categories);
            }

            if (!model.containsAttribute("subCategories")) {
                List<String> subCategories = csvImportService.extractUniqueSubCategories(transactions);
                model.addAttribute("subCategories", subCategories);
            }
        }

        return "pages/import";
    }

    @PostMapping({"/", "/index"})
    public String handleImportCSV(
            @ModelAttribute("importCSVForm") ImportCSVForm form,
            HttpSession session,
            RedirectAttributes ra
    ) {
        try {
            log.info("Start import CSV, filename = {}, format = {}",
                    form.getFile().getOriginalFilename(), form.getFormatOptionId());

            List<TransactionRowDto> transactions = csvImportService.parseAllTransactions(
                    form.getFile(),
                    form.getFormatOptionId()
            );

            session.setAttribute("IMPORTED_TRANSACTIONS", transactions);

            ra.addFlashAttribute("transactions", transactions);
            ra.addFlashAttribute("summary", csvImportService.getInsightSummary(transactions));
            ra.addFlashAttribute("filterTransactionsForm", new FilterTransactionsForm());

        } catch (InvalidCSVFormatException e) {
            log.error("Invalid format exception", e);
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/import/";
    }

    @GetMapping("/filter")
    public String filterTransactions(
            @ModelAttribute("filterTransactionsForm") FilterTransactionsForm form,
            HttpSession session,
            RedirectAttributes ra
    ) {
        List<TransactionRowDto> all =
                (List<TransactionRowDto>) session.getAttribute("IMPORTED_TRANSACTIONS");

        if (all == null) {
            return "redirect:/import/";
        }

        List<TransactionRowDto> filtered = csvImportService.filterTransactions(all, form);

        ra.addFlashAttribute("transactions", filtered);
        ra.addFlashAttribute("filterTransactionsForm", form);
        ra.addFlashAttribute("summary", csvImportService.getInsightSummary(filtered));

        return "redirect:/import/";
    }
}