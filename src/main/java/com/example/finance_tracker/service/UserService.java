package com.example.finance_tracker.service;

import com.example.finance_tracker.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    public User createUser(User user);
    public List<User> findAll();
    public User findByEmail(String email);
    public User findById(Long id);
}
