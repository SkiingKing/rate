package com.task.rate.initializer;


import com.task.rate.domain.CurrencyRate;
import com.task.rate.model.CurrencyHistory;
import com.task.rate.repository.CurrencyHistoryRepository;
import com.task.rate.storage.LocalStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LocalStorageInitializerTest {

    @Mock
    private LocalStorage localStorage;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CurrencyHistoryRepository currencyHistoryRepository;

    @InjectMocks
    private LocalStorageInitializer localStorageInitializer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(localStorageInitializer, "apiUrl", "https://api.example.com");
        ReflectionTestUtils.setField(localStorageInitializer, "apiKey", "testApiKey");
    }

    @Test
    void testFetchExchangeRates() throws InterruptedException {
        when(localStorage.getAllCurrencies()).thenReturn(List.of("USD", "EUR"));
        when(restTemplate.getForObject(anyString(), any()))
                .thenReturn(new CurrencyRate("USD", Map.of("USDEUR", 0.85)))
                .thenReturn(new CurrencyRate());

        localStorageInitializer.fetchExchangeRates();

        verify(localStorage, times(1)).getAllCurrencies();
        verify(restTemplate, times(2)).getForObject(anyString(), any());
        verify(localStorage, times(1)).addCurrencyRate(any(), any());
        verify(currencyHistoryRepository, times(1)).save(any(CurrencyHistory.class));
    }

    @Test
    void testFetchExchangeRatesUnsuccessfully() throws InterruptedException {
        String currency = "USD";
        when(localStorage.getAllCurrencies()).thenReturn(List.of(currency));
        when(restTemplate.getForObject(anyString(), any()))
                .thenReturn(new CurrencyRate(currency, Collections.emptyMap()));

        localStorageInitializer.fetchExchangeRates();

        verify(localStorage, times(1)).getAllCurrencies();
        verify(restTemplate, times(1)).getForObject(anyString(), any());
        verify(localStorage, never()).addCurrencyRate(any(), any());
        verify(currencyHistoryRepository, never()).save(any(CurrencyHistory.class));
    }

    @Test
    void testFetchExchangeRatesForCurrency() {
        String currency = "EUR";
        when(restTemplate.getForObject(anyString(), any()))
                .thenReturn(new CurrencyRate(currency, Map.of("EURUSD", 1.18)));

        localStorageInitializer.fetchExchangeRatesForCurrency(currency);

        verify(restTemplate, times(1)).getForObject(anyString(), any());
        verify(currencyHistoryRepository, times(1)).save(any(CurrencyHistory.class));
        verify(localStorage, times(1)).addCurrencyRate(any(), any());
    }

    @Test
    void testFetchExchangeRatesForCurrencyUnsuccessfully() {
        String currency = "EUR";
        when(restTemplate.getForObject(anyString(), any()))
                .thenReturn(new CurrencyRate(currency,  Collections.emptyMap()));

        localStorageInitializer.fetchExchangeRatesForCurrency(currency);

        verify(restTemplate, times(1)).getForObject(anyString(), any());
        verify(currencyHistoryRepository, never()).save(any(CurrencyHistory.class));
        verify(localStorage, never()).addCurrencyRate(any(), any());
    }
}
