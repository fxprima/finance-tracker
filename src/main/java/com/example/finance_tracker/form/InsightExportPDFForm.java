package com.example.finance_tracker.form;
import lombok.Data;


@Data
public class InsightExportPDFForm {
    private FilterTransactionsForm filterForm = new FilterTransactionsForm();

    private String heatmapImageBase64;
    private String categoryChartImageBase64;
    private String trendChartImageBase64;
    private String topCategoriesImageBase64;
}