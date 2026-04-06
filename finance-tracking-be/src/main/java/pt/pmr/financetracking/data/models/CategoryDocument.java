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
import java.util.List;

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
    private List<SubCategoryDocument> subCategories;
    private Instant createdAt;
    private Instant updatedAt;

    public CategoryDocument(Category category) {
        this.id = category.id() == null ? null : new ObjectId(category.id());
        this.code = category.code();
        this.displayName = category.displayName();
        this.subCategories = category.subCategories() == null
                ? List.of()
                : category.subCategories().stream().map(SubCategoryDocument::new).toList();
        this.createdAt = category.createdAt();
        this.updatedAt = category.updatedAt();
    }

    public Category toEntity() {
        return Category.builder()
                .id(id.toHexString())
                .code(code)
                .displayName(displayName)
                .subCategories(subCategories == null ? List.of() :
                        subCategories.stream().map(SubCategoryDocument::toEntity).toList())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
