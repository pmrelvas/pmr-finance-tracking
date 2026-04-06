package pt.pmr.financetracking.domain.repositories;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import pt.pmr.financetracking.domain.entities.Category;
import pt.pmr.financetracking.domain.entities.CategoryFilter;

import java.util.Optional;

public interface CategoryRepository {
    Multi<Category> fetchAll(CategoryFilter filter);

    Uni<Optional<Category>> fetchById(String id);

    Uni<Category> create(Category category);

    Uni<Optional<Category>> update(String id, Category category);

    Uni<Long> deleteAll();
}
