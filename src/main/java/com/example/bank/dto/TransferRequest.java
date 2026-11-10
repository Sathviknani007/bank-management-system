package com.example.bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(
        @NotBlank(message = "Source account number is required")
        @Pattern(regexp = "^[0-9]{11}$", message = "Source account number must contain exactly 11 digits")
        String fromAccountNumber,

        @NotBlank(message = "Destination account number is required")
        @Pattern(regexp = "^[0-9]{11}$", message = "Destination account number must contain exactly 11 digits")
        String toAccountNumber,

        @NotNull(message = "Transfer amount is required")
        @Positive(message = "Transfer amount must be greater than zero")
        BigDecimal amount
) {
}
