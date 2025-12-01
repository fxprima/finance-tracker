package com.example.finance_tracker.model;

import com.example.finance_tracker.model.base.CategoryBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class Category extends CategoryBase {
    private Long userId;
}
