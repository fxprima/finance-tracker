package com.example.finance_tracker.service.impl;

import com.example.finance_tracker.mapper.UserMapper;
import com.example.finance_tracker.model.User;
import com.example.finance_tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public User createUser(User user) {
        userMapper.insert(user);
        return user;
    }

    /**
     * @return
     */
    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }
}
