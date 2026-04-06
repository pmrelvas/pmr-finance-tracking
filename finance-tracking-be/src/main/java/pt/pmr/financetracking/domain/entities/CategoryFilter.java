    package pt.pmr.financetracking.domain.entities;

import lombok.Builder;

@Builder(toBuilder = true)
public record CategoryFilter(String searchTerm) {
}
