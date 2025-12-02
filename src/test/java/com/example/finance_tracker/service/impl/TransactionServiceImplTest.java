package com.example.finance_tracker.service.impl;

import com.example.finance_tracker.common.contants.TransactionType;
import com.example.finance_tracker.dto.TransactionRowDto;
import com.example.finance_tracker.model.Category;
import com.example.finance_tracker.model.SubCategory;
import com.example.finance_tracker.model.User;
import com.example.finance_tracker.service.CategoryService;
import com.example.finance_tracker.service.TransactionService;
import com.example.finance_tracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class TransactionServiceImplTest {

    @Autowired
    CategoryService categoryService;

    @Autowired
    UserService userService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void createTransaction_ShouldInsertToDatabase() {
        User user = new User();
        user.setPassword("password");
        userService.createUser(user);
        assertNotNull(user.getId());

        TransactionRowDto transactionRowDto = new TransactionRowDto();
        transactionRowDto.setTransactionType(TransactionType.EXPENSE);
        transactionRowDto.setNote("Note");
        transactionRowDto.setCurrency(Currency.getInstance("IDR"));
        transactionRowDto.setAmount(3000.0);
        transactionRowDto.setDate(LocalDateTime.now());

        transactionRowDto.setCategory("Cat");
        transactionRowDto.setSubCategory("SubCat");

        transactionService.createTransaction(user.getId(), transactionRowDto);

        Integer txCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) 
                FROM transactions 
                WHERE user_id = ? 
                  AND amount = ? 
                  AND note = ? 
                  AND type = ?
                """,
                Integer.class,
                user.getId(),
                transactionRowDto.getAmount(),
                transactionRowDto.getNote(),
                transactionRowDto.getTransactionType().name()
        );

        assertEquals(1, txCount, "Transaction tidak tersimpan sesuai expected");

        Long categoryId = jdbcTemplate.queryForObject(
                """
                SELECT id 
                FROM categories 
                WHERE user_id = ? 
                  AND description = ?
                """,
                Long.class,
                user.getId(),
                transactionRowDto.getCategory()
        );
        assertNotNull(categoryId, "Category harusnya auto dibuat");

        Long subCategoryId = jdbcTemplate.queryForObject(
                """
                SELECT id 
                FROM sub_categories 
                WHERE  category_id = ? 
                  AND description = ?
                """,
                Long.class,
                categoryId,
                transactionRowDto.getSubCategory()
        );
        assertNotNull(subCategoryId, "SubCategory harusnya auto dibuat dan link ke category");

        Integer txWithCatCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) 
                FROM transactions 
                WHERE user_id = ? 
                  AND category_id = ? 
                  AND amount = ?
                  AND note = ?
                  AND type = ?
                """,
                Integer.class,
                user.getId(),
                categoryId,
                transactionRowDto.getAmount(),
                transactionRowDto.getNote(),
                transactionRowDto.getTransactionType().name()
        );

        assertEquals(1, txWithCatCount, "Transaction harusnya pakai category & subcategory yang baru dibuat");
    }
}
