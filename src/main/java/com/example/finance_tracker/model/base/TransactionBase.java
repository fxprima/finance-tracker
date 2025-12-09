package com.example.finance_tracker.model.base;

import com.example.finance_tracker.common.contants.TransactionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Currency;

@Data
public abstract class TransactionBase {
    protected Long id;
    protected LocalDateTime date;
    protected TransactionType transactionType;
    protected Double amount;
    protected String currency;
    protected String note;
}
