package com.basnukaev.exchange.rate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateDto {
    private String targetCurrencyCode;
    private BigDecimal rate;
    private LocalDate date;
}
