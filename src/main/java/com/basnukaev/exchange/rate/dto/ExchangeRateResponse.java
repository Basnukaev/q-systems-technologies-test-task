package com.basnukaev.exchange.rate.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
public class ExchangeRateResponse {
    private boolean success;
    private boolean historical;
    private long timestamp;
    private String base;
    private LocalDate date;
    private Map<String, BigDecimal> rates;
}