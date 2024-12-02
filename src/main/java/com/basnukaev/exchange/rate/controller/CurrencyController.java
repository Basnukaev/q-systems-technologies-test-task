package com.basnukaev.exchange.rate.controller;

import com.basnukaev.exchange.rate.service.CurrencyService;
import com.basnukaev.exchange.rate.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class CurrencyController {

    private final ExchangeRateService exchangeRateService;
    private final CurrencyService currencyService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("currencies", currencyService.findAllDto());
        return "index";
    }

    @GetMapping("/rates/{currencyCode}")
    public String getRates(@PathVariable String currencyCode, Model model) {
        model.addAttribute("currencies", currencyService.findAllDto());
        model.addAttribute("selectedCurrency", currencyCode);
        model.addAttribute("rates", exchangeRateService.getForCurrency(currencyCode));
        return "index";
    }
}
