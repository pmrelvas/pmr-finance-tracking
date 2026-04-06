package pt.pmr.financetracking.domain.usecases;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.pmr.financetracking.domain.entities.SubCategory;
import pt.pmr.financetracking.domain.repositories.SubCategoryRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateSubCategoryUseCaseTest {

    @Mock
    SubCategoryRepository subCategoryRepository;

    @InjectMocks
    CreateSubCategoryUseCase useCase;

    private static final String CATEGORY_ID = "cat-1";

    @Test
    void execute_shouldDelegateToRepositoryAndReturnCreatedSubCategory() {
        var subCategory = SubCategory.builder().code("RESTAURANT").displayName("Restaurant").build();
        var created = subCategory.toBuilder().id("1").build();
        when(subCategoryRepository.create(CATEGORY_ID, subCategory)).thenReturn(Uni.createFrom().item(created));

        var result = useCase.execute(CATEGORY_ID, subCategory).await().indefinitely();

        assertEquals(created, result);
        verify(subCategoryRepository).create(CATEGORY_ID, subCategory);
    }
}
