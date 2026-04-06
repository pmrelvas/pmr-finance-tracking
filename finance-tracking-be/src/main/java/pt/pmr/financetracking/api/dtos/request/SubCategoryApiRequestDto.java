package pt.pmr.financetracking.api.dtos.request;

import jakarta.validation.constraints.NotBlank;
import pt.pmr.financetracking.domain.entities.SubCategory;

public record SubCategoryApiRequestDto(
    @NotBlank(message = "code is required")
    String code,
    @NotBlank(message = "displayName is required")
    String displayName
) {
    public SubCategory toEntity() {
        return SubCategory.builder()
                .code(code)
                .displayName(displayName)
                .build();
    }
}
