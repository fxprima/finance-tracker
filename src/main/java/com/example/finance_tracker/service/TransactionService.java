package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.TransactionRowDto;
import com.example.finance_tracker.model.TransactionRecord;

public interface TransactionService {
   void createTransaction (Long userId, TransactionRowDto transactionRowDto);
}
