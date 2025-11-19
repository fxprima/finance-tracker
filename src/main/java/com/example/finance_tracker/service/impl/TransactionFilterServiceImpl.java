package com.example.finance_tracker.service.impl;

import com.example.finance_tracker.dto.TransactionRowDto;
import com.example.finance_tracker.form.FilterTransactionsForm;
import com.example.finance_tracker.service.TransactionFilterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionFilterServiceImpl implements TransactionFilterService {

    @Override
    public List<TransactionRowDto> applyFilter(List<TransactionRowDto> transactions, FilterTransactionsForm filter) {
        return transactions.stream()

                // Start Date
                .filter(tx -> {
                    if (filter.getStartDate() == null) return true;
                    return !tx.getDate().isBefore(filter.getStartDate().atStartOfDay());
                })

                // End Date
                .filter(tx -> {
                    if (filter.getEndDate() == null) return true;
                    return !tx.getDate().isAfter(filter.getEndDate().atTime(23, 59, 59));
                })

                // Categories (multi-select)
                .filter(tx -> {
                    List<String> cats = filter.getCategories();
                    if (cats == null || cats.isEmpty()) return true;
                    return cats.stream()
                            .anyMatch(cat -> cat.equalsIgnoreCase(tx.getCategory()));
                })

                // Sub Categories
                .filter(tx -> {
                    List<String> cats = filter.getSubCategories();
                    if (cats == null || cats.isEmpty()) return true;
                    return cats.stream()
                            .anyMatch(cat -> cat.equalsIgnoreCase(tx.getSubCategory()));
                })

                // Types
                .filter(tx -> {
                    List<String> types = filter.getTypes();
                    if (types == null || types.isEmpty()) return true;

                    return types.stream()
                            .anyMatch(t -> t.equalsIgnoreCase(tx.getTransactionType().name()));
                })

                // Exclude Categories
                .filter(tx -> {
                    List<String> exCats = filter.getExcludeCategories();
                    if (exCats == null || exCats.isEmpty()) return true;
                    return exCats.stream()
                            .noneMatch(cat -> cat.equalsIgnoreCase(tx.getCategory()));
                })

                // Exclude SubCategories
                .filter(tx -> {
                    List<String> exSubCats = filter.getExcludeSubCategories();
                    if (exSubCats == null || exSubCats.isEmpty()) return true;
                    return exSubCats.stream()
                            .noneMatch(sc -> sc.equalsIgnoreCase(tx.getSubCategory()));
                })

                // Exclude Types
                .filter(tx -> {
                    List<String> exTypes = filter.getExcludeTypes();
                    if (exTypes == null || exTypes.isEmpty()) return true;
                    return exTypes.stream()
                            .noneMatch(t -> t.equalsIgnoreCase(tx.getTransactionType().name()));
                })

                .toList();
    }
}
