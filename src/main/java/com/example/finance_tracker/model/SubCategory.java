package com.example.finance_tracker.model;

import com.example.finance_tracker.model.base.CategoryBase;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class SubCategory extends CategoryBase {
    private Long categoryId;
}
