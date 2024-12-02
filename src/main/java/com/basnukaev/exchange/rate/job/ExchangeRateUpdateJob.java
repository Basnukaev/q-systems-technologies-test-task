package com.basnukaev.exchange.rate.job;

import com.basnukaev.exchange.rate.service.ExchangeRateService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateUpdateJob {

    private final ExchangeRateService exchangeRateService;

    @PostConstruct
    public void initializeRates() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate dayBeforeYesterday = today.minusDays(2);

        boolean hasAllRates = exchangeRateService.hasForDate(today) &&
                exchangeRateService.hasForDate(yesterday) &&
                exchangeRateService.hasForDate(dayBeforeYesterday);

        if (!hasAllRates) {
            log.info("Initializing exchange rates for the last 3 days");
            exchangeRateService.upsertExchangeRatesForDate(dayBeforeYesterday);
            exchangeRateService.upsertExchangeRatesForDate(yesterday);
            exchangeRateService.upsertExchangeRatesForDate(today);
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // Каждый день в полночь
    public void updateDailyRates() {
        exchangeRateService.upsertExchangeRatesForDate(LocalDate.now());
    }
}
