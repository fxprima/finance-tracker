package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.TransactionRowDto;
import com.example.finance_tracker.form.FilterTransactionsForm;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionFilterService {
    public List<TransactionRowDto> applyFilter(List <TransactionRowDto> transactions, FilterTransactionsForm filter);
}
