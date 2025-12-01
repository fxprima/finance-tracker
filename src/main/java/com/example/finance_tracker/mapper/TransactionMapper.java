package com.example.finance_tracker.mapper;

import com.example.finance_tracker.model.TransactionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TransactionMapper {

    List<TransactionRecord> findAll();

    List<TransactionRecord> findByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    void insert(TransactionRecord tx);
}