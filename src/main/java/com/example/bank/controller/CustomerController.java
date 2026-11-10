package com.example.bank.controller;

import com.example.bank.dto.CustomerRequest;
import com.example.bank.dto.CustomerResponse;
import com.example.bank.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Customer registration and lookup APIs")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @Operation(summary = "Create customer", description = "Registers a new bank customer.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer created",
                    content = @Content(schema = @Schema(implementation = CustomerResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid customer request", content = @Content)
    })
    public CustomerResponse create(@Valid @RequestBody CustomerRequest request) {
        return customerService.create(request);
    }

    @GetMapping
    @Operation(summary = "List customers", description = "Returns all registered customers.")
    @ApiResponse(
            responseCode = "200",
            description = "Customers found",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CustomerResponse.class)))
    )
    public List<CustomerResponse> findAll() {
        return customerService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Returns a single customer by database ID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer found",
                    content = @Content(schema = @Schema(implementation = CustomerResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
    })
    public CustomerResponse findById(
            @Parameter(description = "Customer ID", example = "1")
            @PathVariable Long id
    ) {
        return customerService.findById(id);
    }
}
