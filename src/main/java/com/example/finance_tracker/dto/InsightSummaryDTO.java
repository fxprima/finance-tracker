package com.example.finance_tracker.dto;

import lombok.Data;

@Data
public class InsightSummaryDTO {
    private long totalTransactions;
    private double totalIncome;
    private double totalExpenses;
    private double maxExpenses;
    private double cashFlow;
    private double cashFlowPercentage;

    private double avgExpensesPerDay;
    private double forecast30day;
}
