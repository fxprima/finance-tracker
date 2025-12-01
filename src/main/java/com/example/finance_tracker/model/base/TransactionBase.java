package com.example.finance_tracker.model.base;

import com.example.finance_tracker.common.contants.TransactionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Currency;

@Data
public abstract class TransactionBase {
    protected LocalDateTime date;
    protected TransactionType transactionType;
    protected Double amount;
    protected Currency currency;
    protected String note;
}
