package com.example.finance_tracker.service.impl;

import com.example.finance_tracker.model.Category;
import com.example.finance_tracker.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class CategoryServiceImplTest {

    @Autowired
    CategoryService categoryService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void createCategory_ShouldInsertToDatabase() {

        for (int i = 0; i < 5; i++) {

            Long userId = (long) (i + 1);
            String description = "Category %d".formatted(i + 1);

            Category category = categoryService.createCategory(userId, description);

            assertNotNull(category.getId());
            assertEquals(userId, category.getUserId());
            assertEquals(description, category.getDescription());

            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM categories WHERE id = ? AND user_id = ? AND description = ?",
                    Integer.class,
                    category.getId(),
                    userId,
                    description
            );

            assertEquals(1, count);
        }
    }
}
