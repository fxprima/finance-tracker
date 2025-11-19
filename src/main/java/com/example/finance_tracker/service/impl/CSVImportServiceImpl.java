package com.example.finance_tracker.service.impl;

import com.example.finance_tracker.common.contants.CSVFormatOption;
import com.example.finance_tracker.dto.FilterTransactionDto;
import com.example.finance_tracker.dto.InsightSummaryDTO;
import com.example.finance_tracker.dto.TransactionRowDto;
import com.example.finance_tracker.form.FilterTransactionsForm;
import com.example.finance_tracker.service.CSVImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CSVImportServiceImpl implements CSVImportService {

    private final CSVFormatValidator validator;
    private final CSVParser parser;

    @Override
    public List<TransactionRowDto> parseAllTransactions(MultipartFile file, CSVFormatOption formatOption) {
        validator.validate(file, formatOption);
        return parser.parse(file, formatOption);
    }

    @Override
    public List<String> extractUniqueCategories(List<TransactionRowDto> transactions) {
        return transactions.stream()
                .map(TransactionRowDto::getCategory)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    public List<String> extractUniqueSubCategories(List<TransactionRowDto> transactions) {
        return transactions.stream()
                .map(TransactionRowDto::getSubCategory)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    public List<TransactionRowDto> filterTransactions(List<TransactionRowDto> transactions, FilterTransactionsForm filter) {
        return transactions.stream()

                // Start Date
                .filter(tx -> {
                    if (filter.getStartDate() == null) return true;
                    return !tx.getDate().isBefore(filter.getStartDate().atStartOfDay());
                })

                // End Date
                .filter(tx -> {
                    if (filter.getEndDate() == null) return true;
                    return !tx.getDate().isAfter(filter.getEndDate().atTime(23, 59, 59));
                })

                // Categories (multi-select)
                .filter(tx -> {
                    List<String> cats = filter.getCategories();
                    if (cats == null || cats.isEmpty()) return true;
                    return cats.stream()
                            .anyMatch(cat -> cat.equalsIgnoreCase(tx.getCategory()));
                })

                // Sub Categories
                .filter(tx -> {
                    List<String> cats = filter.getSubCategories();
                    if (cats == null || cats.isEmpty()) return true;
                    return cats.stream()
                            .anyMatch(cat -> cat.equalsIgnoreCase(tx.getSubCategory()));
                })

                // Types
                .filter(tx -> {
                    List<String> types = filter.getTypes();
                    if (types == null || types.isEmpty()) return true;

                    return types.stream()
                            .anyMatch(t -> t.equalsIgnoreCase(tx.getTransactionType().name()));
                })

                .toList();
    }


    private double computeAverage (List <TransactionRowDto> transactions) {
        return transactions
                .stream()
                .mapToDouble(TransactionRowDto::getAmount)
                .average()
                .orElse(0.0);

    }

    @Override
    public InsightSummaryDTO getInsightSummary(List<TransactionRowDto> transactions) {
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

        return res;
    }
}
