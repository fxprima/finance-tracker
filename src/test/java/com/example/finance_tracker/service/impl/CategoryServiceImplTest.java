package com.example.finance_tracker.service.impl;

import com.example.finance_tracker.model.Category;
import com.example.finance_tracker.model.SubCategory;
import com.example.finance_tracker.model.User;
import com.example.finance_tracker.service.CategoryService;
import com.example.finance_tracker.service.UserService;
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
    UserService userService;

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

    @Test
    void createCategory_ShouldThrow_WhenDescriptionIsEmpty() {
        User user = new User();
        user.setUsername("Test");
        user.setPassword("Test");
        userService.createUser(user);

        assertThrows(IllegalArgumentException.class, () ->
                categoryService.createCategory(user.getId(), "")
        );
    }

    @Test
    void createCategory_ShouldThrow_WhenDescriptionIsNull() {
        User user = new User();
        user.setUsername("Test");
        user.setPassword("Test");
        userService.createUser(user);

        assertThrows(IllegalArgumentException.class, () ->
                categoryService.createCategory(user.getId(), null)
        );
    }


    @Test
    void createSubCategory_ShouldInsertToDatabase() {
        User user = new User();
        user.setUsername("Test");
        user.setPassword("Test");
        userService.createUser(user);

        Long userId = user.getId();
        Category parent = categoryService.createCategory(userId, "Parent Category");

        String description = "SubCategory 1";

        SubCategory subCategory = categoryService.createSubCategory(
                parent.getId(),
                description
        );

        assertNotNull(subCategory.getId());
        assertEquals(parent.getId(), subCategory.getCategoryId());
        assertEquals(description, subCategory.getDescription());

        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) 
                FROM sub_categories 
                WHERE id = ? AND category_id = ? AND description = ?
                """,
                Integer.class,
                subCategory.getId(),
                parent.getId(),
                description
        );

        assertEquals(1, count);
    }

}
