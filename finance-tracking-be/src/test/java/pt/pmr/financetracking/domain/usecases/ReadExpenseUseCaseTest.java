package pt.pmr.financetracking.domain.usecases;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.pmr.financetracking.domain.entities.Expense;
import pt.pmr.financetracking.domain.entities.ExpenseFilter;
import pt.pmr.financetracking.domain.entities.ExpenseType;
import pt.pmr.financetracking.domain.entities.fake.FakeExpenses;
import pt.pmr.financetracking.domain.exceptions.EntityNotFoundException;
import pt.pmr.financetracking.domain.repositories.ExpenseRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadExpenseUseCaseTest {

    @Mock
    ExpenseRepository expenseRepository;

    @InjectMocks
    ReadExpenseUseCase useCase;

    @Test
    void executeFindAll_shouldReturnAllExpenses() {
        ExpenseFilter filter = ExpenseFilter.builder().build();
        when(expenseRepository.fetchAll(filter))
                .thenReturn(Multi.createFrom().items(FakeExpenses.RESTAURANT_DINNER, FakeExpenses.FUEL_UP));

        var result = useCase.executeFindAll(filter).collect().asList().await().indefinitely();

        assertEquals(List.of(FakeExpenses.RESTAURANT_DINNER, FakeExpenses.FUEL_UP), result);
    }

    @Test
    void executeFindById_whenFound_shouldReturnExpense() {
        when(expenseRepository.fetchById(FakeExpenses.RESTAURANT_DINNER.id()))
                .thenReturn(Uni.createFrom().item(Optional.of(FakeExpenses.RESTAURANT_DINNER)));

        var result = useCase.executeFindById(FakeExpenses.RESTAURANT_DINNER.id()).await().indefinitely();

        assertEquals(FakeExpenses.RESTAURANT_DINNER, result);
    }

    @Test
    void executeFindById_whenNotFound_shouldThrowEntityNotFoundException() {
        when(expenseRepository.fetchById("unknown"))
                .thenReturn(Uni.createFrom().item(Optional.empty()));

        assertThrows(EntityNotFoundException.class,
                () -> useCase.executeFindById("unknown").await().indefinitely());
    }
}
