package com.example.bank.controller;

import com.example.bank.dto.AccountResponse;
import com.example.bank.dto.CreateAccountRequest;
import com.example.bank.dto.MoneyRequest;
import com.example.bank.dto.TransactionResponse;
import com.example.bank.dto.TransferRequest;
import com.example.bank.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
        return accountService.create(request);
    }

    @GetMapping
    public List<AccountResponse> findAll() {
        return accountService.findAll();
    }

    @GetMapping("/{accountNumber}")
    public AccountResponse findByNumber(@PathVariable String accountNumber) {
        return accountService.findByNumber(accountNumber);
    }

    @PostMapping("/{accountNumber}/deposit")
    public AccountResponse deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody MoneyRequest request
    ) {
        return accountService.deposit(accountNumber, request);
    }

    @PostMapping("/{accountNumber}/withdraw")
    public AccountResponse withdraw(
            @PathVariable String accountNumber,
            @Valid @RequestBody MoneyRequest request
    ) {
        return accountService.withdraw(accountNumber, request);
    }

    @PostMapping("/transfer")
    public List<AccountResponse> transfer(@Valid @RequestBody TransferRequest request) {
        return accountService.transfer(request);
    }

    @GetMapping("/{accountNumber}/transactions")
    public List<TransactionResponse> transactions(@PathVariable String accountNumber) {
        return accountService.transactions(accountNumber);
    }

    @PatchMapping("/{accountNumber}/close")
    public AccountResponse close(@PathVariable String accountNumber) {
        return accountService.close(accountNumber);
    }
}
