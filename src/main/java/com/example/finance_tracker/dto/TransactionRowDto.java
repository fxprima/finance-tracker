package com.example.finance_tracker.dto;

import com.example.finance_tracker.common.contants.TransactionType;
import com.example.finance_tracker.model.base.TransactionBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Currency;

@EqualsAndHashCode(callSuper = true)
@Data
public class TransactionRowDto extends TransactionBase {
    private String category;
    private String subCategory;
}
