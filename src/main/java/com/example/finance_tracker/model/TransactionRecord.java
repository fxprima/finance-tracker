package com.example.finance_tracker.model;

import com.example.finance_tracker.dto.TransactionRowDto;
import com.example.finance_tracker.model.base.TransactionBase;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
public class TransactionRecord extends TransactionBase {
    private Long id;
    private Long userId;
    private Long categoryId;
    private Long subCategoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
