package com.example.finance_tracker.form;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FilterTransactionsForm {
    private LocalDate startDate;
    private LocalDate endDate;
    private String category;
    private String subCategory;
    private String type;
}
