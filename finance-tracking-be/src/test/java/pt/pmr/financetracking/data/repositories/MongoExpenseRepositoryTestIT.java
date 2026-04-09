package pt.pmr.financetracking.data.repositories;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.mongodb.MongoTestResource;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.pmr.financetracking.domain.entities.Category;
import pt.pmr.financetracking.domain.entities.Expense;
import pt.pmr.financetracking.domain.entities.ExpenseFilter;
import pt.pmr.financetracking.domain.entities.ExpenseType;
import pt.pmr.financetracking.domain.entities.SubCategory;
import pt.pmr.financetracking.domain.entities.fake.FakeCategories;
import pt.pmr.financetracking.domain.entities.fake.FakeExpenses;
import pt.pmr.financetracking.domain.entities.fake.FakeSubCategories;
import pt.pmr.financetracking.domain.repositories.ExpenseRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(
        value = MongoTestResource.class,
        initArgs = @ResourceArg(name = MongoTestResource.PORT, value = "27017"))
class MongoExpenseRepositoryTestIT {

    private static final ExpenseFilter EMPTY_FILTER = ExpenseFilter.builder().build();

    @Inject
    ExpenseRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll().await().indefinitely();
    }

    @Test
    void fetchAll_whenCollectionIsEmpty_returnsEmptyList() {
        List<Expense> result = repository.fetchAll(EMPTY_FILTER).collect().asList().await().indefinitely();

        assertThat(result).isEmpty();
    }

    @Test
    void fetchAll_returnsAllPersistedExpenses() {
        repository.create(FakeExpenses.RESTAURANT_DINNER).await().indefinitely();
        repository.create(FakeExpenses.FUEL_UP).await().indefinitely();

        List<Expense> result = repository.fetchAll(EMPTY_FILTER).collect().asList().await().indefinitely();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Expense::description)
                .containsExactlyInAnyOrder(
                        FakeExpenses.RESTAURANT_DINNER.description(),
                        FakeExpenses.FUEL_UP.description());
    }

    @Test
    void fetchAll_withSearchTerm_returnsMatchingExpenses() {
        repository.create(FakeExpenses.RESTAURANT_DINNER).await().indefinitely();
        repository.create(FakeExpenses.FUEL_UP).await().indefinitely();

        ExpenseFilter filter = ExpenseFilter.builder().searchTerm("dinner").build();
        List<Expense> result = repository.fetchAll(filter).collect().asList().await().indefinitely();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).description()).isEqualTo(FakeExpenses.RESTAURANT_DINNER.description());
    }

    @Test
    void fetchById_whenExpenseExists_returnsExpense() {
        repository.create(FakeExpenses.RESTAURANT_DINNER).await().indefinitely();

        Optional<Expense> result = repository.fetchById(FakeExpenses.RESTAURANT_DINNER.id()).await().indefinitely();

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(FakeExpenses.RESTAURANT_DINNER.id());
        assertThat(result.get().description()).isEqualTo(FakeExpenses.RESTAURANT_DINNER.description());
        assertThat(result.get().value()).isEqualByComparingTo(FakeExpenses.RESTAURANT_DINNER.value());
        assertThat(result.get().type()).isEqualTo(FakeExpenses.RESTAURANT_DINNER.type());
    }

    @Test
    void fetchById_whenExpenseDoesNotExist_returnsEmpty() {
        Optional<Expense> result = repository.fetchById(FakeExpenses.RESTAURANT_DINNER.id()).await().indefinitely();

        assertThat(result).isEmpty();
    }

    @Test
    void fetchById_withInvalidObjectId_returnsEmpty() {
        Optional<Expense> result = repository.fetchById("not-a-valid-object-id").await().indefinitely();

        assertThat(result).isEmpty();
    }

    @Test
    void create_persistsExpenseAndReturnsIt() {
        Expense created = repository.create(FakeExpenses.RESTAURANT_DINNER).await().indefinitely();

        assertThat(created.id()).isEqualTo(FakeExpenses.RESTAURANT_DINNER.id());
        assertThat(created.description()).isEqualTo(FakeExpenses.RESTAURANT_DINNER.description());
        assertThat(created.value()).isEqualByComparingTo(FakeExpenses.RESTAURANT_DINNER.value());
        assertThat(created.type()).isEqualTo(FakeExpenses.RESTAURANT_DINNER.type());
        assertThat(created.source()).isEqualTo(FakeExpenses.RESTAURANT_DINNER.source());
        assertThat(created.createdAt()).isNotNull();
        assertThat(created.updatedAt()).isNotNull();
    }

    @Test
    void create_expenseIsRetrievableAfterPersistence() {
        repository.create(FakeExpenses.FUEL_UP).await().indefinitely();

        Optional<Expense> fetched = repository.fetchById(FakeExpenses.FUEL_UP.id()).await().indefinitely();

        assertThat(fetched).isPresent();
        assertThat(fetched.get().description()).isEqualTo(FakeExpenses.FUEL_UP.description());
    }

    @Test
    void update_whenExpenseExists_returnsUpdatedExpense() {
        repository.create(FakeExpenses.RESTAURANT_DINNER).await().indefinitely();

        Expense updates = FakeExpenses.RESTAURANT_DINNER.toBuilder()
                .description("Updated dinner")
                .value(new BigDecimal("55.00"))
                .build();

        Optional<Expense> result = repository.update(FakeExpenses.RESTAURANT_DINNER.id(), updates).await().indefinitely();

        assertThat(result).isPresent();
        assertThat(result.get().description()).isEqualTo("Updated dinner");
        assertThat(result.get().value()).isEqualByComparingTo(new BigDecimal("55.00"));
    }

    @Test
    void update_whenExpenseExists_persistsChanges() {
        repository.create(FakeExpenses.RESTAURANT_DINNER).await().indefinitely();

        Expense updates = FakeExpenses.RESTAURANT_DINNER.toBuilder()
                .description("Updated dinner")
                .value(new BigDecimal("55.00"))
                .build();

        repository.update(FakeExpenses.RESTAURANT_DINNER.id(), updates).await().indefinitely();

        Optional<Expense> fetched = repository.fetchById(FakeExpenses.RESTAURANT_DINNER.id()).await().indefinitely();
        assertThat(fetched).isPresent();
        assertThat(fetched.get().description()).isEqualTo("Updated dinner");
        assertThat(fetched.get().value()).isEqualByComparingTo(new BigDecimal("55.00"));
    }

    @Test
    void update_whenExpenseDoesNotExist_returnsEmpty() {
        Expense updates = FakeExpenses.RESTAURANT_DINNER.toBuilder()
                .description("Updated dinner")
                .build();

        Optional<Expense> result = repository.update(FakeExpenses.RESTAURANT_DINNER.id(), updates).await().indefinitely();

        assertThat(result).isEmpty();
    }

    @Test
    void update_withInvalidObjectId_returnsEmpty() {
        Expense updates = FakeExpenses.RESTAURANT_DINNER.toBuilder()
                .description("Updated dinner")
                .build();

        Optional<Expense> result = repository.update("not-a-valid-object-id", updates).await().indefinitely();

        assertThat(result).isEmpty();
    }
}
