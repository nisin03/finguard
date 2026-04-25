package com.finguard.expenses.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.finguard.expenses.dto.ExpenseRequest;
import com.finguard.expenses.dto.ExpenseResponse;
import com.finguard.expenses.service.ExpenseService;

@RestController
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @PostMapping("/api/expenses/create")
    public ResponseEntity<ExpenseResponse> createExpense(@RequestBody ExpenseRequest request) {
        ExpenseResponse response = service.createExpense(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/expenses")
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses() {
        List<ExpenseResponse> responseList = service.getAllExpenses();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/api/expenses/{id}")
    public ResponseEntity<ExpenseResponse> getExpenseById(@PathVariable Long id) {
        ExpenseResponse response = service.getExpenseById(id);
        return ResponseEntity.ok(response);
    }

}
