package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.InsightSummaryDTO;
import com.example.finance_tracker.dto.TransactionRowDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InsightSummaryService {
    public InsightSummaryDTO get (List<TransactionRowDto> transactions);

}
