package com.task.rate.controller;

import com.task.rate.service.CurrencyRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/currency")
public class CurrencyRateController {

    private final CurrencyRateService currencyRateService;

    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = "array",
                            implementation = String.class)
            ))
    })
    @Operation(
            summary = "Get all currencies",
            description = "Get a list of currencies used in the project"
    )
    @GetMapping
    public ResponseEntity<List<String>> fetchAllCurrencies() {
        return ResponseEntity.ok(currencyRateService.getAllCurrencies());
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = "object"
                    )
            ))
    })
    @Operation(
            summary = "Get exchange rates",
            description = "Get exchange rates for a currency"
    )
    @GetMapping("/exchange/rate/{currency}")
    public ResponseEntity<Map<String, Double>> fetchExchangeRatesForCurrency(@PathVariable String currency) {
        return ResponseEntity.ok(currencyRateService.getExchangeRatesForCurrency(currency));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Currency successfully added",
                    content = @Content(
                            mediaType = "application/json"
                    )
            )
    })
    @Operation(summary = "Add new currency",
            description = "Add new currency for getting exchange rates. A successful request returns a '201 Created' status."
    )
    @PostMapping("/{currency}")
    public ResponseEntity<Void> addCurrency(@PathVariable String currency) {
        currencyRateService.addCurrency(currency);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
