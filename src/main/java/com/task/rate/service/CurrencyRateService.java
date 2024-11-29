package com.task.rate.service;

import com.task.rate.initializer.LocalStorageInitializer;
import com.task.rate.storage.LocalStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@Service
public class CurrencyRateService {

    private final LocalStorage localStorage;
    private final LocalStorageInitializer localStorageInitializer;

    public List<String> getAllCurrencies() {
        log.info("Fetching all currencies...");
        return localStorage.getAllCurrencies();
    }

    public Map<String, Double> getExchangeRatesForCurrency(String currency) {
        log.info("Fetching exchange rate for currency {}", currency);
        return localStorage.getAllRatesForCurrency(currency);
    }

    public void addCurrency(String currency) {
        log.info("Add currency: " + currency);
        localStorage.addCurrencyRate(currency, Collections.emptyMap());
        localStorageInitializer.fetchExchangeRatesForCurrency(currency);
    }
}
