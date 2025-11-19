package com.example.finance_tracker.form;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class FilterTransactionsForm {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private List <String> categories;
    private List <String> subCategories;
    private List <String> types;

    private List<String> excludeCategories;
    private List<String> excludeSubCategories;
    private List<String> excludeTypes;
}
