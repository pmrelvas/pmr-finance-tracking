package pt.pmr.financetracking.domain.entities;

import lombok.Builder;

@Builder
public record SubCategoryFilter(
    String searchTerm
) {
    public static SubCategoryFilter empty() {
        return SubCategoryFilter.builder().build();
    }

    public boolean hasFilterText() {
        return searchTerm != null && !searchTerm.isBlank();
    }
}
