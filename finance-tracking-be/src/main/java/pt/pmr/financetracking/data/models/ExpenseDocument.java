package pt.pmr.financetracking.data.models;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import pt.pmr.financetracking.domain.entities.Category;
import pt.pmr.financetracking.domain.entities.Expense;
import pt.pmr.financetracking.domain.entities.ExpenseType;
import pt.pmr.financetracking.domain.entities.SubCategory;

import java.math.BigDecimal;
import java.time.Instant;

@MongoEntity(collection = "expenses")
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDocument {

    @BsonId
    private ObjectId id;
    private Instant operationDate;
    private String description;
    private String value;
    private String type;
    private EmbeddedCategory category;
    private EmbeddedSubCategory subCategory;
    private String source;
    private Instant createdAt;
    private Instant updatedAt;

    public ExpenseDocument(Expense expense) {
        this.id = expense.id() == null ? null : new ObjectId(expense.id());
        this.operationDate = expense.operationDate();
        this.description = expense.description();
        this.value = expense.value().toPlainString();
        this.type = expense.type().name();
        this.category = new EmbeddedCategory(expense.category());
        this.subCategory = expense.subCategory() != null ? new EmbeddedSubCategory(expense.subCategory()) : null;
        this.source = expense.source();
        this.createdAt = expense.createdAt();
        this.updatedAt = expense.updatedAt();
    }

    public Expense toEntity() {
        return Expense.builder()
                .id(id.toHexString())
                .operationDate(operationDate)
                .description(description)
                .value(new BigDecimal(value))
                .type(ExpenseType.valueOf(type))
                .category(category.toEntity())
                .subCategory(subCategory != null ? subCategory.toEntity() : null)
                .source(source)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmbeddedCategory {
        private String id;
        private String code;
        private String displayName;

        public EmbeddedCategory(Category category) {
            this.id = category.id();
            this.code = category.code();
            this.displayName = category.displayName();
        }

        public Category toEntity() {
            return Category.builder()
                    .id(id)
                    .code(code)
                    .displayName(displayName)
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmbeddedSubCategory {
        private String id;
        private String code;
        private String displayName;

        public EmbeddedSubCategory(SubCategory subCategory) {
            this.id = subCategory.id();
            this.code = subCategory.code();
            this.displayName = subCategory.displayName();
        }

        public SubCategory toEntity() {
            return SubCategory.builder()
                    .id(id)
                    .code(code)
                    .displayName(displayName)
                    .build();
        }
    }
}
