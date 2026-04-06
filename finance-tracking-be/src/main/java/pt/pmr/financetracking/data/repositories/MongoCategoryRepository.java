package pt.pmr.financetracking.data.repositories;

import com.mongodb.client.result.DeleteResult;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.bson.types.ObjectId;
import pt.pmr.financetracking.data.models.CategoryDocument;
import pt.pmr.financetracking.domain.entities.Category;
import pt.pmr.financetracking.domain.entities.CategoryFilter;
import pt.pmr.financetracking.domain.repositories.CategoryRepository;

import java.time.Instant;
import java.util.Optional;

@ApplicationScoped
public class MongoCategoryRepository implements ReactivePanacheMongoRepository<CategoryDocument>, CategoryRepository {

    @Override
    public Multi<Category> fetchAll(CategoryFilter filter) {
        if (filter.searchTerm() == null || filter.searchTerm().isBlank()) {
            return streamAll().onItem().transform(CategoryDocument::toEntity);
        }

        Document query = new Document("displayName",
                new Document("$regex", filter.searchTerm()).append("$options", "i"));
        return stream(query, new Document()).onItem().transform(CategoryDocument::toEntity);
    }

    @Override
    public Uni<Optional<Category>> fetchById(String id) {
        if (!ObjectId.isValid(id)) {
            return Uni.createFrom().item(Optional.empty());
        }

        return findById(new ObjectId(id))
                .map(document -> Optional.ofNullable(document)
                        .map(CategoryDocument::toEntity));
    }

    @Override
    public Uni<Category> create(Category category) {
        Instant now = Instant.now();
        CategoryDocument document = new CategoryDocument(
                category.toBuilder()
                        .createdAt(now)
                        .updatedAt(now)
                        .build());

        return persist(document)
                .map(CategoryDocument::toEntity);
    }

    @Override
    public Uni<Optional<Category>> update(String id, Category category) {
        if (!ObjectId.isValid(id)) {
            return Uni.createFrom().item(Optional.empty());
        }

        return findById(new ObjectId(id))
                .flatMap(document -> {
                    if (document == null) {
                        return Uni.createFrom().item(Optional.empty());
                    }

                    CategoryDocument categoryDocumentUpdate = document.toBuilder()
                            .code(category.code())
                            .displayName(category.displayName())
                            .updatedAt(Instant.now())
                            .build();

                    return update(categoryDocumentUpdate)
                            .map(CategoryDocument::toEntity)
                            .map(Optional::ofNullable);
                });
    }

    @Override
    public Uni<Long> deleteAll() {
        return mongoCollection().deleteMany(new Document())
                .map(DeleteResult::getDeletedCount);
    }
}
