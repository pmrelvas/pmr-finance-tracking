package pt.pmr.financetracking.domain.entities.fake;

import pt.pmr.financetracking.domain.entities.Category;

import java.time.Instant;
import java.util.List;

public interface FakeCategories {
    Category FOOD = Category.builder()
            .id("69cd166d634d73e295cacf2b")
            .code("FOOD")
            .displayName("Food")
            .subCategories(List.of(FakeSubCategories.RESTAURANT))
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    Category CAR = Category.builder()
            .id("69cd1691634d73e295cacf2c")
            .code("CAR")
            .displayName("Car")
            .subCategories(List.of(FakeSubCategories.FUEL))
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    Category HOUSE = Category.builder()
            .id("69cd1696634d73e295cacf2d")
            .code("HOUSE")
            .displayName("House")
            .subCategories(List.of(FakeSubCategories.RENT))
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    List<Category> ALL = List.of(FOOD, CAR, HOUSE);
}
