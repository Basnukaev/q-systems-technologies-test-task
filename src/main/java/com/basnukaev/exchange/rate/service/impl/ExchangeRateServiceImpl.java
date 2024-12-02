package com.basnukaev.exchange.rate.service.impl;

import com.basnukaev.exchange.rate.domain.Currency;
import com.basnukaev.exchange.rate.domain.ExchangeRate;
import com.basnukaev.exchange.rate.dto.ExchangeRateDto;
import com.basnukaev.exchange.rate.dto.ExchangeRateResponse;
import com.basnukaev.exchange.rate.repository.ExchangeRateRepository;
import com.basnukaev.exchange.rate.service.CurrencyService;
import com.basnukaev.exchange.rate.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateApiClient exchangeRateApiClient;
    private final CrossRateCalculator crossRateCalculator;
    private final CurrencyService currencyService;
    private final ExchangeRateRepository exchangeRateRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ExchangeRateDto> getForCurrency(String currencyCode) {
        Currency currency = currencyService.findByCode(currencyCode)
                .orElseThrow(() -> new IllegalArgumentException("Currency not found: " + currencyCode));

        return exchangeRateRepository.findLatestForCurrency(currency, LocalDate.now()).stream()
                .map(this::mapToExchangeRateDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void upsertExchangeRatesForDate(LocalDate date) {
        try {
            Currency base = currencyService.findByCode("EUR")
                    .orElseThrow(() -> new IllegalStateException("EUR currency not found in database"));

            List<Currency> currenciesExceptEuro = currencyService.findAll().stream()
                    .filter(c -> !c.equals(base))
                    .toList();

            List<String> currencyCodes = currenciesExceptEuro.stream()
                    .map(Currency::getCode)
                    .collect(Collectors.toList());

            ExchangeRateResponse response = exchangeRateApiClient.getExchangeRates(date, currencyCodes);

            if (response != null && response.isSuccess()) {
                LocalDate exchangeRateDate = response.getDate();

                if (!date.equals(exchangeRateDate)) {
                    log.warn("Requested date {} differs from response date {}", date, exchangeRateDate);
                }

                saveExchangeRates(base, response.getRates(), exchangeRateDate);

                crossRateCalculator.calculateAndSaveCrossRates(
                        base,
                        currenciesExceptEuro,
                        exchangeRateDate
                );
            } else {
                log.error("Failed to get exchange rates: {}", response);
            }
        } catch (Exception e) {
            log.error("Error updating exchange rates for date {}: {}", date, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void save(ExchangeRate exchangeRate) {
        exchangeRateRepository.save(exchangeRate);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasForDate(LocalDate date) {
        return exchangeRateRepository.existsByDate(date);
    }

    private void saveExchangeRates(Currency baseCurrency, Map<String, BigDecimal> rates, LocalDate date) {
        rates.forEach((code, rate) -> {
            currencyService.findByCode(code).ifPresent(targetCurrency -> {
                ExchangeRate exchangeRate = new ExchangeRate();
                exchangeRate.setBaseCurrency(baseCurrency);
                exchangeRate.setTargetCurrency(targetCurrency);
                exchangeRate.setRate(rate);
                exchangeRate.setDate(date);

                exchangeRateRepository.save(exchangeRate);

                ExchangeRate reverseRate = new ExchangeRate();
                reverseRate.setBaseCurrency(targetCurrency);
                reverseRate.setTargetCurrency(baseCurrency);
                reverseRate.setRate(BigDecimal.ONE.divide(rate, 6, RoundingMode.HALF_UP));
                reverseRate.setDate(date);

                exchangeRateRepository.save(reverseRate);
            });
        });
    }

    private ExchangeRateDto mapToExchangeRateDto(ExchangeRate rate) {
        return new ExchangeRateDto(
                rate.getTargetCurrency().getCode(),
                rate.getRate(),
                rate.getDate()
        );
    }
}
