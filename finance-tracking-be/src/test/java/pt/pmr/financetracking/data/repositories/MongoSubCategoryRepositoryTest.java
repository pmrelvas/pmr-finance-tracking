package pt.pmr.financetracking.data.repositories;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.mongodb.MongoTestResource;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.pmr.financetracking.domain.entities.SubCategory;
import pt.pmr.financetracking.domain.entities.SubCategoryFilter;
import pt.pmr.financetracking.domain.entities.fake.FakeCategories;
import pt.pmr.financetracking.domain.entities.fake.FakeSubCategories;
import pt.pmr.financetracking.domain.repositories.CategoryRepository;
import pt.pmr.financetracking.domain.repositories.SubCategoryRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(
        value = MongoTestResource.class,
        initArgs = @ResourceArg(name = MongoTestResource.PORT, value = "27017"))
class MongoSubCategoryRepositoryTestIT {

    private static final String CATEGORY_ID = FakeCategories.FOOD.id();

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    SubCategoryRepository repository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll().await().indefinitely();
        categoryRepository.create(FakeCategories.FOOD).await().indefinitely();
        categoryRepository.create(FakeCategories.CAR).await().indefinitely();
        categoryRepository.create(FakeCategories.HOUSE).await().indefinitely();
    }

    // -------------------------------------------------------------------------
    // fetchAll — no filter
    // -------------------------------------------------------------------------

    @Test
    void fetchAll_whenCollectionIsEmpty_returnsEmptyList() {
        List<SubCategory> result = repository.fetchAll(CATEGORY_ID, SubCategoryFilter.empty()).collect().asList().await().indefinitely();

        assertThat(result).isEmpty();
    }

    @Test
    void fetchAll_returnsAllPersistedSubCategories() {
        repository.create(CATEGORY_ID, FakeSubCategories.RESTAURANT).await().indefinitely();
        repository.create(CATEGORY_ID, FakeSubCategories.FUEL).await().indefinitely();
        repository.create(CATEGORY_ID, FakeSubCategories.RENT).await().indefinitely();

        List<SubCategory> result = repository.fetchAll(CATEGORY_ID, SubCategoryFilter.empty()).collect().asList().await().indefinitely();

        assertThat(result).hasSize(3);
        assertThat(result).extracting(SubCategory::code)
                .containsExactlyInAnyOrder("RESTAURANT", "FUEL", "RENT");
    }

    // -------------------------------------------------------------------------
    // fetchAll — searchTerm
    // -------------------------------------------------------------------------

    @Test
    void fetchAll_withFilterText_returnsPartialCaseInsensitiveMatches() {
        repository.create(CATEGORY_ID, FakeSubCategories.RESTAURANT).await().indefinitely();
        repository.create(CATEGORY_ID, FakeSubCategories.FUEL).await().indefinitely();

        var filter = SubCategoryFilter.builder().searchTerm("resta").build();
        List<SubCategory> result = repository.fetchAll(CATEGORY_ID, filter).collect().asList().await().indefinitely();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).code()).isEqualTo(FakeSubCategories.RESTAURANT.code());
    }

    @Test
    void fetchAll_withFilterText_isCaseInsensitive() {
        repository.create(CATEGORY_ID, FakeSubCategories.RESTAURANT).await().indefinitely();

        var filter = SubCategoryFilter.builder().searchTerm("RESTA").build();
        List<SubCategory> result = repository.fetchAll(CATEGORY_ID, filter).collect().asList().await().indefinitely();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).code()).isEqualTo(FakeSubCategories.RESTAURANT.code());
    }

    @Test
    void fetchAll_withFilterText_whenNoneMatch_returnsEmptyList() {
        repository.create(CATEGORY_ID, FakeSubCategories.RESTAURANT).await().indefinitely();

        var filter = SubCategoryFilter.builder().searchTerm("xyz_no_match").build();
        List<SubCategory> result = repository.fetchAll(CATEGORY_ID, filter).collect().asList().await().indefinitely();

        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------------------
    // fetchById
    // -------------------------------------------------------------------------

    @Test
    void fetchById_whenSubCategoryExists_returnsSubCategory() {
        repository.create(CATEGORY_ID, FakeSubCategories.RESTAURANT).await().indefinitely();

        Optional<SubCategory> result = repository.fetchById(CATEGORY_ID, FakeSubCategories.RESTAURANT.id()).await().indefinitely();

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(FakeSubCategories.RESTAURANT.id());
        assertThat(result.get().code()).isEqualTo(FakeSubCategories.RESTAURANT.code());
        assertThat(result.get().displayName()).isEqualTo(FakeSubCategories.RESTAURANT.displayName());
    }

    @Test
    void fetchById_whenSubCategoryDoesNotExist_returnsEmpty() {
        Optional<SubCategory> result = repository.fetchById(CATEGORY_ID, FakeSubCategories.RESTAURANT.id()).await().indefinitely();

        assertThat(result).isEmpty();
    }

    @Test
    void fetchById_withInvalidObjectId_returnsEmpty() {
        Optional<SubCategory> result = repository.fetchById(CATEGORY_ID, "not-a-valid-object-id").await().indefinitely();

        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------------------
    // create
    // -------------------------------------------------------------------------

    @Test
    void create_persistsSubCategoryAndReturnsIt() {
        SubCategory created = repository.create(CATEGORY_ID, FakeSubCategories.RESTAURANT).await().indefinitely();

        assertThat(created.id()).isEqualTo(FakeSubCategories.RESTAURANT.id());
        assertThat(created.code()).isEqualTo(FakeSubCategories.RESTAURANT.code());
        assertThat(created.displayName()).isEqualTo(FakeSubCategories.RESTAURANT.displayName());
        assertThat(created.createdAt()).isNotNull();
        assertThat(created.updatedAt()).isNotNull();
    }

    @Test
    void create_subCategoryIsRetrievableAfterPersistence() {
        repository.create(CATEGORY_ID, FakeSubCategories.FUEL).await().indefinitely();

        Optional<SubCategory> fetched = repository.fetchById(CATEGORY_ID, FakeSubCategories.FUEL.id()).await().indefinitely();

        assertThat(fetched).isPresent();
        assertThat(fetched.get().code()).isEqualTo(FakeSubCategories.FUEL.code());
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Test
    void update_whenSubCategoryExists_returnsUpdatedSubCategory() {
        repository.create(CATEGORY_ID, FakeSubCategories.RESTAURANT).await().indefinitely();

        SubCategory updates = SubCategory.builder()
                .code("FAST_FOOD")
                .displayName("Fast Food")
                .build();

        Optional<SubCategory> result = repository.update(CATEGORY_ID, FakeSubCategories.RESTAURANT.id(), updates).await().indefinitely();

        assertThat(result).isPresent();
        assertThat(result.get().code()).isEqualTo("FAST_FOOD");
        assertThat(result.get().displayName()).isEqualTo("Fast Food");
    }

    @Test
    void update_whenSubCategoryExists_persistsChanges() {
        repository.create(CATEGORY_ID, FakeSubCategories.RESTAURANT).await().indefinitely();

        SubCategory updates = SubCategory.builder()
                .code("FAST_FOOD")
                .displayName("Fast Food")
                .build();

        repository.update(CATEGORY_ID, FakeSubCategories.RESTAURANT.id(), updates).await().indefinitely();

        Optional<SubCategory> fetched = repository.fetchById(CATEGORY_ID, FakeSubCategories.RESTAURANT.id()).await().indefinitely();
        assertThat(fetched).isPresent();
        assertThat(fetched.get().code()).isEqualTo("FAST_FOOD");
        assertThat(fetched.get().displayName()).isEqualTo("Fast Food");
    }

    @Test
    void update_whenSubCategoryDoesNotExist_returnsEmpty() {
        SubCategory updates = SubCategory.builder()
                .code("FAST_FOOD")
                .displayName("Fast Food")
                .build();

        Optional<SubCategory> result = repository.update(CATEGORY_ID, FakeSubCategories.RESTAURANT.id(), updates).await().indefinitely();

        assertThat(result).isEmpty();
    }

    @Test
    void update_withInvalidObjectId_returnsEmpty() {
        SubCategory updates = SubCategory.builder()
                .code("FAST_FOOD")
                .displayName("Fast Food")
                .build();

        Optional<SubCategory> result = repository.update(CATEGORY_ID, "not-a-valid-object-id", updates).await().indefinitely();

        assertThat(result).isEmpty();
    }
}
