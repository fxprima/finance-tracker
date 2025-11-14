package com.example.finance_tracker.service.impl;

import com.example.finance_tracker.common.contants.CSVFormatOption;
import com.example.finance_tracker.common.utils.csv.CSVUtils;
import com.example.finance_tracker.exception.InvalidCSVFormatException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Service
public class CSVFormatValidator {

    public void validate(MultipartFile file, CSVFormatOption formatOption) {
        this.validateNotEmpty(file);
        this.validateIsCSV(file);
        this.validateHeader(file, formatOption);
    }

    private void validateNotEmpty(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new InvalidCSVFormatException("File tidak boleh kosong.");
    }

    private void validateIsCSV(MultipartFile file) {
        if (!file.getOriginalFilename().toLowerCase().endsWith(".csv"))
            throw new InvalidCSVFormatException("Hanya boleh file bertipe .csv");
    }

    private void validateHeader(MultipartFile file, CSVFormatOption formatOption) {

        if(formatOption != CSVFormatOption.MONY && formatOption != CSVFormatOption.MONEY_TRACKER)
            throw new InvalidCSVFormatException("Format harus antara MONY atau Money Tracker");

        List <String> expected = this.expectedHeader(formatOption);

        List <String> header = CSVUtils.parseHeader(file);

        if (!header.equals(expected))
            throw new InvalidCSVFormatException("Format tidak sesuai format MONY. Harus berisi kolom : %s".formatted(expected));

    }

    private List<String> expectedHeader(CSVFormatOption formatOption) {
        return switch (formatOption) {
            case NONE -> null;
            case MONY -> List.of(
                    "Date (UTC)",
                    "Type",
                    "From account / from category",
                    "From subcategory",
                    "To account / to category",
                    "To subcategory",
                    "Amount 1",
                    "Currency 1",
                    "Amount 2",
                    "Currency 2",
                    "Commission",
                    "Commission currency",
                    "Tax",
                    "Tax currency",
                    "Comment"
            );
            case MONEY_TRACKER -> null;
        };
    }


}
