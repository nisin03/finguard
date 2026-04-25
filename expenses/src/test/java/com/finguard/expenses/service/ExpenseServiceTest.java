package com.finguard.expenses.service;

import com.finguard.expenses.dto.ExpenseDTO;
import com.finguard.expenses.entity.Expense;
import com.finguard.expenses.mapper.ExpenseMapper;
import com.finguard.expenses.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository repository;

    @Mock
    private ExpenseMapper mapper;

    @InjectMocks
    private ExpenseService service;

    @Test
    void createExpense_shouldMapDtoToEntityAndSave() {
        // Given
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 15, 10, 30);
        LocalDate date = LocalDate.of(2024, 1, 15);
        BigDecimal amount = new BigDecimal("100.50");
        String category = "Food";
        String description = "Lunch at restaurant";

        ExpenseDTO request = ExpenseDTO.builder()
                .createdAt(createdAt)
                .date(date)
                .amount(amount)
                .category(category)
                .description(description)
                .build();

        Expense expense = Expense.builder()
                .description(description)
                .amount(amount)
                .date(date)
                .category(category)
                .createdAt(createdAt)
                .build();

        Expense savedExpense = Expense.builder()
                .id(1L)
                .description(description)
                .amount(amount)
                .date(date)
                .category(category)
                .createdAt(createdAt)
                .build();

        ExpenseDTO responseDto = ExpenseDTO.builder()
                .description(description)
                .amount(amount)
                .date(date)
                .category(category)
                .createdAt(createdAt)
                .build();

        when(mapper.toEntity(request)).thenReturn(expense);
        when(repository.save(any(Expense.class))).thenReturn(savedExpense);
        when(mapper.toDto(savedExpense)).thenReturn(responseDto);

        // When
        ExpenseDTO result = service.createExpense(request);

        // Then
        verify(mapper).toEntity(request);
        verify(repository).save(expense);
        verify(mapper).toDto(savedExpense);

        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals(amount, result.getAmount());
        assertEquals(date, result.getDate());
        assertEquals(category, result.getCategory());
        assertEquals(createdAt, result.getCreatedAt());
    }

    @Test
    void createExpense_shouldReturnMappedExpenseDTO() {
        // Given
        ExpenseDTO request = ExpenseDTO.builder()
                .description("Test expense")
                .amount(BigDecimal.TEN)
                .date(LocalDate.now())
                .category("Test")
                .createdAt(LocalDateTime.now())
                .build();

        Expense mappedExpense = Expense.builder()
                .description("Test expense")
                .amount(BigDecimal.TEN)
                .date(LocalDate.now())
                .category("Test")
                .createdAt(LocalDateTime.now())
                .build();

        Expense savedExpense = Expense.builder()
                .id(2L)
                .description("Test expense")
                .amount(BigDecimal.TEN)
                .date(LocalDate.now())
                .category("Test")
                .createdAt(LocalDateTime.now())
                .build();

        ExpenseDTO expectedResponse = ExpenseDTO.builder()
                .description("Test expense")
                .amount(BigDecimal.TEN)
                .date(LocalDate.now())
                .category("Test")
                .createdAt(LocalDateTime.now())
                .build();

        when(mapper.toEntity(request)).thenReturn(mappedExpense);
        when(repository.save(any(Expense.class))).thenReturn(savedExpense);
        when(mapper.toDto(savedExpense)).thenReturn(expectedResponse);

        // When
        ExpenseDTO result = service.createExpense(request);

        // Then
        assertNotNull(result);
        assertEquals("Test expense", result.getDescription());
        assertEquals(BigDecimal.TEN, result.getAmount());
        assertEquals("Test", result.getCategory());
    }
}