package com.task.rate.schedule;

import com.task.rate.initializer.LocalStorageInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class ScheduledTasks {

    private final LocalStorageInitializer localStorageInitializer;

    @Scheduled(cron = "0 0 * * * *")
    public void fetchExchangeRatesHourly() {
        log.info("Fetching exchange rates...");
        try {
            localStorageInitializer.fetchExchangeRates();
        } catch (Exception e) {
            log.error("Error fetching exchange rates: {}", e.getMessage(), e);
        }
    }
}
