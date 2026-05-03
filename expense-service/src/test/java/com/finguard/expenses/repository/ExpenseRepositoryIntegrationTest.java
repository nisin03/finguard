package com.finguard.expenses.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.finguard.expenses.entity.Expense;
import com.finguard.expenses.support.PostgresIntegrationTest;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ExpenseRepositoryIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private ExpenseRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void save_shouldPersistExpenseToDatabase() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        BigDecimal amount = new BigDecimal("100.50");

        Expense expense = Expense.builder()
                .description("Lunch at restaurant")
                .amount(amount)
                .date(date)
                .category("Food")
                .build();

        Expense saved = repository.save(expense);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDescription()).isEqualTo("Lunch at restaurant");
        assertThat(saved.getAmount()).isEqualByComparingTo(amount);
        assertThat(saved.getDate()).isEqualTo(date);
        assertThat(saved.getCategory()).isEqualTo("Food");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findById_shouldReturnExpenseWhenExists() {
        Expense expense = Expense.builder()
                .description("Coffee")
                .amount(new BigDecimal("5.00"))
                .date(LocalDate.of(2024, 1, 15))
                .category("Beverages")
                .build();

        Expense saved = repository.save(expense);
        Long id = saved.getId();

        Optional<Expense> found = repository.findById(id);

        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("Coffee");
        assertThat(found.get().getId()).isEqualTo(id);
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        Optional<Expense> found = repository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllExpenses() {
        Expense expense1 = Expense.builder()
                .description("Lunch")
                .amount(new BigDecimal("20.00"))
                .date(LocalDate.of(2024, 1, 15))
                .category("Food")
                .build();

        Expense expense2 = Expense.builder()
                .description("Taxi")
                .amount(new BigDecimal("15.00"))
                .date(LocalDate.of(2024, 1, 16))
                .category("Travel")
                .build();

        repository.save(expense1);
        repository.save(expense2);

        List<Expense> all = repository.findAll();

        assertThat(all).hasSize(2);
        assertThat(all).extracting(Expense::getDescription)
                .containsExactlyInAnyOrder("Lunch", "Taxi");
    }

    @Test
    void delete_shouldRemoveExpenseFromDatabase() {
        Expense expense = Expense.builder()
                .description("Shopping")
                .amount(new BigDecimal("100.00"))
                .date(LocalDate.of(2024, 1, 17))
                .category("Shopping")
                .build();

        Expense saved = repository.save(expense);
        Long id = saved.getId();

        assertThat(repository.count()).isEqualTo(1);

        repository.deleteById(id);

        assertThat(repository.count()).isEqualTo(0);
        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void update_shouldModifyExistingExpense() {
        Expense expense = Expense.builder()
                .description("Original")
                .amount(new BigDecimal("50.00"))
                .date(LocalDate.of(2024, 1, 15))
                .category("Original")
                .build();

        Expense saved = repository.save(expense);

        saved.setDescription("Updated");
        saved.setAmount(new BigDecimal("75.00"));
        saved.setCategory("Updated");
        Expense updated = repository.save(saved);

        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getDescription()).isEqualTo("Updated");
        assertThat(updated.getAmount()).isEqualByComparingTo(new BigDecimal("75.00"));
        assertThat(updated.getCategory()).isEqualTo("Updated");
        assertThat(updated.getCreatedAt()).isEqualTo(saved.getCreatedAt());
    }

    @Test
    void save_shouldHandleMultipleExpenses() {
        Expense expense1 = createExpense("Expense1", "10.00", "Category1");
        Expense expense2 = createExpense("Expense2", "20.00", "Category2");
        Expense expense3 = createExpense("Expense3", "30.00", "Category3");

        repository.saveAll(List.of(expense1, expense2, expense3));

        assertThat(repository.count()).isEqualTo(3);
    }

    @Test
    void findAll_shouldReturnEmptyWhenNoExpenses() {
        List<Expense> all = repository.findAll();

        assertThat(all).isEmpty();
    }

    private Expense createExpense(String description, String amount, String category) {
        return Expense.builder()
                .description(description)
                .amount(new BigDecimal(amount))
                .date(LocalDate.now())
                .category(category)
                .build();
    }
}
