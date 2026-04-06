package pt.pmr.financetracking.data.models;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import pt.pmr.financetracking.domain.entities.Category;

import java.time.Instant;

@MongoEntity(collection = "categories")
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDocument {
    @BsonId
    private ObjectId id;
    private String code;
    private String displayName;
    private Instant createdAt;
    private Instant updatedAt;

    public CategoryDocument(Category category) {
        this.id = category.id() == null ? null : new ObjectId(category.id());
        this.code = category.code();
        this.displayName = category.displayName();
        this.createdAt = category.createdAt();
        this.updatedAt = category.updatedAt();
    }

    public Category toEntity() {
        return Category.builder()
                .id(id.toHexString())
                .code(code)
                .displayName(displayName)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
