package pt.pmr.financetracking.data.repositories;

import com.mongodb.client.result.DeleteResult;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.bson.types.ObjectId;
import pt.pmr.financetracking.data.models.ExpenseDocument;
import pt.pmr.financetracking.domain.entities.Expense;
import pt.pmr.financetracking.domain.entities.ExpenseFilter;
import pt.pmr.financetracking.domain.repositories.ExpenseRepository;

import java.time.Instant;
import java.util.Optional;

@ApplicationScoped
public class MongoExpenseRepository implements ReactivePanacheMongoRepository<ExpenseDocument>, ExpenseRepository {

    @Override
    public Multi<Expense> fetchAll(ExpenseFilter filter) {
        if (filter.searchTerm() == null || filter.searchTerm().isBlank()) {
            return streamAll().onItem().transform(ExpenseDocument::toEntity);
        }

        Document query = new Document("description",
                new Document("$regex", filter.searchTerm()).append("$options", "i"));
        return stream(query, new Document()).onItem().transform(ExpenseDocument::toEntity);
    }

    @Override
    public Uni<Optional<Expense>> fetchById(String id) {
        if (!ObjectId.isValid(id)) {
            return Uni.createFrom().item(Optional.empty());
        }

        return findById(new ObjectId(id))
                .map(document -> Optional.ofNullable(document)
                        .map(ExpenseDocument::toEntity));
    }

    @Override
    public Uni<Expense> create(Expense expense) {
        Instant now = Instant.now();
        ExpenseDocument document = new ExpenseDocument(
                expense.toBuilder()
                        .createdAt(now)
                        .updatedAt(now)
                        .build());

        return persist(document)
                .map(ExpenseDocument::toEntity);
    }

    @Override
    public Uni<Optional<Expense>> update(String id, Expense expense) {
        if (!ObjectId.isValid(id)) {
            return Uni.createFrom().item(Optional.empty());
        }

        return findById(new ObjectId(id))
                .flatMap(document -> {
                    if (document == null) {
                        return Uni.createFrom().item(Optional.empty());
                    }

                    ExpenseDocument updated = document.toBuilder()
                            .operationDate(expense.operationDate())
                            .description(expense.description())
                            .value(expense.value().toPlainString())
                            .type(expense.type().name())
                            .category(new ExpenseDocument.EmbeddedCategory(expense.category()))
                            .subCategory(expense.subCategory() != null
                                    ? new ExpenseDocument.EmbeddedSubCategory(expense.subCategory())
                                    : null)
                            .source(expense.source())
                            .updatedAt(Instant.now())
                            .build();

                    return update(updated)
                            .map(ExpenseDocument::toEntity)
                            .map(Optional::ofNullable);
                });
    }

    @Override
    public Uni<Long> deleteAll() {
        return mongoCollection().deleteMany(new Document())
                .map(DeleteResult::getDeletedCount);
    }
}
