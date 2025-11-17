package com.example.finance_tracker.service.impl;

import com.example.finance_tracker.common.contants.CSVFormatOption;
import com.example.finance_tracker.common.contants.TransactionType;
import com.example.finance_tracker.common.utils.parser.DateTimeParser;
import com.example.finance_tracker.dto.TransactionRowDto;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Service
@Slf4j
public class CSVParser {

    public List <TransactionRowDto> parse (MultipartFile file, CSVFormatOption formatOption) {

        List<TransactionRowDto> result;
        Integer invalidDataCount = 0;
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            CSVReader csvReader = new CSVReader(reader);
            String[] header = csvReader.readNext();
            String[] line;


            result = new ArrayList<>();
            while ((line = csvReader.readNext()) != null) {
                TransactionRowDto row = new TransactionRowDto();

                switch (formatOption) {
                    case MONY -> {

                        if (line.length <= 14) {
                            invalidDataCount++;
                            continue;
                        }

                        LocalDateTime date = DateTimeParser.convertUtcToLocalDateTime(line[0]);
                        row.setDate(date);

                        TransactionType transactionType = "EXPENSE".toLowerCase().equals(line[1].toLowerCase()) ? TransactionType.EXPENSE : TransactionType.INCOME;
                        row.setTransactionType(transactionType);

                        row.setCategory(
                                (transactionType == TransactionType.EXPENSE) ?
                                        line[4] : line[2]
                        );

                        row.setSubCategory(line[3]);
                        row.setAmount(Double.parseDouble(line[6]));
                        row.setCurrency(Currency.getInstance(line[7]));
                        row.setNote(line[14]);
                    }

                    case MONEY_TRACKER, NONE -> {

                    }

                }

                result.add(row);
            }

        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw e;
        }

        return result;
    }

}
