package com.example.finance_tracker.service.impl;

import com.example.finance_tracker.dto.InsightSummaryDTO;
import com.example.finance_tracker.dto.TransactionRowDto;
import com.example.finance_tracker.service.InsightSummaryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InsightSummaryServiceImpl implements InsightSummaryService {

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


        res.setTotalExpenses(totalExpenses);
        res.setTotalTransactions(transactions.size());
        res.setMaxExpenses(maxExpenses);
        res.setTotalIncome(totalIncome);
        res.setCashFlow(totalIncome - totalExpenses);
        res.setCashFlowPercentage((res.getCashFlow() / res.getTotalIncome()) * 100);
        return res;
    }
}
