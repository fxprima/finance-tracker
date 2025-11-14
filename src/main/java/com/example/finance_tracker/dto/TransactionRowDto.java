package com.example.finance_tracker.dto;

import com.example.finance_tracker.common.contants.TransactionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Currency;

@Data
public class TransactionRowDto {
    private LocalDateTime date;
    private TransactionType transactionType;
    private String category;
    private String subCategory;
    private Double amount;
    private Currency currency;
    private String note;
}
