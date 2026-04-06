package pt.pmr.financetracking.domain.entities;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record SubCategory(
    String id,
    String code,
    String displayName,
    Instant createdAt,
    Instant updatedAt
) {
}
