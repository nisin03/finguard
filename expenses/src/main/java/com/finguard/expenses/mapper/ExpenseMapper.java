package com.finguard.expenses.mapper;

import com.finguard.expenses.dto.ExpenseDTO;
import com.finguard.expenses.entity.Expense;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExpenseMapper {

    public ExpenseDTO toDto(Expense expense) {
        if (expense == null) {
            return null;
        }

        return ExpenseDTO.builder()
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .date(expense.getDate())
                .category(expense.getCategory())
                .createdAt(expense.getCreatedAt())
                .build();
    }

    public Expense toEntity(ExpenseDTO expenseDTO) {
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

    public List<ExpenseDTO> toDtoList(List<Expense> expenses) {
        if (expenses == null) {
            return null;
        }

        return expenses.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Expense> toEntityList(List<ExpenseDTO> expenseDTOs) {
        if (expenseDTOs == null) {
            return null;
        }

        return expenseDTOs.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}