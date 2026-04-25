package com.finguard.expenses.service;

import com.finguard.expenses.dto.ExpenseDTO;
import com.finguard.expenses.entity.Expense;
import com.finguard.expenses.mapper.ExpenseMapper;
import com.finguard.expenses.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper mapper;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper mapper) {
        this.expenseRepository = expenseRepository;
        this.mapper = mapper;
    }

    public ExpenseDTO createExpense(ExpenseDTO request) {
        Expense expense = mapper.toEntity(request);

        Expense saved = expenseRepository.save(expense);
        return mapper.toDto(saved);
    }
}
