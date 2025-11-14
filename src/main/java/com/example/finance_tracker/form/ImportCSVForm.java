package com.example.finance_tracker.form;

import com.example.finance_tracker.common.contants.CSVFormatOption;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImportCSVForm {

    private CSVFormatOption formatOptionId;
    private MultipartFile file;

}
