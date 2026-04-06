package pt.pmr.financetracking.domain.usecases;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import pt.pmr.financetracking.domain.entities.Category;
import pt.pmr.financetracking.domain.repositories.CategoryRepository;

@ApplicationScoped
@RequiredArgsConstructor
public class CreateCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public Uni<Category> execute(Category category) {
        return categoryRepository.create(category);
    }
}
