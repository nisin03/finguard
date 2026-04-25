package com.finguard.expenses.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.finguard.expenses.dto.ExpenseRequest;
import com.finguard.expenses.dto.ExpenseResponse;
import com.finguard.expenses.entity.Expense;
import com.finguard.expenses.exception.ResourceNotFoundException;
import com.finguard.expenses.mapper.ExpenseMapper;
import com.finguard.expenses.repository.ExpenseRepository;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    public static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1, 15, 10, 30);
    public static final LocalDate DATE = LocalDate.of(2026, 1, 15);
    public static final long EXPENSE_ID = 1L;
    public static final BigDecimal AMOUNT = new BigDecimal("100.50");
    public static final String DESCRIPTION = "Lunch at restaurant";
    public static final String CATEGORY = "Food";

    @Mock
    private ExpenseRepository repository;

    @Mock
    private ExpenseMapper mapper;

    @InjectMocks
    private ExpenseService service;

    @Test
    void createExpense_shouldMapDtoToEntityAndSave() {
        // Given
        ExpenseRequest request = createDefaultExpenseRequest();
        Expense mappedExpense = createDefaultExpense();

        Expense savedExpense = createDefaultExpense();
        ExpenseResponse expectedResponse = createDefaultExpenseResponse();

        when(mapper.toEntity(request)).thenReturn(mappedExpense);
        when(repository.save(any(Expense.class))).thenReturn(savedExpense);
        when(mapper.toDto(savedExpense)).thenReturn(expectedResponse);

        // When
        ExpenseResponse result = service.createExpense(request);

        // Then
        verify(mapper).toEntity(request);
        verify(repository).save(mappedExpense);
        verify(mapper).toDto(savedExpense);

        assertNotNull(result);
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(AMOUNT, result.getAmount());
        assertEquals(DATE, result.getDate());
        assertEquals(CATEGORY, result.getCategory());
        assertEquals(CREATED_AT, result.getCreatedAt());
    }

    @Test
    void createExpense_shouldReturnMappedExpenseDTO() {
        // Given
        ExpenseRequest request = createDefaultExpenseRequest();
        Expense mappedExpense = createDefaultExpense();

        Expense savedExpense = createDefaultExpense();
        ExpenseResponse expectedResponse = createDefaultExpenseResponse();

        when(mapper.toEntity(request)).thenReturn(mappedExpense);
        when(repository.save(any(Expense.class))).thenReturn(savedExpense);
        when(mapper.toDto(savedExpense)).thenReturn(expectedResponse);

        // When
        ExpenseResponse result = service.createExpense(request);

        // Then
        assertNotNull(result);
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(AMOUNT, result.getAmount());
        assertEquals(CATEGORY, result.getCategory());
    }

    @Test
    void getExpenseById_shouldReturnExpenseDTOWhenFound() {
        // Given
        Expense expense = createDefaultExpense();
        ExpenseResponse expectedDto = createDefaultExpenseResponse();

        when(repository.findById(EXPENSE_ID)).thenReturn(Optional.of(expense));
        when(mapper.toDto(expense)).thenReturn(expectedDto);

        // When
        ExpenseResponse result = service.getExpenseById(EXPENSE_ID);

        // Then
        assertNotNull(result);
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(AMOUNT, result.getAmount());
        assertEquals(CATEGORY, result.getCategory());
        verify(repository).findById(EXPENSE_ID);
        verify(mapper).toDto(expense);
    }

    @Test
    void getExpenseById_shouldThrowResourceNotFoundExceptionWhenNotFound() {
        // Given
        Long expenseId = 999L;

        when(repository.findById(expenseId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.getExpenseById(expenseId));

        assertEquals("Expense not found with id: '999'", exception.getMessage());
        verify(repository).findById(expenseId);
    }

    @Test
    void getAllExpenses_shouldReturnExpenseList() {
        // Given
        List<Expense> expenseList = Collections.singletonList(createDefaultExpense());

        when(repository.findAll()).thenReturn(expenseList);
        when(mapper.toDto(any())).thenReturn(createDefaultExpenseResponse());

        // When
        List<ExpenseResponse> resultList = service.getAllExpenses();

        // Then
        assertNotNull(resultList);
        assertEquals(DESCRIPTION, resultList.get(0).getDescription());
        assertEquals(AMOUNT, resultList.get(0).getAmount());
        assertEquals(CATEGORY, resultList.get(0).getCategory());
        verify(repository).findAll();
        verify(mapper).toDto(any());
    }

    private Expense createDefaultExpense() {
        return Expense.builder()
                .id(EXPENSE_ID)
                .description(DESCRIPTION)
                .amount(AMOUNT)
                .date(DATE)
                .category(CATEGORY)
                .createdAt(CREATED_AT)
                .build();
    }

    private ExpenseRequest createDefaultExpenseRequest() {
        return ExpenseRequest.builder()
                .description(DESCRIPTION)
                .amount(AMOUNT)
                .date(DATE)
                .category(CATEGORY)
                .createdAt(CREATED_AT)
                .build();
    }

    private ExpenseResponse createDefaultExpenseResponse() {
        return ExpenseResponse.builder()
                .id(EXPENSE_ID)
                .description(DESCRIPTION)
                .amount(AMOUNT)
                .date(DATE)
                .category(CATEGORY)
                .createdAt(CREATED_AT)
                .build();
    }
}