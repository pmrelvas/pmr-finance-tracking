package pt.pmr.financetracking.domain.usecases;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import pt.pmr.financetracking.domain.entities.Category;
import pt.pmr.financetracking.domain.entities.CategoryFilter;
import pt.pmr.financetracking.domain.exceptions.EntityNotFoundException;
import pt.pmr.financetracking.domain.repositories.CategoryRepository;

@ApplicationScoped
@RequiredArgsConstructor
public class ReadCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public Multi<Category> executeFindAll(CategoryFilter filter) {
        return categoryRepository.fetchAll(filter);
    }

    public Uni<Category> executeFindById(String id) {
        return categoryRepository.fetchById(id)
                .map(opt -> opt.orElseThrow(() -> EntityNotFoundException.buildForId(Category.class, id)));
    }
}
