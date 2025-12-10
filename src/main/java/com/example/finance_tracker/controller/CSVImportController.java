package com.example.finance_tracker.controller;

import com.example.finance_tracker.common.contants.CSVFormatOption;
import com.example.finance_tracker.common.utils.alert.ConfirmDialog;
import com.example.finance_tracker.common.utils.alert.Modal;
import com.example.finance_tracker.common.utils.security.SecurityUtils;
import com.example.finance_tracker.dto.ImportFormatOptionsDto;
import com.example.finance_tracker.dto.TransactionRowDto;
import com.example.finance_tracker.exception.InvalidCSVFormatException;
import com.example.finance_tracker.form.AccountRegisterForm;
import com.example.finance_tracker.form.FilterTransactionsForm;
import com.example.finance_tracker.form.ImportCSVForm;
import com.example.finance_tracker.form.InsightExportPDFForm;
import com.example.finance_tracker.security.CustomUserDetails;
import com.example.finance_tracker.service.CSVImportService;
import com.example.finance_tracker.service.InsightSummaryService;
import com.example.finance_tracker.service.TransactionFilterService;
import com.example.finance_tracker.service.TransactionService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/import")
public class CSVImportController {

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
            ra.addFlashAttribute("summary", insightSummaryService.get(transactions));
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

        List<TransactionRowDto> filtered = transactionFilterService.applyFilter(all, form);

        InsightExportPDFForm exportForm = new InsightExportPDFForm();
        exportForm.setFilterForm(form);

        ra.addFlashAttribute("insightExportPDFForm", exportForm);
        ra.addFlashAttribute("transactions", filtered);
        ra.addFlashAttribute("filterTransactionsForm", form);
        ra.addFlashAttribute("summary", insightSummaryService.get(filtered));

        return "redirect:/import/";
    }

    @PostMapping("/save")
    public String saveDialog(@AuthenticationPrincipal CustomUserDetails user, Model model, HttpSession session, RedirectAttributes ra) {

        if (!SecurityUtils.isLoggedIn()) {
            model.addAttribute("accountRegisterForm", new AccountRegisterForm());
            return "pages/auth/register";
        }

        if (!transactionService.hasTransactions(user.getId()))
            return this.saveReplace(user, model, session, ra);

        ConfirmDialog.show(
                ra,
                "Imported transactions detected, choose what to do:",
                "Cancel",
                new ConfirmDialog.ConfirmAction("Append", "/import/save/append", "POST"),
                new ConfirmDialog.ConfirmAction("Replace Existing", "/import/save/replace", "POST")
        );

        return "redirect:/import/";
    }

    @PostMapping("/save/append")
    public String saveAppend(@AuthenticationPrincipal CustomUserDetails user, Model model, HttpSession session, RedirectAttributes ra) {
        List<TransactionRowDto> all =
                (List<TransactionRowDto>) session.getAttribute("IMPORTED_TRANSACTIONS");

        try {
            int rowsAffected = csvImportService.saveTransactions(user.getId(), all, true);
            Modal.addSuccess(ra, "You have successfully imported %d transactions into your database".formatted(rowsAffected));
        } catch (Exception e) {
            Modal.addError(ra, "An error occurred: %s".formatted(e.getMessage()));
            return "redirect:/import/";
        }

        session.removeAttribute("IMPORTED_TRANSACTIONS");
        return "redirect:/import/";
    }

    @PostMapping("/save/replace")
    public String saveReplace(@AuthenticationPrincipal CustomUserDetails user, Model model, HttpSession session, RedirectAttributes ra) {
        List<TransactionRowDto> all =
                (List<TransactionRowDto>) session.getAttribute("IMPORTED_TRANSACTIONS");

        try {
            int rowsAffected = csvImportService.saveTransactions(user.getId(), all, false);
            Modal.addSuccess(ra, "You have successfully imported %d transactions into your database".formatted(rowsAffected));
        } catch (Exception e) {
            Modal.addError(ra, "An error occurred: %s".formatted(e.getMessage()));
            return "redirect:/import/";
        }

        session.removeAttribute("IMPORTED_TRANSACTIONS");
        return "redirect:/import/";
    }


    @PostMapping("/export-pdf")
    public ResponseEntity<byte[]> exportPDF(
            HttpSession session,
            @ModelAttribute("insightExportPDFForm") InsightExportPDFForm form
    ) {

        List<TransactionRowDto> all =
                (List<TransactionRowDto>) session.getAttribute("IMPORTED_TRANSACTIONS");


        List<TransactionRowDto> filtered = transactionFilterService.applyFilter(all, form.getFilterForm());

        Context ctx = new Context();
        ctx.setVariable("transactions", filtered);
        ctx.setVariable("summary", insightSummaryService.get(filtered));
        ctx.setVariable("form", form);
        ctx.setVariable("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        String html = templateEngine.process("pdf/insight-report", ctx);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();

        builder.useFastMode();
        builder.withHtmlContent(html, null);
        builder.toStream(out);

        try {
            builder.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        byte[] pdfBytes = out.toByteArray();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String filename = "finance-report-" + timestamp + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);

    }


}