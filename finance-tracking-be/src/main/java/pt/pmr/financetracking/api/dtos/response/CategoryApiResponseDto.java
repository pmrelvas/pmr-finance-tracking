package pt.pmr.financetracking.api.dtos.response;

import pt.pmr.financetracking.domain.entities.Category;

import java.time.Instant;
import java.util.List;

public record CategoryApiResponseDto(
    String id,
    String code,
    String displayName,
    List<SubCategoryApiResponseDto> subCategories,
    Instant createdAt,
    Instant updatedAt
) {
    public CategoryApiResponseDto(Category category) {
        this(
                category.id(),
                category.code(),
                category.displayName(),
                category.subCategories() == null ? List.of() :
                        category.subCategories().stream().map(SubCategoryApiResponseDto::new).toList(),
                category.createdAt(),
                category.updatedAt()
        );
    }
}
