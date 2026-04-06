package pt.pmr.financetracking.domain.usecases;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import pt.pmr.financetracking.domain.entities.SubCategory;
import pt.pmr.financetracking.domain.repositories.SubCategoryRepository;

@ApplicationScoped
@RequiredArgsConstructor
public class CreateSubCategoryUseCase {

    private final SubCategoryRepository subCategoryRepository;

    public Uni<SubCategory> execute(String categoryId, SubCategory subCategory) {
        return subCategoryRepository.create(categoryId, subCategory);
    }
}
