package pt.pmr.financetracking.domain.usecases;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.pmr.financetracking.domain.entities.Category;
import pt.pmr.financetracking.domain.entities.CategoryFilter;
import pt.pmr.financetracking.domain.exceptions.EntityNotFoundException;
import pt.pmr.financetracking.domain.repositories.CategoryRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadCategoryUseCaseTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    ReadCategoryUseCase useCase;

    private static final Category CATEGORY = Category.builder()
            .id("1")
            .code("FOOD")
            .displayName("Food")
            .build();

    @Test
    void executeFindAll_shouldReturnAllCategories() {
        CategoryFilter filter = CategoryFilter.builder().build();
        when(categoryRepository.fetchAll(filter)).thenReturn(Multi.createFrom().items(CATEGORY));

        var result = useCase.executeFindAll(filter).collect().asList().await().indefinitely();

        assertEquals(List.of(CATEGORY), result);
    }

    @Test
    void executeFindById_whenFound_shouldReturnCategory() {
        when(categoryRepository.fetchById("1")).thenReturn(Uni.createFrom().item(Optional.of(CATEGORY)));

        var result = useCase.executeFindById("1").await().indefinitely();

        assertEquals(CATEGORY, result);
    }

    @Test
    void executeFindById_whenNotFound_shouldThrowEntityNotFoundException() {
        when(categoryRepository.fetchById("unknown")).thenReturn(Uni.createFrom().item(Optional.empty()));

        assertThrows(EntityNotFoundException.class,
                () -> useCase.executeFindById("unknown").await().indefinitely());
    }
}
