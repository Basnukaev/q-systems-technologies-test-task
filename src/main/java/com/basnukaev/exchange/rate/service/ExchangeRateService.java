package com.basnukaev.exchange.rate.service;

import com.basnukaev.exchange.rate.domain.ExchangeRate;
import com.basnukaev.exchange.rate.dto.ExchangeRateDto;

import java.time.LocalDate;
import java.util.List;

public interface ExchangeRateService {

    List<ExchangeRateDto> getForCurrency(String currencyCode);

    void save(ExchangeRate exchangeRate);

    boolean hasForDate(LocalDate date);

    void upsertExchangeRatesForDate(LocalDate date);
}
