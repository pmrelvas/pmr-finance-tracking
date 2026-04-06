package pt.pmr.financetracking.domain.usecases;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.pmr.financetracking.domain.entities.Category;
import pt.pmr.financetracking.domain.exceptions.EntityNotFoundException;
import pt.pmr.financetracking.domain.repositories.CategoryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCategoryUseCaseTest {

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    ReadCategoryUseCase readCategoryUseCase;

    @InjectMocks
    UpdateCategoryUseCase useCase;

    private static final Category PATCH = Category.builder().code("FOOD").displayName("Food Updated").build();
    private static final Category UPDATED = PATCH.toBuilder().id("1").build();

    @Test

    void execute_whenFound_shouldReturnUpdatedCategory() {
        when(categoryRepository.update("1", PATCH)).thenReturn(Uni.createFrom().item(Optional.of(UPDATED)));

        var result = useCase.execute("1", PATCH).await().indefinitely();

        assertEquals(UPDATED, result);
        verify(categoryRepository).update("1", PATCH);
    }

    @Test
    void execute_whenNotFound_shouldThrowEntityNotFoundException() {
        when(categoryRepository.update("unknown", PATCH)).thenReturn(Uni.createFrom().item(Optional.empty()));

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute("unknown", PATCH).await().indefinitely());
    }
}
