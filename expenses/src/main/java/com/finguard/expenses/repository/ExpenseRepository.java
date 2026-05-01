package com.finguard.expenses.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.finguard.expenses.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query(value = """
            SELECT * FROM expense
            WHERE deleted = true
            AND deleted_at >= NOW() - INTERVAL '7 days'
            ORDER BY deleted_at DESC
            """, nativeQuery = true)
    List<Expense> findRecentlyDeleted();
}
