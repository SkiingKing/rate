package com.task.rate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalStorageTest {

    private LocalStorage localStorage;

    @BeforeEach
    void setUp() {
        localStorage = new LocalStorage();
    }

    @Test
    void testAddCurrencyRate() {
        String currency = "USD";
        Map<String, Double> rates = Map.of("EUR", 0.85, "GBP", 0.75);

        localStorage.addCurrencyRate(currency, rates);

        assertTrue(localStorage.getAllCurrencies().contains(currency));
        assertEquals(rates, localStorage.getAllRatesForCurrency(currency));
    }

    @Test
    void testGetAllCurrencies() {
        localStorage.addCurrencyRate("USD", Map.of("EUR", 0.85));
        localStorage.addCurrencyRate("EUR", Map.of("USD", 1.18));

        List<String> currencies = localStorage.getAllCurrencies();

        assertEquals(2, currencies.size());
        assertTrue(currencies.contains("USD"));
        assertTrue(currencies.contains("EUR"));
    }

    @Test
    void testGetAllRatesForCurrency() {
        String currency = "USD";
        Map<String, Double> expectedRates = Map.of("EUR", 0.85, "GBP", 0.75);
        localStorage.addCurrencyRate(currency, expectedRates);

        Map<String, Double> fetchedRates = localStorage.getAllRatesForCurrency(currency);

        assertNotNull(fetchedRates);
        assertEquals(expectedRates, fetchedRates);
    }

    @Test
    void testGetAllRatesForCurrencyWithoutRates() {
        String currency = "JPY";

        Map<String, Double> fetchedRates = localStorage.getAllRatesForCurrency(currency);

        assertNull(fetchedRates);
    }

    @Test
    void testAddCurrencyRateAndOverwriteExistingRates() {
        String currency = "USD";
        Map<String, Double> initialRates = Map.of("EUR", 0.85);
        Map<String, Double> newRates = Map.of("GBP", 0.75);

        localStorage.addCurrencyRate(currency, initialRates);

        localStorage.addCurrencyRate(currency, newRates);

        assertEquals(newRates, localStorage.getAllRatesForCurrency(currency));
        assertFalse(localStorage.getAllRatesForCurrency(currency).containsKey("EUR"));
    }
}