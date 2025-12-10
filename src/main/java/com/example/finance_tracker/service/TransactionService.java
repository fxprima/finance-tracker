package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.TransactionRowDto;
import com.example.finance_tracker.model.TransactionRecord;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionService {
   void createTransaction (Long userId, TransactionRowDto transactionRowDto);

   List<TransactionRowDto> findByUserId(Long userId);
   int deleteByUserId(Long userId);

   boolean hasTransactions(Long userId);
}
