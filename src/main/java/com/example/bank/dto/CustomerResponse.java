package com.example.bank.dto;

import java.util.List;

public record CustomerResponse(
        Long id,
        String name,
        String email,
        String phone,
        List<String> accountNumbers
) {
}
