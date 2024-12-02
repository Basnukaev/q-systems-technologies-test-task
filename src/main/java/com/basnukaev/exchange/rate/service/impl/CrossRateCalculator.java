package com.basnukaev.exchange.rate.service.impl;

import com.basnukaev.exchange.rate.domain.Currency;
import com.basnukaev.exchange.rate.domain.ExchangeRate;
import com.basnukaev.exchange.rate.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CrossRateCalculator {

    private final ExchangeRateRepository exchangeRateRepository;

    public void calculateAndSaveCrossRates(Currency baseEuro, List<Currency> currenciesExceptEuro, LocalDate date) {
        for (Currency baseCurrency : currenciesExceptEuro) {
            for (Currency targetCurrency : currenciesExceptEuro) {
                if (baseCurrency.equals(targetCurrency)) {
                    continue;
                }
                BigDecimal baseToEur = exchangeRateRepository.findOneByBaseCurrencyAndTargetCurrencyAndDate(
                                baseCurrency, baseEuro, date)
                        .map(ExchangeRate::getRate)
                        .orElseThrow(() -> new IllegalStateException("Exchange rate not found"));

                BigDecimal targetToEur = exchangeRateRepository.findOneByBaseCurrencyAndTargetCurrencyAndDate(
                                targetCurrency, baseEuro, date)
                        .map(ExchangeRate::getRate)
                        .orElseThrow(() -> new IllegalStateException("Exchange rate not found"));

                BigDecimal crossRate = baseToEur.divide(targetToEur, 6, RoundingMode.HALF_UP);

                ExchangeRate exchangeRate = new ExchangeRate();
                exchangeRate.setBaseCurrency(baseCurrency);
                exchangeRate.setTargetCurrency(targetCurrency);
                exchangeRate.setRate(crossRate);
                exchangeRate.setDate(date);

                exchangeRateRepository.save(exchangeRate);
            }
        }
    }
}
