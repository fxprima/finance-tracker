package com.example.finance_tracker.service.impl;

import com.example.finance_tracker.model.User;
import com.example.finance_tracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testInsertFiveUsers() {

        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setFirstName("FirstName" + i);
            user.setLastName("LastName" + i);
            user.setUsername("testuser" + i);
            user.setEmail("testuser" + i + "@mail.com");
            user.setPhone("08123456" + i);

            userService.createUser(user);

            assertNotNull(user.getId(), "ID harus ke-set otomatis");
        }

        System.out.println("Successfully inserted 5 users!");
    }

    @ParameterizedTest
    @CsvSource({
            "user_a, user_a@mail.com, password123",
            "user_b, user_b@mail.com, password123",
            "user_c, user_c@mail.com, password123"
    })
    void createUser_ShouldInsert_ForVariousValidInputs(String username, String email, String password) {
        User user = new User();
        user.setFirstName("First");
        user.setLastName("Last");
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone("08123456");

        User saved = userService.createUser(user);

        assertNotNull(saved.getId());
        assertEquals(username, saved.getUsername());
        assertEquals(email, saved.getEmail());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE id = ? AND username = ? AND email = ?",
                Integer.class,
                saved.getId(),
                saved.getUsername(),
                saved.getEmail()
        );
        assertEquals(1, count);
    }

    @Test
    void findAll() {
        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setFirstName("First" + i);
            user.setPassword("Passsword" + i);
            user.setLastName("Last" + i);
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@mail.com");
            user.setPhone("08123" + i);

            userService.createUser(user);
        }

        var users = userService.findAll();

        assertNotNull(users);
        assertTrue(users.size() >= 5, "Minimal harus ada 5 user setelah insert");

        for (int i = 1; i <= 5; i++) {
            String expectedUsername = "user" + i;

            boolean exists = users.stream()
                    .anyMatch(u -> expectedUsername.equals(u.getUsername()));

            assertTrue(exists, "User dengan username '" + expectedUsername + "' harus ada di DB");
        }
    }
}
