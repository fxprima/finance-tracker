package com.example.finance_tracker.dto;

import com.example.finance_tracker.common.contants.CSVFormatOption;
import lombok.Data;

@Data
public class ImportFormatOptionsDto {
    private String name;
    private CSVFormatOption formatId;

    public ImportFormatOptionsDto(String name, CSVFormatOption formatId) {
        this.name = name;
        this.formatId = formatId;
    }
}
