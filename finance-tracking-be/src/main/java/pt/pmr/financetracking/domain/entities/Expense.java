package pt.pmr.financetracking.domain.entities;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder(toBuilder = true)
public record Expense(
    String id,
    Instant operationDate,
    String description,
    BigDecimal value,
    ExpenseType type,
    Category category,
    SubCategory subCategory,
    String source,
    Instant createdAt,
    Instant updatedAt
) {
}
