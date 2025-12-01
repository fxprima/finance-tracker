package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.TransactionRowDto;
import com.example.finance_tracker.model.TransactionRecord;
import org.springframework.stereotype.Service;

@Service
public interface TransactionService {
   void createTransaction (Long userId, TransactionRowDto transactionRowDto);
}
