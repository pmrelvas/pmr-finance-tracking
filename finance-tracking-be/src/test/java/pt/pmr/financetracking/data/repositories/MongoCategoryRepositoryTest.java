package pt.pmr.financetracking.data.repositories;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.mongodb.MongoTestResource;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.pmr.financetracking.domain.entities.Category;
import pt.pmr.financetracking.domain.entities.CategoryFilter;
import pt.pmr.financetracking.domain.entities.fake.FakeCategories;
import pt.pmr.financetracking.domain.repositories.CategoryRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(
        value = MongoTestResource.class,
        initArgs = @ResourceArg(name = MongoTestResource.PORT, value = "27017"))
class MongoCategoryRepositoryTestIT {

    private static final CategoryFilter EMPTY_FILTER = CategoryFilter.builder().build();
    @Inject
    CategoryRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll().await().indefinitely();
    }

    // -------------------------------------------------------------------------
    // fetchAll
    // -------------------------------------------------------------------------

    @Test
    void fetchAll_whenCollectionIsEmpty_returnsEmptyList() {
        List<Category> result = repository.fetchAll(EMPTY_FILTER).collect().asList().await().indefinitely();

        assertThat(result).isEmpty();
    }

    @Test
    void fetchAll_returnsAllPersistedCategories() {
        repository.create(FakeCategories.FOOD).await().indefinitely();
        repository.create(FakeCategories.CAR).await().indefinitely();
        repository.create(FakeCategories.HOUSE).await().indefinitely();

        List<Category> result = repository.fetchAll(EMPTY_FILTER).collect().asList().await().indefinitely();

        assertThat(result).hasSize(3);
        assertThat(result).extracting(Category::code)
                .containsExactlyInAnyOrder("FOOD", "CAR", "HOUSE");
    }

    // -------------------------------------------------------------------------
    // fetchById
    // -------------------------------------------------------------------------

    @Test
    void fetchById_whenCategoryExists_returnsCategory() {
        repository.create(FakeCategories.FOOD).await().indefinitely();

        Optional<Category> result = repository.fetchById(FakeCategories.FOOD.id()).await().indefinitely();

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(FakeCategories.FOOD.id());
        assertThat(result.get().code()).isEqualTo(FakeCategories.FOOD.code());
        assertThat(result.get().displayName()).isEqualTo(FakeCategories.FOOD.displayName());
    }

    @Test
    void fetchById_whenCategoryDoesNotExist_returnsEmpty() {
        Optional<Category> result = repository.fetchById(FakeCategories.FOOD.id()).await().indefinitely();

        assertThat(result).isEmpty();
    }

    @Test
    void fetchById_withInvalidObjectId_returnsEmpty() {
        Optional<Category> result = repository.fetchById("not-a-valid-object-id").await().indefinitely();

        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------------------
    // create
    // -------------------------------------------------------------------------

    @Test
    void create_persistsCategoryAndReturnsIt() {
        Category created = repository.create(FakeCategories.FOOD).await().indefinitely();

        assertThat(created.id()).isEqualTo(FakeCategories.FOOD.id());
        assertThat(created.code()).isEqualTo(FakeCategories.FOOD.code());
        assertThat(created.displayName()).isEqualTo(FakeCategories.FOOD.displayName());
        assertThat(created.createdAt()).isNotNull();
        assertThat(created.updatedAt()).isNotNull();
    }

    @Test
    void create_categoryIsRetrievableAfterPersistence() {
        repository.create(FakeCategories.CAR).await().indefinitely();

        Optional<Category> fetched = repository.fetchById(FakeCategories.CAR.id()).await().indefinitely();

        assertThat(fetched).isPresent();
        assertThat(fetched.get().code()).isEqualTo(FakeCategories.CAR.code());
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Test
    void update_whenCategoryExists_returnsUpdatedCategory() {
        repository.create(FakeCategories.FOOD).await().indefinitely();

        Category updates = Category.builder()
                .code("GROCERIES")
                .displayName("Groceries")
                .build();

        Optional<Category> result = repository.update(FakeCategories.FOOD.id(), updates).await().indefinitely();

        assertThat(result).isPresent();
        assertThat(result.get().code()).isEqualTo("GROCERIES");
        assertThat(result.get().displayName()).isEqualTo("Groceries");
    }

    @Test
    void update_whenCategoryExists_persistsChanges() {
        repository.create(FakeCategories.FOOD).await().indefinitely();

        Category updates = Category.builder()
                .code("GROCERIES")
                .displayName("Groceries")
                .build();

        repository.update(FakeCategories.FOOD.id(), updates).await().indefinitely();

        Optional<Category> fetched = repository.fetchById(FakeCategories.FOOD.id()).await().indefinitely();
        assertThat(fetched).isPresent();
        assertThat(fetched.get().code()).isEqualTo("GROCERIES");
        assertThat(fetched.get().displayName()).isEqualTo("Groceries");
    }

    @Test
    void update_whenCategoryDoesNotExist_returnsEmpty() {
        Category updates = Category.builder()
                .code("GROCERIES")
                .displayName("Groceries")
                .build();

        Optional<Category> result = repository.update(FakeCategories.FOOD.id(), updates).await().indefinitely();

        assertThat(result).isEmpty();
    }

    @Test
    void update_withInvalidObjectId_returnsEmpty() {
        Category updates = Category.builder()
                .code("GROCERIES")
                .displayName("Groceries")
                .build();

        Optional<Category> result = repository.update("not-a-valid-object-id", updates).await().indefinitely();

        assertThat(result).isEmpty();
    }
}
