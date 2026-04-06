package pt.pmr.financetracking.domain.usecases;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.pmr.financetracking.domain.entities.Category;
import pt.pmr.financetracking.domain.repositories.CategoryRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCategoryUseCaseTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CreateCategoryUseCase useCase;

    @Test
    void execute_shouldDelegateToRepositoryAndReturnCreatedCategory() {
        var category = Category.builder().code("FOOD").displayName("Food").build();
        var created = category.toBuilder().id("1").build();
        when(categoryRepository.create(category)).thenReturn(Uni.createFrom().item(created));

        var result = useCase.execute(category).await().indefinitely();

        assertEquals(created, result);
        verify(categoryRepository).create(category);
    }
}