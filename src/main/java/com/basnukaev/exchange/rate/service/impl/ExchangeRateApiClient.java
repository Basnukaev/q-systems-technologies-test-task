package com.basnukaev.exchange.rate.service.impl;

import com.basnukaev.exchange.rate.dto.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExchangeRateApiClient {

    private final RestTemplate restTemplate;

    @Value("${exchange.api.key}")
    private String apiKey;

    private static final String API_URL = "https://api.exchangeratesapi.io/v1/%s?access_key=%s&base=EUR&symbols=%s";

    public ExchangeRateResponse getExchangeRates(LocalDate date, List<String> currencySymbols) {
        String symbols = String.join(",", currencySymbols);
        String formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String url = String.format(API_URL, formattedDate, apiKey, symbols);
        return restTemplate.getForObject(url, ExchangeRateResponse.class);
    }
}
