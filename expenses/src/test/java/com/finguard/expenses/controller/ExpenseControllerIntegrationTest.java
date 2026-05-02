package com.finguard.expenses.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.finguard.expenses.dto.ExpenseRequest;
import com.finguard.expenses.dto.ExpenseResponse;
import com.finguard.expenses.repository.ExpenseRepository;
import com.finguard.expenses.support.PostgresIntegrationTest;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ExpenseControllerIntegrationTest extends PostgresIntegrationTest {

    public static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1, 15, 10, 30);
    public static final LocalDate DATE = LocalDate.of(2026, 1, 15);
    public static final long EXPENSE_ID = 1L;
    public static final BigDecimal AMOUNT = new BigDecimal("100.50");
    public static final String DESCRIPTION = "Lunch at restaurant";
    public static final String CATEGORY = "Food";

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
    void getAllExpenses_shouldReturnListOfExpenses() throws Exception {
        ExpenseRequest request1 = ExpenseRequest.builder()
                .description("Lunch")
                .amount(new BigDecimal("25.50"))
                .date(LocalDate.of(2026, 1, 15))
                .category("Food")
                .build();

        ExpenseRequest request2 = ExpenseRequest.builder()
                .description("Gas")
                .amount(new BigDecimal("50.00"))
                .date(LocalDate.of(2026, 1, 16))
                .category("Travel")
                .build();

        mockMvc.perform(post("/api/expenses/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/expenses/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description").value("Lunch"))
                .andExpect(jsonPath("$[0].amount").value(25.50))
                .andExpect(jsonPath("$[1].description").value("Gas"))
                .andExpect(jsonPath("$[1].amount").value(50.00));
    }

    @Test
    void getAllExpenses_shouldReturnEmptyListWhenNoExpenses() throws Exception {
        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getExpenseById_shouldReturnExpenseWhenExists() throws Exception {
        ExpenseRequest request = createDefaultExpenseRequest();

        String response = mockMvc.perform(post("/api/expenses/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExpenseResponse createdExpense = objectMapper.readValue(response, ExpenseResponse.class);
        Long expenseId = createdExpense.getId();

        mockMvc.perform(get("/api/expenses/{id}", expenseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(DESCRIPTION))
                .andExpect(jsonPath("$.amount").value(AMOUNT.doubleValue()))
                .andExpect(jsonPath("$.category").value(CATEGORY));
    }

    @Test
    void getExpenseById_shouldReturn404WhenExpenseNotFound() throws Exception {
        mockMvc.perform(get("/api/expenses/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createExpense_shouldSaveExpenseWithValidData() throws Exception {
        ExpenseRequest request = createDefaultExpenseRequest();

        mockMvc.perform(post("/api/expenses/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value(DESCRIPTION))
                .andExpect(jsonPath("$.category").value(CATEGORY))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        assertThat(expenseRepository.count()).isEqualTo(1);
    }

    @Test
    void createExpense_shouldHandleMultipleExpenses() throws Exception {
        ExpenseRequest request1 = ExpenseRequest.builder()
                .description("Lunch")
                .amount(new BigDecimal("15.00"))
                .date(LocalDate.of(2024, 1, 15))
                .category("Food")
                .build();

        ExpenseRequest request2 = ExpenseRequest.builder()
                .description("Gas")
                .amount(new BigDecimal("50.00"))
                .date(LocalDate.of(2024, 1, 16))
                .category("Travel")
                .build();

        mockMvc.perform(post("/api/expenses/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/expenses/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        assertThat(expenseRepository.count()).isEqualTo(2);
    }

    @Test
    void createExpense_shouldAssignIdToResponse() throws Exception {
        ExpenseRequest request = createDefaultExpenseRequest();

        String response = mockMvc.perform(post("/api/expenses/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExpenseResponse createdExpense = objectMapper.readValue(response, ExpenseResponse.class);

        assertThat(expenseRepository.count()).isEqualTo(1);
        assertThat(createdExpense.getId()).isNotNull();
    }

    @Test
    void deleteExpense_shouldReturn204WhenExpenseIsDelete() throws Exception {
        ExpenseRequest request = ExpenseRequest.builder()
                .description("Lunch")
                .amount(new BigDecimal("15.00"))
                .date(LocalDate.of(2024, 1, 15))
                .category("Food")
                .build();

        String response = mockMvc.perform(post("/api/expenses/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExpenseResponse createdExpense = objectMapper.readValue(response, ExpenseResponse.class);
        Long expenseId = createdExpense.getId();

        assertThat(expenseRepository.count()).isEqualTo(1);

        mockMvc.perform(delete("/api/expenses/{id}", expenseId))
                .andExpect(status().is2xxSuccessful());

        assertThat(expenseRepository.count()).isEqualTo(0);
    }

    private ExpenseRequest createDefaultExpenseRequest() {
        return ExpenseRequest.builder()
                .description(DESCRIPTION)
                .amount(AMOUNT)
                .date(DATE)
                .category(CATEGORY)
                .build();
    }
}
