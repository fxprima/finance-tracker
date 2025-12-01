package com.example.finance_tracker.mapper;

import com.example.finance_tracker.model.Category;
import com.example.finance_tracker.model.SubCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SubCategoryMapper {
    List<SubCategory> findAll();
    void insert(SubCategory subCategory);
}
