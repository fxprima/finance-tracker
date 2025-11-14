package com.example.finance_tracker.common.utils.csv;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class CSVUtils {
    public static List<String> parseHeader (MultipartFile file) {
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            CSVReader csvReader = new CSVReader(reader);
            return Arrays.stream(csvReader.readNext()).map(String::trim).toList();
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }
}
