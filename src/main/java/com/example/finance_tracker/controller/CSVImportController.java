package com.example.finance_tracker.controller;

import com.example.finance_tracker.common.contants.CSVFormatOption;
import com.example.finance_tracker.dto.ImportFormatOptionsDto;
import com.example.finance_tracker.exception.InvalidCSVFormatException;
import com.example.finance_tracker.form.ImportCSVForm;
import com.example.finance_tracker.service.importcsv.CSVFormatValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/import")
public class CSVImportController {

    private final CSVFormatValidator validator;

    final private List<ImportFormatOptionsDto> formatOptions = new ArrayList<>(
            List.of(
                    new ImportFormatOptionsDto("Mony", CSVFormatOption.MONY),
                    new ImportFormatOptionsDto("Money Tracker", CSVFormatOption.MONEY_TRACKER)
            )
    );


    @ModelAttribute("formatOptions")
    public List<ImportFormatOptionsDto> populateFormatOptions() {
        return formatOptions;
    }


    @GetMapping({"/", "/index"})
    public String view(Model model, ImportCSVForm form) {
        return "pages/import";
    }

    @PostMapping({"/", "/index"})
    public String handle(ImportCSVForm form) {

        try {
            log.info("Start import CSV, filename = {}, format = {}", form.getFile().getOriginalFilename(), form.getFormatOptionId());

            validator.validate(form.getFile(), form.getFormatOptionId());



        } catch (InvalidCSVFormatException e) {
            log.error("Invalid format exception: %s", e.getMessage());
        }

        return "pages/import";
    }

}
