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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateExpenseUseCaseTest {

    @Mock
    ExpenseRepository expenseRepository;

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CreateExpenseUseCase useCase;

    @Test
    void execute_shouldResolveCategoryAndCreateExpense() {
        Expense input = Expense.builder()
                .operationDate(Instant.parse("2026-01-15T12:00:00Z"))
                .description("Dinner at restaurant")
                .value(new BigDecimal("45.50"))
                .type(ExpenseType.DEBIT)
                .category(Category.builder().id(FakeCategories.FOOD.id()).build())
                .subCategory(SubCategory.builder().id(FakeSubCategories.RESTAURANT.id()).build())
                .source("CREDIT_CARD")
                .build();

        when(categoryRepository.fetchById(FakeCategories.FOOD.id()))
                .thenReturn(Uni.createFrom().item(Optional.of(FakeCategories.FOOD)));
        when(expenseRepository.create(any()))
                .thenReturn(Uni.createFrom().item(FakeExpenses.RESTAURANT_DINNER));

        var result = useCase.execute(input).await().indefinitely();

        assertEquals(FakeExpenses.RESTAURANT_DINNER, result);
        verify(categoryRepository).fetchById(FakeCategories.FOOD.id());
        verify(expenseRepository).create(any());
    }

    @Test
    void execute_whenCategoryNotFound_shouldThrowEntityNotFoundException() {
        Expense input = Expense.builder()
                .operationDate(Instant.now())
                .description("Test expense")
                .value(new BigDecimal("10.00"))
                .type(ExpenseType.DEBIT)
                .category(Category.builder().id("unknown-category-id").build())
                .source("CASH")
                .build();

        when(categoryRepository.fetchById("unknown-category-id"))
                .thenReturn(Uni.createFrom().item(Optional.empty()));

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(input).await().indefinitely());
    }

    @Test
    void execute_whenSubCategoryNotFoundInCategory_shouldThrowEntityNotFoundException() {
        Expense input = Expense.builder()
                .operationDate(Instant.now())
                .description("Test expense")
                .value(new BigDecimal("10.00"))
                .type(ExpenseType.DEBIT)
                .category(Category.builder().id(FakeCategories.FOOD.id()).build())
                .subCategory(SubCategory.builder().id("non-existent-sub-id").build())
                .source("CASH")
                .build();

        when(categoryRepository.fetchById(FakeCategories.FOOD.id()))
                .thenReturn(Uni.createFrom().item(Optional.of(FakeCategories.FOOD)));

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(input).await().indefinitely());
    }
}
