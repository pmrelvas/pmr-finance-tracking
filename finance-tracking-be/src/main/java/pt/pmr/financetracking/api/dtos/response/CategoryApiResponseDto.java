package pt.pmr.financetracking.api.dtos.response;


import pt.pmr.financetracking.domain.entities.Category;

import java.time.Instant;

public record CategoryApiResponseDto(
    String id,
    String code,
    String displayName,
    Instant createdAt,
    Instant updatedAt
) {

    public CategoryApiResponseDto(Category category) {
        this(category.id(),
                category.code(),
                category.displayName(),
                category.createdAt(),
                category.updatedAt());
    }
}
