package com.example.finance_tracker.service.impl;

import com.example.finance_tracker.common.contants.CSVFormatOption;
import com.example.finance_tracker.dto.TransactionRowDto;
import com.example.finance_tracker.service.CSVImportService;
import com.example.finance_tracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CSVImportServiceImpl implements CSVImportService {

    private final CSVFormatValidator validator;
    private final CSVParser parser;

    @Autowired
    private TransactionService transactionService;

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
    @Transactional
    public Integer saveTransactions(Long userId, List<TransactionRowDto> transactions, Boolean append) {

        try {

            if (append) {
                Set<String> existingKeys = transactionService.findByUserId(userId)
                        .stream()
                        .map(t -> t.getDate() + "|" +
                                t.getAmount() + "|" +
                                t.getCategory() + "|" +
                                t.getNote() + "|" +
                                t.getTransactionType())
                        .collect(Collectors.toSet());

                List<TransactionRowDto> filtered = transactions.stream()
                        .filter(t -> existingKeys.add(
                                t.getDate() + "|" +
                                        t.getAmount() + "|" +
                                        t.getCategory() + "|" +
                                        t.getNote() + "|" +
                                        t.getTransactionType()
                        ))
                        .toList();

                for (TransactionRowDto transaction : filtered)
                    transactionService.createTransaction(userId, transaction);

                return filtered.size();
            } else {
                transactionService.deleteByUserId(userId);
                for(TransactionRowDto transaction : transactions)
                    transactionService.createTransaction(userId, transaction);

                return transactions.size();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
