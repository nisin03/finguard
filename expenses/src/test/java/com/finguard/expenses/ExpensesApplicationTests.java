package com.finguard.expenses;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.finguard.expenses.support.PostgresIntegrationTest;

@SpringBootTest
@ActiveProfiles("test")
class ExpensesApplicationTests extends PostgresIntegrationTest {

	@Test
	void contextLoads() {
	}

}
