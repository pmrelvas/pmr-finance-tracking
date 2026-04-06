package pt.pmr.financetracking.data.repositories;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.bson.types.ObjectId;
import pt.pmr.financetracking.data.models.CategoryDocument;
import pt.pmr.financetracking.data.models.SubCategoryDocument;
import pt.pmr.financetracking.domain.entities.SubCategory;
import pt.pmr.financetracking.domain.entities.SubCategoryFilter;
import pt.pmr.financetracking.domain.exceptions.EntityNotFoundException;
import pt.pmr.financetracking.domain.repositories.SubCategoryRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MongoSubCategoryRepository implements ReactivePanacheMongoRepository<CategoryDocument>, SubCategoryRepository {

    @Override
    public Multi<SubCategory> fetchAll(String categoryId, SubCategoryFilter filter) {
        if (!ObjectId.isValid(categoryId)) {
            return Multi.createFrom().empty();
        }
        return findById(new ObjectId(categoryId))
                .onItem().transformToMulti(category -> {
                    if (category == null || category.getSubCategories() == null) {
                        return Multi.createFrom().empty();
                    }
                    return Multi.createFrom().iterable(
                            category.getSubCategories().stream()
                                    .filter(sc -> matchesFilter(sc, filter))
                                    .map(SubCategoryDocument::toEntity)
                                    .toList()
                    );
                });
    }

    @Override
    public Uni<Optional<SubCategory>> fetchById(String categoryId, String id) {
        if (!ObjectId.isValid(categoryId) || !ObjectId.isValid(id)) {
            return Uni.createFrom().item(Optional.empty());
        }
        return findById(new ObjectId(categoryId))
                .map(category -> {
                    if (category == null || category.getSubCategories() == null) {
                        return Optional.empty();
                    }
                    return category.getSubCategories().stream()
                            .filter(sc -> sc.getId().toHexString().equals(id))
                            .findFirst()
                            .map(SubCategoryDocument::toEntity);
                });
    }

    @Override
    public Uni<SubCategory> create(String categoryId, SubCategory subCategory) {
        if (!ObjectId.isValid(categoryId)) {
            return Uni.createFrom().failure(EntityNotFoundException.buildForId(CategoryDocument.class, categoryId));
        }
        return findById(new ObjectId(categoryId))
                .flatMap(category -> {
                    if (category == null) {
                        return Uni.createFrom().failure(EntityNotFoundException.buildForId(CategoryDocument.class, categoryId));
                    }
                    Instant now = Instant.now();
                    SubCategoryDocument newDoc = new SubCategoryDocument(
                            subCategory.toBuilder().createdAt(now).updatedAt(now).build());

                    if (category.getSubCategories() == null) {
                        category.setSubCategories(new ArrayList<>());
                    }
                    category.getSubCategories().add(newDoc);

                    return update(category).map(c -> newDoc.toEntity());
                });
    }

    @Override
    public Uni<Optional<SubCategory>> update(String categoryId, String id, SubCategory subCategory) {
        if (!ObjectId.isValid(categoryId) || !ObjectId.isValid(id)) {
            return Uni.createFrom().item(Optional.empty());
        }
        return findById(new ObjectId(categoryId))
                .flatMap(category -> {
                    if (category == null || category.getSubCategories() == null) {
                        return Uni.createFrom().item(Optional.empty());
                    }
                    List<SubCategoryDocument> subCategories = category.getSubCategories();
                    for (int i = 0; i < subCategories.size(); i++) {
                        if (subCategories.get(i).getId().toHexString().equals(id)) {
                            SubCategoryDocument updated = subCategories.get(i).toBuilder()
                                    .code(subCategory.code())
                                    .displayName(subCategory.displayName())
                                    .updatedAt(Instant.now())
                                    .build();
                            subCategories.set(i, updated);
                            return update(category).map(c -> Optional.of(updated.toEntity()));
                        }
                    }
                    return Uni.createFrom().item(Optional.empty());
                });
    }

    @Override
    public Uni<Long> deleteAll() {
        return mongoCollection()
                .updateMany(new Document(), new Document("$set", new Document("subCategories", new ArrayList<>())))
                .map(result -> result.getModifiedCount());
    }

    private boolean matchesFilter(SubCategoryDocument sc, SubCategoryFilter filter) {
        if (!filter.hasFilterText()) return true;
        return sc.getDisplayName().toLowerCase().contains(filter.searchTerm().toLowerCase());
    }
}
