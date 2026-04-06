package pt.pmr.financetracking.api.dtos.response;

import pt.pmr.financetracking.domain.entities.SubCategory;

import java.time.Instant;

public record SubCategoryApiResponseDto(
    String id,
    String code,
    String displayName,
    Instant createdAt,
    Instant updatedAt
) {
    public SubCategoryApiResponseDto(SubCategory subCategory) {
        this(
                subCategory.id(),
                subCategory.code(),
                subCategory.displayName(),
                subCategory.createdAt(),
                subCategory.updatedAt()
        );
    }
}
