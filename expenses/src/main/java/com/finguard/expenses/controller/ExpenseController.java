package com.finguard.expenses.controller;

import com.finguard.expenses.dto.ExpenseDTO;
import com.finguard.expenses.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @PostMapping("/api/expense/create")
    public ResponseEntity<ExpenseDTO> createExpense(@RequestBody ExpenseDTO request) {
        ExpenseDTO response = service.createExpense(request);
        return ResponseEntity.ok(response);
    }

}
