package com.finguard.expenses.repository;

import com.finguard.expenses.entity.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ExpenseRepositoryIntegrationTest {

    @Autowired
    private ExpenseRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void save_shouldPersistExpenseToDatabase() {
        // Given
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 15, 10, 30);
        LocalDate date = LocalDate.of(2024, 1, 15);
        BigDecimal amount = new BigDecimal("100.50");

        Expense expense = Expense.builder()
                .description("Lunch at restaurant")
                .amount(amount)
                .date(date)
                .category("Food")
                .createdAt(createdAt)
                .build();

        // When
        Expense saved = repository.save(expense);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDescription()).isEqualTo("Lunch at restaurant");
        assertThat(saved.getAmount()).isEqualByComparingTo(amount);
        assertThat(saved.getDate()).isEqualTo(date);
        assertThat(saved.getCategory()).isEqualTo("Food");
    }

    @Test
    void findById_shouldReturnExpenseWhenExists() {
        // Given
        Expense expense = Expense.builder()
                .description("Coffee")
                .amount(new BigDecimal("5.00"))
                .date(LocalDate.of(2024, 1, 15))
                .category("Beverages")
                .createdAt(LocalDateTime.now())
                .build();

        Expense saved = repository.save(expense);
        Long id = saved.getId();

        // When
        Optional<Expense> found = repository.findById(id);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("Coffee");
        assertThat(found.get().getId()).isEqualTo(id);
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        // Given
        Long nonExistentId = 999L;

        // When
        Optional<Expense> found = repository.findById(nonExistentId);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllExpenses() {
        // Given
        Expense expense1 = Expense.builder()
                .description("Lunch")
                .amount(new BigDecimal("20.00"))
                .date(LocalDate.of(2024, 1, 15))
                .category("Food")
                .createdAt(LocalDateTime.now())
                .build();

        Expense expense2 = Expense.builder()
                .description("Taxi")
                .amount(new BigDecimal("15.00"))
                .date(LocalDate.of(2024, 1, 16))
                .category("Travel")
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(expense1);
        repository.save(expense2);

        // When
        List<Expense> all = repository.findAll();

        // Then
        assertThat(all).hasSize(2);
        assertThat(all).extracting(Expense::getDescription)
                .containsExactlyInAnyOrder("Lunch", "Taxi");
    }

    @Test
    void delete_shouldRemoveExpenseFromDatabase() {
        // Given
        Expense expense = Expense.builder()
                .description("Shopping")
                .amount(new BigDecimal("100.00"))
                .date(LocalDate.of(2024, 1, 17))
                .category("Shopping")
                .createdAt(LocalDateTime.now())
                .build();

        Expense saved = repository.save(expense);
        Long id = saved.getId();

        assertThat(repository.count()).isEqualTo(1);

        // When
        repository.deleteById(id);

        // Then
        assertThat(repository.count()).isEqualTo(0);
        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void update_shouldModifyExistingExpense() {
        // Given
        Expense expense = Expense.builder()
                .description("Original")
                .amount(new BigDecimal("50.00"))
                .date(LocalDate.of(2024, 1, 15))
                .category("Original")
                .createdAt(LocalDateTime.now())
                .build();

        Expense saved = repository.save(expense);

        // When
        saved.setDescription("Updated");
        saved.setAmount(new BigDecimal("75.00"));
        saved.setCategory("Updated");
        Expense updated = repository.save(saved);

        // Then
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getDescription()).isEqualTo("Updated");
        assertThat(updated.getAmount()).isEqualByComparingTo(new BigDecimal("75.00"));
        assertThat(updated.getCategory()).isEqualTo("Updated");
    }

    @Test
    void save_shouldHandleMultipleExpenses() {
        // Given
        Expense expense1 = createExpense("Expense1", "10.00", "Category1");
        Expense expense2 = createExpense("Expense2", "20.00", "Category2");
        Expense expense3 = createExpense("Expense3", "30.00", "Category3");

        // When
        repository.saveAll(List.of(expense1, expense2, expense3));

        // Then
        assertThat(repository.count()).isEqualTo(3);
    }

    @Test
    void findAll_shouldReturnEmptyWhenNoExpenses() {
        // When
        List<Expense> all = repository.findAll();

        // Then
        assertThat(all).isEmpty();
    }

    // Helper method
    private Expense createExpense(String description, String amount, String category) {
        return Expense.builder()
                .description(description)
                .amount(new BigDecimal(amount))
                .date(LocalDate.now())
                .category(category)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
