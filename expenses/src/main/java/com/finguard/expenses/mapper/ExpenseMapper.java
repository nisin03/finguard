package com.finguard.expenses.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.finguard.expenses.dto.ExpenseRequest;
import com.finguard.expenses.dto.ExpenseResponse;
import com.finguard.expenses.entity.Expense;

@Component
public class ExpenseMapper {

    public ExpenseResponse toDto(Expense expense) {
        if (expense == null) {
            return null;
        }

        return ExpenseResponse.builder()
                .id(expense.getId())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .date(expense.getDate())
                .category(expense.getCategory())
                .createdAt(expense.getCreatedAt())
                .build();
    }

    public Expense toEntity(ExpenseRequest expenseDTO) {
        if (expenseDTO == null) {
            return null;
        }

        return Expense.builder()
                .description(expenseDTO.getDescription())
                .amount(expenseDTO.getAmount())
                .date(expenseDTO.getDate())
                .category(expenseDTO.getCategory())
                .createdAt(expenseDTO.getCreatedAt())
                .build();
    }

    public List<ExpenseResponse> toDtoList(List<Expense> expenses) {
        if (expenses == null) {
            return null;
        }

        return expenses.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Expense> toEntityList(List<ExpenseRequest> expenseDTOs) {
        if (expenseDTOs == null) {
            return null;
        }

        return expenseDTOs.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}