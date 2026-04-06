package pt.pmr.financetracking.domain.usecases;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import pt.pmr.financetracking.domain.entities.SubCategory;
import pt.pmr.financetracking.domain.exceptions.EntityNotFoundException;
import pt.pmr.financetracking.domain.repositories.SubCategoryRepository;

@ApplicationScoped
@RequiredArgsConstructor
public class UpdateSubCategoryUseCase {

    private final SubCategoryRepository subCategoryRepository;

    public Uni<SubCategory> execute(String categoryId, String id, SubCategory subCategory) {
        return subCategoryRepository.update(categoryId, id, subCategory)
                .map(opt -> opt.orElseThrow(() -> EntityNotFoundException.buildForId(SubCategory.class, id)));
    }
}
