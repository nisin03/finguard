package com.finguard.expenses.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.finguard.expenses.dto.ExpenseRequest;
import com.finguard.expenses.dto.ExpenseResponse;
import com.finguard.expenses.entity.Expense;
import com.finguard.expenses.exception.ResourceNotFoundException;
import com.finguard.expenses.mapper.ExpenseMapper;
import com.finguard.expenses.repository.ExpenseRepository;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper mapper;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper mapper) {
        this.expenseRepository = expenseRepository;
        this.mapper = mapper;
    }

    public ExpenseResponse createExpense(ExpenseRequest request) {
        Expense expense = mapper.toEntity(request);

        Expense saved = expenseRepository.save(expense);
        return mapper.toDto(saved);
    }

    public ExpenseResponse getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));
        return mapper.toDto(expense);
    }

    public List<ExpenseResponse> getAllExpenses() {
        List<Expense> expenseList = expenseRepository.findAll();
        return expenseList.stream().map(mapper::toDto).collect(Collectors.toList());
    }
}
