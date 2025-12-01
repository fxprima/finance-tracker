package com.example.finance_tracker.mapper;

import com.example.finance_tracker.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    int insert (User user);
    List<User> findAll();
}
