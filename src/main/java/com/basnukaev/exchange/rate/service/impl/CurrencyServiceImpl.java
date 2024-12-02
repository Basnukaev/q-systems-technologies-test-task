package com.basnukaev.exchange.rate.service.impl;

import com.basnukaev.exchange.rate.domain.Currency;
import com.basnukaev.exchange.rate.dto.CurrencyDto;
import com.basnukaev.exchange.rate.repository.CurrencyRepository;
import com.basnukaev.exchange.rate.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Currency> findAll() {
        return currencyRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CurrencyDto> findAllDto() {
        return currencyRepository.findAll().stream()
                .map(this::mapToCurrencyDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Currency> findByCode(String code) {
        return currencyRepository.findByCode(code);
    }

    private CurrencyDto mapToCurrencyDto(Currency currency) {
        return new CurrencyDto(currency.getCode(), currency.getName());
    }
}
