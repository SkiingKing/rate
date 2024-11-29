package com.task.rate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Log4j2
@Component
public class LocalStorage {

    private final Map<String, Map<String, Double>> currencyRatesMap = new ConcurrentHashMap<>();


    public void addCurrencyRate(String currency, Map<String, Double> rates) {
        currencyRatesMap.put(currency, rates);
    }

    public List<String> getAllCurrencies() {
        return new ArrayList<>(currencyRatesMap.keySet());
    }

    public Map<String, Double> getAllRatesForCurrency(String currency) {
        return currencyRatesMap.get(currency);
    }

    public void clearCache() {
        currencyRatesMap.clear();
    }
}
