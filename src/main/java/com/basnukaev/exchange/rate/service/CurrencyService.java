package com.basnukaev.exchange.rate.service;

import com.basnukaev.exchange.rate.domain.Currency;
import com.basnukaev.exchange.rate.dto.CurrencyDto;

import java.util.List;
import java.util.Optional;

public interface CurrencyService {

    List<Currency> findAll();

    List<CurrencyDto> findAllDto();

    Optional<Currency> findByCode(String code);
}
