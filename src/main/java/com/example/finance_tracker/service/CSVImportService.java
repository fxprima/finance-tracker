package com.example.finance_tracker.service;

import com.example.finance_tracker.common.contants.CSVFormatOption;
import com.example.finance_tracker.common.contants.TransactionType;
import com.example.finance_tracker.dto.FilterTransactionDto;
import com.example.finance_tracker.dto.TransactionRowDto;
import com.example.finance_tracker.form.FilterTransactionsForm;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface CSVImportService {
    public List<TransactionRowDto> parseAllTransactions (MultipartFile file, CSVFormatOption formatOption);

    public List <String> extractUniqueCategories(List <TransactionRowDto> transactions);

    public List <String> extractUniqueSubCategories(List <TransactionRowDto> transactions);

    public List<TransactionRowDto> filterTransactions (List <TransactionRowDto> transactions, FilterTransactionsForm filter);
}
