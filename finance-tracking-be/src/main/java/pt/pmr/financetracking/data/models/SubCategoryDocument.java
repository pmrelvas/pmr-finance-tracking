package pt.pmr.financetracking.data.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import pt.pmr.financetracking.domain.entities.SubCategory;

import java.time.Instant;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubCategoryDocument {
    @BsonId
    private ObjectId id;
    private String code;
    private String displayName;
    private Instant createdAt;
    private Instant updatedAt;

    public SubCategoryDocument(SubCategory subCategory) {
        this.id = subCategory.id() == null ? new ObjectId() : new ObjectId(subCategory.id());
        this.code = subCategory.code();
        this.displayName = subCategory.displayName();
        this.createdAt = subCategory.createdAt();
        this.updatedAt = subCategory.updatedAt();
    }

    public SubCategory toEntity() {
        return SubCategory.builder()
                .id(id.toHexString())
                .code(code)
                .displayName(displayName)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
