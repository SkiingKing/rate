package com.task.rate.service;

import com.task.rate.initializer.LocalStorageInitializer;
import com.task.rate.storage.LocalStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyRateServiceTest {

    @Mock
    private LocalStorage localStorage;

    @Mock
    private LocalStorageInitializer localStorageInitializer;

    @InjectMocks
    private CurrencyRateService currencyRateService;

    @Test
    void testGetAllCurrencies() {
        List<String> expectedCurrencies = List.of("USD", "EUR", "GBP");
        when(localStorage.getAllCurrencies()).thenReturn(expectedCurrencies);

        List<String> result = currencyRateService.getAllCurrencies();

        verify(localStorage, times(1)).getAllCurrencies();
        assertEquals(expectedCurrencies, result);
    }

    @Test
    void testGetExchangeRatesForCurrency() {
        String currency = "USD";
        Map<String, Double> expectedRates = Map.of("USDEUR", 0.85, "USDGBP", 0.75);
        when(localStorage.getAllRatesForCurrency(currency)).thenReturn(expectedRates);

        Map<String, Double> result = currencyRateService.getExchangeRatesForCurrency(currency);

        verify(localStorage, times(1)).getAllRatesForCurrency(currency);
        assertEquals(expectedRates, result);
    }

    @Test
    void testAddCurrency() {
        String newCurrency = "USD";

        currencyRateService.addCurrency(newCurrency);

        verify(localStorage, times(1)).addCurrencyRate(newCurrency, Collections.emptyMap());
        verify(localStorageInitializer, times(1)).fetchExchangeRatesForCurrency(newCurrency);
    }
}
