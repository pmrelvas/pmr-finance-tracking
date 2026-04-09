package pt.pmr.financetracking.domain.usecases;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.pmr.financetracking.domain.entities.Category;
import pt.pmr.financetracking.domain.entities.Expense;
import pt.pmr.financetracking.domain.entities.ExpenseType;
import pt.pmr.financetracking.domain.entities.SubCategory;
import pt.pmr.financetracking.domain.entities.fake.FakeCategories;
import pt.pmr.financetracking.domain.entities.fake.FakeExpenses;
import pt.pmr.financetracking.domain.entities.fake.FakeSubCategories;
import pt.pmr.financetracking.domain.exceptions.EntityNotFoundException;
import pt.pmr.financetracking.domain.repositories.CategoryRepository;
import pt.pmr.financetracking.domain.repositories.ExpenseRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateExpenseUseCaseTest {

    @Mock
    ExpenseRepository expenseRepository;

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    UpdateExpenseUseCase useCase;

    private static final Expense PATCH = Expense.builder()
            .operationDate(Instant.parse("2026-01-15T12:00:00Z"))
            .description("Updated dinner")
            .value(new BigDecimal("50.00"))
            .type(ExpenseType.DEBIT)
            .category(Category.builder().id(FakeCategories.FOOD.id()).build())
            .subCategory(SubCategory.builder().id(FakeSubCategories.RESTAURANT.id()).build())
            .source("CREDIT_CARD")
            .build();

    @Test
    void execute_whenFound_shouldReturnUpdatedExpense() {
        when(categoryRepository.fetchById(FakeCategories.FOOD.id()))
                .thenReturn(Uni.createFrom().item(Optional.of(FakeCategories.FOOD)));
        when(expenseRepository.update(eq(FakeExpenses.RESTAURANT_DINNER.id()), any()))
                .thenReturn(Uni.createFrom().item(Optional.of(FakeExpenses.RESTAURANT_DINNER)));

        var result = useCase.execute(FakeExpenses.RESTAURANT_DINNER.id(), PATCH).await().indefinitely();

        assertEquals(FakeExpenses.RESTAURANT_DINNER, result);
    }

    @Test
    void execute_whenExpenseNotFound_shouldThrowEntityNotFoundException() {
        when(categoryRepository.fetchById(FakeCategories.FOOD.id()))
                .thenReturn(Uni.createFrom().item(Optional.of(FakeCategories.FOOD)));
        when(expenseRepository.update(eq("unknown-id"), any()))
                .thenReturn(Uni.createFrom().item(Optional.empty()));

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute("unknown-id", PATCH).await().indefinitely());
    }

    @Test
    void execute_whenCategoryNotFound_shouldThrowEntityNotFoundException() {
        Expense patch = PATCH.toBuilder()
                .category(Category.builder().id("unknown-category-id").build())
                .build();

        when(categoryRepository.fetchById("unknown-category-id"))
                .thenReturn(Uni.createFrom().item(Optional.empty()));

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(FakeExpenses.RESTAURANT_DINNER.id(), patch).await().indefinitely());
    }
}
