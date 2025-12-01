package com.example.finance_tracker.service.impl;

import com.example.finance_tracker.dto.TransactionRowDto;
import com.example.finance_tracker.mapper.CategoryMapper;
import com.example.finance_tracker.mapper.SubCategoryMapper;
import com.example.finance_tracker.mapper.TransactionMapper;
import com.example.finance_tracker.model.Category;
import com.example.finance_tracker.model.SubCategory;
import com.example.finance_tracker.model.TransactionRecord;
import com.example.finance_tracker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    SubCategoryMapper subCategoryMapper;

    @Autowired
    TransactionMapper transactionMapper;

    @Override
    @Transactional
    public void createTransaction(Long userId,TransactionRowDto transactionRowDto) {
        Category category = new Category();
        category.setDescription(transactionRowDto.getCategory());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        category.setUserId(userId);

        categoryMapper.insert(category);

        if (transactionRowDto.getSubCategory() != null || !transactionRowDto.getSubCategory().isEmpty()){
            SubCategory subCategory = new SubCategory();
            subCategory.setCategoryId(category.getId());
            subCategory.setCreatedAt(LocalDateTime.now());
            subCategory.setUpdatedAt(LocalDateTime.now());
            subCategory.setDescription(transactionRowDto.getSubCategory());

            subCategoryMapper.insert(subCategory);
        }

        TransactionRecord transactionRecord = new TransactionRecord();
        transactionRecord.setUserId(userId);
        transactionRecord.setTransactionType(transactionRowDto.getTransactionType());
        transactionRecord.setAmount(transactionRowDto.getAmount());
        transactionRecord.setNote(transactionRowDto.getNote());
        transactionRecord.setDate(transactionRowDto.getDate());
        transactionRecord.setCurrency(transactionRowDto.getCurrency());
        transactionRecord.setCategoryId(category.getId());
        transactionRecord.setCreatedAt(LocalDateTime.now());
        transactionRecord.setUpdatedAt(LocalDateTime.now());

        transactionMapper.insert(transactionRecord);
    }
}
