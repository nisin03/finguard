package com.finguard.expenses.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.finguard.expenses.dto.ExpenseRequest;
import com.finguard.expenses.dto.ExpenseResponse;
import com.finguard.expenses.entity.Expense;

@ExtendWith(MockitoExtension.class)
class ExpenseMapperTest {

    @InjectMocks
    private ExpenseMapper mapper;

    @Test
    void toDto_shouldMapExpenseToExpenseDTO() {
        // Given
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 15, 10, 30);
        LocalDate date = LocalDate.of(2024, 1, 15);
        BigDecimal amount = new BigDecimal("100.50");
        String category = "Food";
        String description = "Lunch at restaurant";

        Expense expense = Expense.builder()
                .id(1L)
                .description(description)
                .amount(amount)
                .date(date)
                .category(category)
                .createdAt(createdAt)
                .build();

        // When
        ExpenseResponse result = mapper.toDto(expense);

        // Then
        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals(amount, result.getAmount());
        assertEquals(date, result.getDate());
        assertEquals(category, result.getCategory());
        assertEquals(createdAt, result.getCreatedAt());
    }

    @Test
    void toDto_shouldReturnNullWhenExpenseIsNull() {
        // When
        ExpenseResponse result = mapper.toDto(null);

        // Then
        assertNull(result);
    }

    @Test
    void toEntity_shouldMapExpenseDTOToExpense() {
        // Given
        LocalDate date = LocalDate.of(2024, 1, 15);
        BigDecimal amount = new BigDecimal("100.50");
        String category = "Food";
        String description = "Lunch at restaurant";

        ExpenseRequest expenseDTO = ExpenseRequest.builder()
                .description(description)
                .amount(amount)
                .date(date)
                .category(category)
                .build();

        // When
        Expense result = mapper.toEntity(expenseDTO);

        // Then
        assertNotNull(result);
        assertNull(result.getId()); // ID should be null as it's not in DTO
        assertEquals(description, result.getDescription());
        assertEquals(amount, result.getAmount());
        assertEquals(date, result.getDate());
        assertEquals(category, result.getCategory());
    }

    @Test
    void toEntity_shouldReturnNullWhenExpenseDTOIsNull() {
        // When
        Expense result = mapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    void toDtoList_shouldMapListOfExpensesToListOfExpenseDTOs() {
        // Given
        Expense expense1 = Expense.builder()
                .id(1L)
                .description("Expense 1")
                .amount(BigDecimal.ONE)
                .date(LocalDate.now())
                .category("Category 1")
                .createdAt(LocalDateTime.now())
                .build();

        Expense expense2 = Expense.builder()
                .id(2L)
                .description("Expense 2")
                .amount(BigDecimal.TEN)
                .date(LocalDate.now().minusDays(1))
                .category("Category 2")
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        List<Expense> expenses = Arrays.asList(expense1, expense2);

        // When
        List<ExpenseResponse> result = mapper.toDtoList(expenses);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Expense 1", result.get(0).getDescription());
        assertEquals("Expense 2", result.get(1).getDescription());
    }

    @Test
    void toDtoList_shouldReturnNullWhenExpensesListIsNull() {
        // When
        List<ExpenseResponse> result = mapper.toDtoList(null);

        // Then
        assertNull(result);
    }

    @Test
    void toDtoList_shouldReturnEmptyListWhenExpensesListIsEmpty() {
        // When
        List<ExpenseResponse> result = mapper.toDtoList(Collections.emptyList());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toEntityList_shouldMapListOfExpenseDTOsToListOfExpenses() {
        // Given
        ExpenseRequest expenseDTO1 = ExpenseRequest.builder()
                .description("Expense 1")
                .amount(BigDecimal.ONE)
                .date(LocalDate.now())
                .category("Category 1")
                .build();

        ExpenseRequest expenseDTO2 = ExpenseRequest.builder()
                .description("Expense 2")
                .amount(BigDecimal.TEN)
                .date(LocalDate.now().minusDays(1))
                .category("Category 2")
                .build();

        List<ExpenseRequest> expenseDTOs = Arrays.asList(expenseDTO1, expenseDTO2);

        // When
        List<Expense> result = mapper.toEntityList(expenseDTOs);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Expense 1", result.get(0).getDescription());
        assertEquals("Expense 2", result.get(1).getDescription());
    }

    @Test
    void toEntityList_shouldReturnNullWhenExpenseDTOsListIsNull() {
        // When
        List<Expense> result = mapper.toEntityList(null);

        // Then
        assertNull(result);
    }

    @Test
    void toEntityList_shouldReturnEmptyListWhenExpenseDTOsListIsEmpty() {
        // When
        List<Expense> result = mapper.toEntityList(Collections.emptyList());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}