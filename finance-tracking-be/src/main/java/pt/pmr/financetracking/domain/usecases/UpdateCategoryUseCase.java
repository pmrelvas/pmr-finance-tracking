package pt.pmr.financetracking.domain.usecases;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import pt.pmr.financetracking.domain.entities.Category;
import pt.pmr.financetracking.domain.exceptions.EntityNotFoundException;
import pt.pmr.financetracking.domain.repositories.CategoryRepository;

@ApplicationScoped
@RequiredArgsConstructor
public class UpdateCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final ReadCategoryUseCase readCategoryUseCase;

    public Uni<Category> execute(String id, Category category) {
        return categoryRepository.update(id, category)
                .map(opt -> opt.orElseThrow(() -> EntityNotFoundException.buildForId(Category.class, id)));
    }
}
