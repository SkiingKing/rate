package com.task.rate.initializer;

import com.task.rate.domain.CurrencyRate;
import com.task.rate.model.CurrencyHistory;
import com.task.rate.repository.CurrencyHistoryRepository;
import com.task.rate.storage.LocalStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Log4j2
@RequiredArgsConstructor
@Component
public class LocalStorageInitializer {

    private static final String URL_PATTERN = "%s/live?access_key=%s&source=%s";
    private static final int API_CALL_DELAY = 1000;

    private final LocalStorage localStorage;
    private final RestTemplate restTemplate;
    private final CurrencyHistoryRepository currencyHistoryRepository;

    @Value("${currency.api.url}")
    private String apiUrl;

    @Value("${currency.api.key}")
    private String apiKey;


    public void fetchExchangeRates() throws InterruptedException {
        for (String currency : localStorage.getAllCurrencies()) {
            processCurrency(currency);
            Thread.sleep(API_CALL_DELAY);
        }
    }

    public void fetchExchangeRatesForCurrency(String currency) {
        processCurrency(currency);
    }

    private void processCurrency(String currency) {
        String url = buildUrl(currency);
        try {
            CurrencyRate response = restTemplate.getForObject(url, CurrencyRate.class);
            if (isValidResponse(response)) {
                handleSuccessfulResponse(currency, response);
            }
        } catch (Exception e) {
            log.error("Error processing currency {}: {}", currency, e.getMessage(), e);
        }
    }

    private String buildUrl(String currency) {
        return String.format(URL_PATTERN, apiUrl, apiKey, currency);
    }

    private boolean isValidResponse(CurrencyRate response) {
        return response != null &&
                StringUtils.isNotBlank(response.getSource()) &&
                MapUtils.isNotEmpty(response.getQuotes());
    }

    private void handleSuccessfulResponse(String currency, CurrencyRate response) {
        log.info("Exchange rates successfully fetched for currency: {}", currency);
        localStorage.addCurrencyRate(response.getSource(), response.getQuotes());

        CurrencyHistory currencyHistory = CurrencyHistory.builder()
                .currency(currency)
                .lastUpdatedTimestamp(System.currentTimeMillis())
                .build();
        currencyHistoryRepository.save(currencyHistory);
    }
}
