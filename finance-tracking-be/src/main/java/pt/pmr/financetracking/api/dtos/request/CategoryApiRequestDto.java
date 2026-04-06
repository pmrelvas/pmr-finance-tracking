package pt.pmr.financetracking.api.dtos.request;

import jakarta.validation.constraints.NotBlank;
import pt.pmr.financetracking.domain.entities.Category;

public record CategoryApiRequestDto(
    @NotBlank(message = "code is required")
    String code,
    @NotBlank(message = "displayName is required")
    String displayName
) {
    public CategoryApiRequestDto(Category category) {
        this(
                category.code(),
                category.displayName()
        );
    }

    public Category toEntity() {
        return Category.builder()
                .code(code)
                .displayName(displayName)
                .build();
    }
}
