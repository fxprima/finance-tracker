package com.example.finance_tracker.service;

import com.example.finance_tracker.model.Category;
import com.example.finance_tracker.model.SubCategory;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {

    Category createCategory (Long userId, String description);
    SubCategory createSubCategory (Long userId, Long categoryId, String description);

}
