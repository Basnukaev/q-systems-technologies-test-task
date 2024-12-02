package com.basnukaev.exchange.rate.repository;

import com.basnukaev.exchange.rate.domain.Currency;
import com.basnukaev.exchange.rate.domain.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query("""
            SELECT e
            FROM ExchangeRate e
            WHERE e.baseCurrency = :currency
            AND e.date <= :date
            ORDER BY e.date DESC, e.targetCurrency.code
            """)
    List<ExchangeRate> findLatestForCurrency(
            @Param("currency") Currency currency,
            @Param("date") LocalDate date);

    @Query("SELECT COUNT(e) > 0 FROM ExchangeRate e WHERE e.date = :date")
    boolean existsByDate(LocalDate date);

    @Query("""
            SELECT e
            FROM ExchangeRate e
            WHERE e.baseCurrency = :baseCurrency
            AND e.targetCurrency = :targetCurrency
            AND e.date = :date
            ORDER BY e.date DESC
            LIMIT 1
            """)
    Optional<ExchangeRate> findOneByBaseCurrencyAndTargetCurrencyAndDate(
            Currency baseCurrency,
            Currency targetCurrency,
            LocalDate date
    );
}
