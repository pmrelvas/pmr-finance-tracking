package pt.pmr.financetracking.domain.entities.fake;

import pt.pmr.financetracking.domain.entities.SubCategory;

import java.time.Instant;
import java.util.List;

public interface FakeSubCategories {
    SubCategory RESTAURANT = SubCategory.builder()
            .id("69cd16a0634d73e295cacf2e")
            .code("RESTAURANT")
            .displayName("Restaurant")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    SubCategory FUEL = SubCategory.builder()
            .id("69cd16a5634d73e295cacf2f")
            .code("FUEL")
            .displayName("Fuel")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    SubCategory RENT = SubCategory.builder()
            .id("69cd16aa634d73e295cacf30")
            .code("RENT")
            .displayName("Rent")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    List<SubCategory> ALL = List.of(RESTAURANT, FUEL, RENT);
}
