package pt.pmr.financetracking.domain.usecases;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import pt.pmr.financetracking.domain.entities.SubCategory;
import pt.pmr.financetracking.domain.entities.SubCategoryFilter;
import pt.pmr.financetracking.domain.exceptions.EntityNotFoundException;
import pt.pmr.financetracking.domain.repositories.SubCategoryRepository;

@ApplicationScoped
@RequiredArgsConstructor
public class ReadSubCategoryUseCase {

    private final SubCategoryRepository subCategoryRepository;

    public Multi<SubCategory> executeFindAll(String categoryId, SubCategoryFilter filter) {
        return subCategoryRepository.fetchAll(categoryId, filter);
    }

    public Uni<SubCategory> executeFindById(String categoryId, String id) {
        return subCategoryRepository.fetchById(categoryId, id)
                .map(opt -> opt.orElseThrow(() -> EntityNotFoundException.buildForId(SubCategory.class, id)));
    }
}
