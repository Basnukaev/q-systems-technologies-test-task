package com.basnukaev.exchange.rate.service.impl;

import com.basnukaev.exchange.rate.domain.Currency;
import com.basnukaev.exchange.rate.domain.ExchangeRate;
import com.basnukaev.exchange.rate.dto.ExchangeRateDto;
import com.basnukaev.exchange.rate.dto.ExchangeRateResponse;
import com.basnukaev.exchange.rate.repository.ExchangeRateRepository;
import com.basnukaev.exchange.rate.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExchangeRateServiceImplTest {

    @Mock
    private ExchangeRateApiClient exchangeRateApiClient;

    @Mock
    private CrossRateCalculator crossRateCalculator;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private ExchangeRateServiceImpl exchangeRateServiceImpl;

    @BeforeEach
    @SuppressWarnings("resource")
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetForCurrency() {
        String currencyCode = "USD";
        Currency currency = new Currency();
        currency.setCode(currencyCode);
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrency(currency);
        exchangeRate.setTargetCurrency(currency);
        exchangeRate.setRate(BigDecimal.ONE);
        exchangeRate.setDate(LocalDate.now());

        when(currencyService.findByCode(currencyCode)).thenReturn(Optional.of(currency));
        when(exchangeRateRepository.findLatestForCurrency(currency, LocalDate.now())).thenReturn(List.of(exchangeRate));

        List<ExchangeRateDto> result = exchangeRateServiceImpl.getForCurrency(currencyCode);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(currencyCode, result.get(0).getTargetCurrencyCode());
    }

    @Test
    void testUpsertExchangeRatesForDate() {
        // given
        LocalDate date = LocalDate.now();
        Currency baseCurrency = new Currency();
        baseCurrency.setCode("EUR");
        baseCurrency.setId(1L);

        Currency usdCurrency = new Currency();
        usdCurrency.setCode("USD");
        usdCurrency.setId(2L);

        ExchangeRateResponse response = new ExchangeRateResponse();
        response.setSuccess(true);
        response.setBase("EUR");
        response.setRates(Map.of("USD", BigDecimal.ONE));
        response.setDate(date);

        when(currencyService.findAll()).thenReturn(List.of(baseCurrency, usdCurrency));
        when(currencyService.findByCode("EUR")).thenReturn(Optional.of(baseCurrency));
        when(currencyService.findByCode("USD")).thenReturn(Optional.of(usdCurrency));
        when(exchangeRateApiClient.getExchangeRates(date, List.of("USD"))).thenReturn(response);

        // when
        exchangeRateServiceImpl.upsertExchangeRatesForDate(date);

        // then
        verify(crossRateCalculator, times(1)).calculateAndSaveCrossRates(eq(baseCurrency), any(), eq(date));
    }
}
