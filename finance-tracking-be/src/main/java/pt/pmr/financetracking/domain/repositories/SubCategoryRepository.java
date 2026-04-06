package pt.pmr.financetracking.domain.repositories;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import pt.pmr.financetracking.domain.entities.SubCategory;
import pt.pmr.financetracking.domain.entities.SubCategoryFilter;

import java.util.Optional;

public interface SubCategoryRepository {
    Multi<SubCategory> fetchAll(String categoryId, SubCategoryFilter filter);

    Uni<Optional<SubCategory>> fetchById(String categoryId, String id);

    Uni<SubCategory> create(String categoryId, SubCategory subCategory);

    Uni<Optional<SubCategory>> update(String categoryId, String id, SubCategory subCategory);

    Uni<Long> deleteAll();
}
