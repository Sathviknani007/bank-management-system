package com.example.bank.service;

import com.example.bank.dto.AccountResponse;
import com.example.bank.dto.CreateAccountRequest;
import com.example.bank.dto.MoneyRequest;
import com.example.bank.dto.TransactionResponse;
import com.example.bank.dto.TransferRequest;
import com.example.bank.exception.BusinessException;
import com.example.bank.exception.ResourceNotFoundException;
import com.example.bank.model.Account;
import com.example.bank.model.AccountStatus;
import com.example.bank.model.Customer;
import com.example.bank.model.Transaction;
import com.example.bank.model.TransactionType;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerService customerService;
    private final SecureRandom secureRandom = new SecureRandom();

    public AccountService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            CustomerService customerService
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.customerService = customerService;
    }

    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        Customer customer = customerService.getCustomer(request.customerId());
        Account account = new Account();
        account.setCustomer(customer);
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(request.openingBalance());

        Account saved = accountRepository.save(account);
        if (request.openingBalance().compareTo(BigDecimal.ZERO) > 0) {
            saveTransaction(saved, TransactionType.DEPOSIT, request.openingBalance(), "Opening balance");
        }
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findAll() {
        return accountRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse findByNumber(String accountNumber) {
        return toResponse(getActiveAccount(accountNumber));
    }

    @Transactional
    public AccountResponse deposit(String accountNumber, MoneyRequest request) {
        Account account = getActiveAccount(accountNumber);
        account.setBalance(account.getBalance().add(request.amount()));
        saveTransaction(account, TransactionType.DEPOSIT, request.amount(), "Cash deposit");
        return toResponse(account);
    }

    @Transactional
    public AccountResponse withdraw(String accountNumber, MoneyRequest request) {
        Account account = getActiveAccount(accountNumber);
        ensureSufficientBalance(account, request.amount());
        account.setBalance(account.getBalance().subtract(request.amount()));
        saveTransaction(account, TransactionType.WITHDRAW, request.amount(), "Cash withdraw");
        return toResponse(account);
    }

    @Transactional
    public List<AccountResponse> transfer(TransferRequest request) {
        if (request.fromAccountNumber().equals(request.toAccountNumber())) {
            throw new BusinessException("Source and destination accounts must be different");
        }

        Account from = getActiveAccount(request.fromAccountNumber());
        Account to = getActiveAccount(request.toAccountNumber());
        ensureSufficientBalance(from, request.amount());

        from.setBalance(from.getBalance().subtract(request.amount()));
        to.setBalance(to.getBalance().add(request.amount()));

        saveTransaction(from, TransactionType.TRANSFER_OUT, request.amount(), "Transfer to " + to.getAccountNumber());
        saveTransaction(to, TransactionType.TRANSFER_IN, request.amount(), "Transfer from " + from.getAccountNumber());

        return List.of(toResponse(from), toResponse(to));
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> transactions(String accountNumber) {
        getActiveAccount(accountNumber);
        return transactionRepository.findByAccountAccountNumberOrderByCreatedAtDesc(accountNumber).stream()
                .map(this::toTransactionResponse)
                .toList();
    }

    @Transactional
    public AccountResponse close(String accountNumber) {
        Account account = getActiveAccount(accountNumber);
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessException("Only zero-balance accounts can be closed");
        }
        account.setStatus(AccountStatus.CLOSED);
        return toResponse(account);
    }

    private Account getActiveAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException("Account is not active: " + accountNumber);
        }
        return account;
    }

    private void ensureSufficientBalance(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Insufficient balance");
        }
    }

    private void saveTransaction(Account account, TransactionType type, BigDecimal amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(account.getBalance());
        transaction.setDescription(description);
        transactionRepository.save(transaction);
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = "10" + (100000000L + secureRandom.nextInt(900000000));
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    private AccountResponse toResponse(Account account) {
        Customer customer = account.getCustomer();
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getStatus(),
                customer.getId(),
                customer.getName()
        );
    }

    private TransactionResponse toTransactionResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getBalanceAfter(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }
}
