package pt.pmr.financetracking.api.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pt.pmr.financetracking.domain.entities.Category;
import pt.pmr.financetracking.domain.entities.Expense;
import pt.pmr.financetracking.domain.entities.ExpenseType;
import pt.pmr.financetracking.domain.entities.SubCategory;

import java.math.BigDecimal;
import java.time.Instant;

public record ExpenseApiRequestDto(
    @NotNull(message = "operationDate is required")
    Instant operationDate,
    @NotBlank(message = "description is required")
    String description,
    @NotNull(message = "value is required")
    BigDecimal value,
    @NotNull(message = "type is required")
    ExpenseType type,
    @NotBlank(message = "categoryId is required")
    String categoryId,
    String subCategoryId,
    @NotBlank(message = "source is required")
    String source
) {
    public Expense toEntity() {
        return Expense.builder()
                .operationDate(operationDate)
                .description(description)
                .value(value)
                .type(type)
                .category(Category.builder().id(categoryId).build())
                .subCategory(subCategoryId != null && !subCategoryId.isBlank()
                        ? SubCategory.builder().id(subCategoryId).build()
                        : null)
                .source(source)
                .build();
    }
}
