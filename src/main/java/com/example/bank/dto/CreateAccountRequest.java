package com.example.bank.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotNull(message = "Customer id is required")
        @Positive(message = "Customer id must be positive")
        Long customerId,

        @NotNull(message = "Opening balance is required")
        @Positive(message = "Opening balance must be greater than zero")
        BigDecimal openingBalance
) {
}
