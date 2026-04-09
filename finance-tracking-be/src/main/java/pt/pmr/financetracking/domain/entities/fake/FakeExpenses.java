package pt.pmr.financetracking.domain.entities.fake;

import pt.pmr.financetracking.domain.entities.Expense;
import pt.pmr.financetracking.domain.entities.ExpenseType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface FakeExpenses {

    Expense RESTAURANT_DINNER = Expense.builder()
            .id("69cd16b0634d73e295cacf31")
            .operationDate(Instant.parse("2026-01-15T12:00:00Z"))
            .description("Dinner at restaurant")
            .value(new BigDecimal("45.50"))
            .type(ExpenseType.DEBIT)
            .category(FakeCategories.FOOD)
            .subCategory(FakeSubCategories.RESTAURANT)
            .source("CREDIT_CARD")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    Expense FUEL_UP = Expense.builder()
            .id("69cd16b5634d73e295cacf32")
            .operationDate(Instant.parse("2026-01-16T10:00:00Z"))
            .description("Fuel at gas station")
            .value(new BigDecimal("60.00"))
            .type(ExpenseType.DEBIT)
            .category(FakeCategories.CAR)
            .subCategory(FakeSubCategories.FUEL)
            .source("DEBIT_CARD")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    List<Expense> ALL = List.of(RESTAURANT_DINNER, FUEL_UP);
}
