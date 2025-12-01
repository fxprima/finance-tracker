package com.example.finance_tracker.service.impl;

import com.example.finance_tracker.mapper.CategoryMapper;
import com.example.finance_tracker.mapper.SubCategoryMapper;
import com.example.finance_tracker.model.Category;
import com.example.finance_tracker.model.SubCategory;
import com.example.finance_tracker.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    SubCategoryMapper subCategoryMapper;

    /**
     * @param userId
     * @param description
     * @return
     */
    @Override
    public Category createCategory(Long userId, String description) {

        Category category = new Category();
        category.setUserId(userId);
        category.setDescription(description);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        categoryMapper.insert(category);
        return category;
    }

    /**
     * @param categoryId
     * @param description
     * @return
     */
    @Override
    public SubCategory createSubCategory(Long categoryId, String description) {
        SubCategory subCategory = new SubCategory();
        subCategory.setCategoryId(categoryId);
        subCategory.setDescription(description);
        subCategory.setCreatedAt(LocalDateTime.now());
        subCategory.setUpdatedAt(LocalDateTime.now());

        subCategoryMapper.insert(subCategory);
        return subCategory;
    }
}
