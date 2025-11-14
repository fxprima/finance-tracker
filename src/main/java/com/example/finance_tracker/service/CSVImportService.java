package com.example.finance_tracker.service;

import com.example.finance_tracker.common.contants.CSVFormatOption;
import com.example.finance_tracker.common.contants.TransactionType;
import com.example.finance_tracker.dto.TransactionRowDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface CSVImportService {
    public List<TransactionRowDto> preview (MultipartFile file, CSVFormatOption formatOption);
}
