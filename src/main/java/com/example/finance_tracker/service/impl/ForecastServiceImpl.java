package com.example.finance_tracker.service.impl;

import com.example.finance_tracker.dto.TransactionRowDto;
import com.example.finance_tracker.service.ForecastService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ForecastServiceImpl implements ForecastService {
    private static final double ALPHA = 0.3;
    private static final double BETA  = 0.1;

    @Override
    public double predict(List<TransactionRowDto> transactions) {
        return calculateMonthlyForecast(transactions);
    }

    private long countDaysRange(List<TransactionRowDto> transactions) {
        LocalDateTime minDate = transactions.stream()
                .map(TransactionRowDto::getDate)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime maxDate = transactions.stream()
                .map(TransactionRowDto::getDate)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        if (minDate == null || maxDate == null) return 0;

        return ChronoUnit.DAYS.between(minDate, maxDate) + 1;
    }

    private double calculateMonthlyForecast(List<TransactionRowDto> transactions) {
        if (transactions == null || transactions.isEmpty())
            return 0.0;

        LocalDateTime now = LocalDateTime.now();

        double[] dailySeries = this.buildDailyExpenseSeriesHybrid(transactions, now, 7);

        if (dailySeries.length == 0)
            return 0.0;

        YearMonth currentMonth = YearMonth.from(now.toLocalDate());
        int currentDayOfMonth = now.getDayOfMonth();
        int totalDaysInMonth = currentMonth.lengthOfMonth();
        int remainingDays = totalDaysInMonth - currentDayOfMonth;

        double actualSoFar = transactions.stream()
                .filter(tx -> tx != null
                        && tx.getDate() != null
                        && tx.getTransactionType() != null
                        && "EXPENSE".equalsIgnoreCase(tx.getTransactionType().name()))
                .filter(tx -> {
                    LocalDateTime dt = tx.getDate();
                    return YearMonth.from(dt.toLocalDate()).equals(currentMonth)
                            && !dt.toLocalDate().isAfter(now.toLocalDate());
                })
                .mapToDouble(TransactionRowDto::getAmount)
                .sum();

        if (remainingDays <= 0) {
            return actualSoFar;
        }

        double forecastRemaining;

        if (dailySeries.length >= 3) {
            double next = holtNextForecast(dailySeries, ALPHA, BETA);
            forecastRemaining = next * remainingDays;
        } else {
            double next = sesNextForecast(dailySeries, ALPHA);
            if (next < 0) next = 0;
            forecastRemaining = next * remainingDays;
        }

        double forecastTotal = actualSoFar + forecastRemaining;

        return forecastTotal;
    }


    private double[] buildDailyExpenseSeriesHybrid(
            List<TransactionRowDto> transactions,
            LocalDateTime now,
            int minDaysWindow
    ) {
        if (transactions == null || transactions.isEmpty())
            return new double[0];

        if (minDaysWindow <= 0)
            minDaysWindow = 1;

        LocalDateTime endDateTime = now
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        LocalDateTime startOfMonth = endDateTime
                .withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        long daysFromMonthStart = ChronoUnit.DAYS.between(startOfMonth, endDateTime) + 1;
        LocalDateTime minStartByWindow = endDateTime.minusDays(minDaysWindow - 1);

        LocalDateTime startDateTime = (daysFromMonthStart >= minDaysWindow) ? startOfMonth : minStartByWindow;

        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate   = endDateTime.toLocalDate();

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        if (totalDays <= 0)
            return new double[0];

        Map<LocalDate, Double> dailyExpenses = new HashMap<>();

        for (TransactionRowDto tx : transactions) {
            if (tx == null || tx.getDate() == null || tx.getTransactionType() == null)
                continue;

            if (!"EXPENSE".equalsIgnoreCase(tx.getTransactionType().name()))
                continue;

            LocalDateTime txDateTime = tx.getDate();
            LocalDate txDate = txDateTime.toLocalDate();

            if (txDate.isBefore(startDate) || txDate.isAfter(endDate))
                continue;

            double amount = tx.getAmount();
            dailyExpenses.merge(txDate, amount, Double::sum);
        }

        double[] series = new double[(int) totalDays];
        for (int i = 0; i < totalDays; i++) {
            LocalDate d = startDate.plusDays(i);
            series[i] = dailyExpenses.getOrDefault(d, 0.0);
        }

        return series;
    }


    private double holtNextForecast(double[] x, double alpha, double beta) {
        if (x == null || x.length == 0) return 0.0;

        if (x.length == 1)
            return x[0];

        double s = x[0];
        double b = x[1] - x[0];

        for (int t = 1; t < x.length; t++) {
            double prevS = s;

            s = alpha * x[t] + (1 - alpha) * (s + b);
            b = beta * (s - prevS) + (1 - beta) * b;
        }

        return s + b;
    }

    private double sesNextForecast(double[] x, double alpha) {
        if (x == null || x.length == 0) return 0.0;

        double s = x[0];
        for (int t = 1; t < x.length; t++) {
            s = alpha * x[t] + (1 - alpha) * s;
        }
        return s;
    }

}
