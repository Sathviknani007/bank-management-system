package com.example.bank.dto;

import com.example.bank.model.AccountStatus;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String accountNumber,
        BigDecimal balance,
        AccountStatus status,
        Long customerId,
        String customerName
) {
}
