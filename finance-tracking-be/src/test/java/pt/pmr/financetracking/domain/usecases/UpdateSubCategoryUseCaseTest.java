package pt.pmr.financetracking.domain.usecases;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.pmr.financetracking.domain.entities.SubCategory;
import pt.pmr.financetracking.domain.exceptions.EntityNotFoundException;
import pt.pmr.financetracking.domain.repositories.SubCategoryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateSubCategoryUseCaseTest {

    @Mock
    SubCategoryRepository subCategoryRepository;

    @InjectMocks
    UpdateSubCategoryUseCase useCase;

    private static final String CATEGORY_ID = "cat-1";

    private static final SubCategory PATCH = SubCategory.builder()
            .code("RESTAURANT")
            .displayName("Restaurant Updated")
            .build();

    private static final SubCategory UPDATED = PATCH.toBuilder().id("1").build();

    @Test
    void execute_whenFound_shouldReturnUpdatedSubCategory() {
        when(subCategoryRepository.update(CATEGORY_ID, "1", PATCH)).thenReturn(Uni.createFrom().item(Optional.of(UPDATED)));

        var result = useCase.execute(CATEGORY_ID, "1", PATCH).await().indefinitely();

        assertEquals(UPDATED, result);
        verify(subCategoryRepository).update(CATEGORY_ID, "1", PATCH);
    }

    @Test
    void execute_whenNotFound_shouldThrowEntityNotFoundException() {
        when(subCategoryRepository.update(CATEGORY_ID, "unknown", PATCH)).thenReturn(Uni.createFrom().item(Optional.empty()));

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(CATEGORY_ID, "unknown", PATCH).await().indefinitely());
    }
}
