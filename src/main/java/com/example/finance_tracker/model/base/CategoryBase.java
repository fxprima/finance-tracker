package com.example.finance_tracker.model.base;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class CategoryBase{
    private Long id;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
