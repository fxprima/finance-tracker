package com.example.finance_tracker.service.impl;

import com.example.finance_tracker.dto.InsightSummaryDTO;
import com.example.finance_tracker.dto.TransactionRowDto;
import com.example.finance_tracker.service.ForecastService;
import com.example.finance_tracker.service.InsightSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class InsightSummaryServiceImpl implements InsightSummaryService {

    @Autowired
    ForecastService forecastService;

    @Override
    public InsightSummaryDTO get(List<TransactionRowDto> transactions) {
        InsightSummaryDTO res = new InsightSummaryDTO();

        if (transactions == null || transactions.isEmpty())
            return res;

        double totalIncome = transactions.stream()
                .filter(
                        tx -> tx.getTransactionType()
                                .name()
                                .equalsIgnoreCase("INCOME"))
                .mapToDouble(TransactionRowDto::getAmount)
                .sum();

        double totalExpenses = transactions.stream()
                .filter(
                        tx -> tx.getTransactionType()
                                .name()
                                .equalsIgnoreCase("EXPENSE"))
                .mapToDouble(TransactionRowDto::getAmount)
                .sum();

        double maxExpenses = transactions.stream()
                .filter(
                        tx -> tx.getTransactionType()
                                .name()
                                .equalsIgnoreCase("EXPENSE"))
                .mapToDouble(TransactionRowDto::getAmount)
                .max()
                .orElse(0.0);

        LocalDateTime minDate = transactions.stream()
                .map(TransactionRowDto::getDate)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime maxDate = transactions.stream()
                .map(TransactionRowDto::getDate)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        long days = 0;
        if (minDate != null && maxDate != null)
            days = ChronoUnit.DAYS.between(minDate, maxDate) + 1;

        double avgExpensesPerDay = (days > 0) ? (totalExpenses / days) : 0.0;

        res.setForecast30day(forecastService.predict(transactions));
        res.setAvgExpensesPerDay(avgExpensesPerDay);
        res.setTotalExpenses(totalExpenses);
        res.setTotalTransactions(transactions.size());
        res.setMaxExpenses(maxExpenses);
        res.setTotalIncome(totalIncome);
        res.setCashFlow(totalIncome - totalExpenses);
        res.setCashFlowPercentage((res.getCashFlow() / res.getTotalIncome()) * 100);
        return res;
    }
}
