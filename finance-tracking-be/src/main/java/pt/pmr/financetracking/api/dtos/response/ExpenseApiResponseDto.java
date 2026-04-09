package pt.pmr.financetracking.api.dtos.response;

import pt.pmr.financetracking.domain.entities.Expense;
import pt.pmr.financetracking.domain.entities.ExpenseType;

import java.math.BigDecimal;
import java.time.Instant;

public record ExpenseApiResponseDto(
    String id,
    Instant operationDate,
    String description,
    BigDecimal value,
    ExpenseType type,
    CategoryApiResponseDto category,
    SubCategoryApiResponseDto subCategory,
    String source,
    Instant createdAt,
    Instant updatedAt
) {
    public ExpenseApiResponseDto(Expense expense) {
        this(
                expense.id(),
                expense.operationDate(),
                expense.description(),
                expense.value(),
                expense.type(),
                expense.category() != null ? new CategoryApiResponseDto(expense.category()) : null,
                expense.subCategory() != null ? new SubCategoryApiResponseDto(expense.subCategory()) : null,
                expense.source(),
                expense.createdAt(),
                expense.updatedAt()
        );
    }
}
