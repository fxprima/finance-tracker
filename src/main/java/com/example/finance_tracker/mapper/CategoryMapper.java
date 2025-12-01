package com.example.finance_tracker.mapper;

import com.example.finance_tracker.model.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List <Category> findAll();
    void insert(Category category);
}
