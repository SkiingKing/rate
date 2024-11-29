package com.task.rate.controller;

import com.task.rate.model.CurrencyHistory;
import com.task.rate.repository.CurrencyHistoryRepository;
import com.task.rate.storage.LocalStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CurrencyRateControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CurrencyHistoryRepository currencyHistoryRepository;

    @Autowired
    private LocalStorage localStorage;

    @AfterEach
    void cleanUp() {
        localStorage.clearCache();
    }

    @Test
    void testFetchAllCurrencies() throws Exception {
        localStorage.addCurrencyRate("USD", Collections.emptyMap());
        localStorage.addCurrencyRate("EUR", Collections.emptyMap());
        localStorage.addCurrencyRate("GBP", Collections.emptyMap());

        mockMvc.perform(get("/v1/currency"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[0]").value("EUR"))
                .andExpect(jsonPath("$[1]").value("GBP"))
                .andExpect(jsonPath("$[2]").value("USD"));
    }

    @Test
    void testFetchExchangeRatesForCurrency() throws Exception {
        Map<String, Double> exchangeRates = Map.of("USDEUR", 1.0);
        localStorage.addCurrencyRate("USD", exchangeRates);

        mockMvc.perform(get("/v1/currency/exchange/rate/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.USDEUR").value(1.0));
    }

    @Test
    void testAddCurrency() throws Exception {
        String currency = "JPY";

        mockMvc.perform(post("/v1/currency/" + currency))
                .andExpect(status().isCreated());

        Optional<CurrencyHistory> currencyHistory = currencyHistoryRepository.findById(currency);


        assertThat(currencyHistory.isPresent()).isTrue();
        assertThat(currencyHistory.get().getCurrency()).isEqualTo(currency);
        assertThat(currencyHistory.get().getCreatedAt()).isNotNull();
        assertThat(currencyHistory.get().getLastUpdatedTimestamp()).isNotNull();

        List<String> cachedCurrencies = localStorage.getAllCurrencies();
        assertThat(cachedCurrencies).contains(currency);
        assertThat(cachedCurrencies.size()).isEqualTo(1);
        assertThat(localStorage.getAllRatesForCurrency(currency)).isNotNull();
    }
}
