package com.finguard.expenses.controller;

import com.finguard.expenses.dto.ExpenseDTO;
import com.finguard.expenses.entity.Expense;
import com.finguard.expenses.repository.ExpenseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ExpenseControllerIntegrationTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        expenseRepository.deleteAll();
    }

    @Test
    void createExpense_shouldPersistExpenseAndReturnResponse() throws Exception {
        // Given
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 15, 10, 30);
        LocalDate date = LocalDate.of(2026, 1, 15);
        BigDecimal amount = new BigDecimal("100.50");
        String category = "Food";
        String description = "Lunch at restaurant";

        ExpenseDTO request = ExpenseDTO.builder()
                .description(description)
                .amount(amount)
                .date(date)
                .category(category)
                .createdAt(createdAt)
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/expense/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.amount").value(amount.doubleValue()))
                .andExpect(jsonPath("$.date").value(date.toString()))
                .andExpect(jsonPath("$.category").value(category))
                .andReturn();

        // Verify persistence
        assertThat(expenseRepository.count()).isEqualTo(1);
        Expense savedExpense = expenseRepository.findAll().get(0);
        assertThat(savedExpense.getDescription()).isEqualTo(description);
        assertThat(savedExpense.getAmount()).isEqualByComparingTo(amount);
        assertThat(savedExpense.getCategory()).isEqualTo(category);
    }

    @Test
    void createExpense_shouldSaveExpenseWithValidData() throws Exception {
        // Given
        ExpenseDTO request = ExpenseDTO.builder()
                .description("Transport")
                .amount(new BigDecimal("25.00"))
                .date(LocalDate.of(2024, 2, 20))
                .category("Travel")
                .createdAt(LocalDateTime.now())
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(post("/api/expense/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Transport"))
                .andExpect(jsonPath("$.category").value("Travel"));

        // Verify saved to database
        assertThat(expenseRepository.count()).isEqualTo(1);
    }

    @Test
    void createExpense_shouldHandleMultipleExpenses() throws Exception {
        // Given
        ExpenseDTO request1 = ExpenseDTO.builder()
                .description("Lunch")
                .amount(new BigDecimal("15.00"))
                .date(LocalDate.of(2024, 1, 15))
                .category("Food")
                .createdAt(LocalDateTime.now())
                .build();

        ExpenseDTO request2 = ExpenseDTO.builder()
                .description("Gas")
                .amount(new BigDecimal("50.00"))
                .date(LocalDate.of(2024, 1, 16))
                .category("Travel")
                .createdAt(LocalDateTime.now())
                .build();

        // When
        mockMvc.perform(post("/api/expense/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/expense/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());

        // Then
        assertThat(expenseRepository.count()).isEqualTo(2);
    }

    @Test
    void createExpense_shouldAssignIdToResponse() throws Exception {
        // Given
        ExpenseDTO request = ExpenseDTO.builder()
                .description("Test")
                .amount(BigDecimal.ONE)
                .date(LocalDate.now())
                .category("Test")
                .createdAt(LocalDateTime.now())
                .build();

        // When
        mockMvc.perform(post("/api/expense/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Then
        assertThat(expenseRepository.count()).isEqualTo(1);
        Expense saved = expenseRepository.findAll().get(0);
        assertThat(saved.getId()).isNotNull();
    }
}
