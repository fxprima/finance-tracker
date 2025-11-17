package com.example.finance_tracker.service.impl;

import com.example.finance_tracker.common.contants.CSVFormatOption;
import com.example.finance_tracker.dto.FilterTransactionDto;
import com.example.finance_tracker.dto.TransactionRowDto;
import com.example.finance_tracker.form.FilterTransactionsForm;
import com.example.finance_tracker.service.CSVImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

                // Category
                .filter(tx -> {
                    if (filter.getCategory() == null || filter.getCategory().isEmpty()) return true;
                    return filter.getCategory().equalsIgnoreCase(tx.getCategory());
                })

                // Sub Category
                .filter(tx -> {
                    if (filter.getSubCategory() == null || filter.getSubCategory().isEmpty()) return true;
                    return filter.getSubCategory().equalsIgnoreCase(tx.getSubCategory());
                })

                // Type
                .filter(tx -> {
                    if (filter.getType() == null || filter.getType().isEmpty()) return true;
                    return filter.getType().equalsIgnoreCase(tx.getTransactionType().name());
                })

                .toList();
    }
}
