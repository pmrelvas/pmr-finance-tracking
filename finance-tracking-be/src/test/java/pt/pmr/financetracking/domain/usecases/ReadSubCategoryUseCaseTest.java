package pt.pmr.financetracking.domain.usecases;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.pmr.financetracking.domain.entities.SubCategory;
import pt.pmr.financetracking.domain.entities.SubCategoryFilter;
import pt.pmr.financetracking.domain.exceptions.EntityNotFoundException;
import pt.pmr.financetracking.domain.repositories.SubCategoryRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadSubCategoryUseCaseTest {

    @Mock
    SubCategoryRepository subCategoryRepository;

    @InjectMocks
    ReadSubCategoryUseCase useCase;

    private static final String CATEGORY_ID = "cat-1";

    private static final SubCategory RESTAURANT = SubCategory.builder()
            .id("1")
            .code("RESTAURANT")
            .displayName("Restaurant")
            .build();

    private static final SubCategory FUEL = SubCategory.builder()
            .id("2")
            .code("FUEL")
            .displayName("Fuel Station")
            .build();

    @Test
    void executeFindAll_withEmptyFilter_shouldReturnAllSubCategories() {
        var filter = SubCategoryFilter.empty();
        when(subCategoryRepository.fetchAll(CATEGORY_ID, filter)).thenReturn(Multi.createFrom().items(RESTAURANT, FUEL));

        var result = useCase.executeFindAll(CATEGORY_ID, filter).collect().asList().await().indefinitely();

        assertEquals(List.of(RESTAURANT, FUEL), result);
    }

    @Test
    void executeFindAll_withFilterText_shouldReturnOnlyMatchingSubCategories() {
        var filter = SubCategoryFilter.builder().searchTerm("resta").build();
        when(subCategoryRepository.fetchAll(CATEGORY_ID, filter)).thenReturn(Multi.createFrom().items(RESTAURANT));

        var result = useCase.executeFindAll(CATEGORY_ID, filter).collect().asList().await().indefinitely();

        assertEquals(List.of(RESTAURANT), result);
    }

    @Test
    void executeFindById_whenFound_shouldReturnSubCategory() {
        when(subCategoryRepository.fetchById(CATEGORY_ID, "1")).thenReturn(Uni.createFrom().item(Optional.of(RESTAURANT)));

        var result = useCase.executeFindById(CATEGORY_ID, "1").await().indefinitely();

        assertEquals(RESTAURANT, result);
    }

    @Test
    void executeFindById_whenNotFound_shouldThrowEntityNotFoundException() {
        when(subCategoryRepository.fetchById(CATEGORY_ID, "unknown")).thenReturn(Uni.createFrom().item(Optional.empty()));

        assertThrows(EntityNotFoundException.class,
                () -> useCase.executeFindById(CATEGORY_ID, "unknown").await().indefinitely());
    }
}
