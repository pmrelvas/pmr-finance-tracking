package pt.pmr.financetracking.api.controllers;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import pt.pmr.financetracking.domain.entities.Expense;
import pt.pmr.financetracking.domain.entities.ExpenseFilter;
import pt.pmr.financetracking.domain.entities.fake.FakeExpenses;
import pt.pmr.financetracking.domain.exceptions.EntityNotFoundException;
import pt.pmr.financetracking.domain.usecases.CreateExpenseUseCase;
import pt.pmr.financetracking.domain.usecases.ReadExpenseUseCase;
import pt.pmr.financetracking.domain.usecases.UpdateExpenseUseCase;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@QuarkusTest
class ExpenseControllerTest {

    @InjectMock
    ReadExpenseUseCase readExpenseUseCase;

    @InjectMock
    CreateExpenseUseCase createExpenseUseCase;

    @InjectMock
    UpdateExpenseUseCase updateExpenseUseCase;

    private static final String VALID_PAYLOAD = """
            {
              "operationDate": "2026-01-15T12:00:00Z",
              "description": "Dinner at restaurant",
              "value": 45.50,
              "type": "DEBIT",
              "categoryId": "69cd166d634d73e295cacf2b",
              "subCategoryId": "69cd16a0634d73e295cacf2e",
              "source": "CREDIT_CARD"
            }
            """;

    @Test
    void fetchAll_shouldReturn200WithAllExpenses() {
        ExpenseFilter filter = ExpenseFilter.builder().build();

        when(readExpenseUseCase.executeFindAll(filter))
                .thenReturn(Multi.createFrom().items(FakeExpenses.RESTAURANT_DINNER, FakeExpenses.FUEL_UP));

        given()
            .when().get("/api/v1/expenses")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(2))
                .body("[0].id", equalTo(FakeExpenses.RESTAURANT_DINNER.id()))
                .body("[0].description", equalTo(FakeExpenses.RESTAURANT_DINNER.description()))
                .body("[1].id", equalTo(FakeExpenses.FUEL_UP.id()))
                .body("[1].description", equalTo(FakeExpenses.FUEL_UP.description()));
    }

    @Test
    void fetchAll_whenEmpty_shouldReturn200WithEmptyList() {
        ExpenseFilter filter = ExpenseFilter.builder().build();

        when(readExpenseUseCase.executeFindAll(filter))
                .thenReturn(Multi.createFrom().empty());

        given()
            .when().get("/api/v1/expenses")
            .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    void fetchAll_withSearchTerm_shouldPassFilterToUseCase() {
        ExpenseFilter filter = ExpenseFilter.builder().searchTerm("dinner").build();

        when(readExpenseUseCase.executeFindAll(filter))
                .thenReturn(Multi.createFrom().items(FakeExpenses.RESTAURANT_DINNER));

        given()
            .when().get("/api/v1/expenses?searchTerm=dinner")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(1))
                .body("[0].id", equalTo(FakeExpenses.RESTAURANT_DINNER.id()));
    }

    @Test
    void fetchById_whenFound_shouldReturn200() {
        when(readExpenseUseCase.executeFindById(FakeExpenses.RESTAURANT_DINNER.id()))
                .thenReturn(Uni.createFrom().item(FakeExpenses.RESTAURANT_DINNER));

        given()
            .when().get("/api/v1/expenses/" + FakeExpenses.RESTAURANT_DINNER.id())
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(FakeExpenses.RESTAURANT_DINNER.id()))
                .body("description", equalTo(FakeExpenses.RESTAURANT_DINNER.description()));
    }

    @Test
    void fetchById_whenNotFound_shouldReturn404WithErrorCode() {
        var unknownId = "unknown-id";
        when(readExpenseUseCase.executeFindById(unknownId))
                .thenReturn(Uni.createFrom().failure(EntityNotFoundException.buildForId(Expense.class, unknownId)));

        given()
            .when().get("/api/v1/expenses/" + unknownId)
            .then()
                .statusCode(404)
                .body("errorCode", equalTo(4000))
                .body("message", containsString(unknownId));
    }

    @Test
    void create_withValidPayload_shouldReturn201() {
        when(createExpenseUseCase.execute(any()))
                .thenReturn(Uni.createFrom().item(FakeExpenses.RESTAURANT_DINNER));

        given()
            .contentType(ContentType.JSON)
            .body(VALID_PAYLOAD)
            .when().post("/api/v1/expenses")
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", equalTo(FakeExpenses.RESTAURANT_DINNER.id()))
                .body("description", equalTo(FakeExpenses.RESTAURANT_DINNER.description()));
    }

    @Test
    void create_whenDescriptionIsBlank_shouldReturn400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                      "operationDate": "2026-01-15T12:00:00Z",
                      "description": "",
                      "value": 45.50,
                      "type": "DEBIT",
                      "categoryId": "69cd166d634d73e295cacf2b",
                      "source": "CREDIT_CARD"
                    }
                    """)
            .when().post("/api/v1/expenses")
            .then()
                .statusCode(400);
    }

    @Test
    void create_whenValueIsNull_shouldReturn400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                      "operationDate": "2026-01-15T12:00:00Z",
                      "description": "Test",
                      "type": "DEBIT",
                      "categoryId": "69cd166d634d73e295cacf2b",
                      "source": "CREDIT_CARD"
                    }
                    """)
            .when().post("/api/v1/expenses")
            .then()
                .statusCode(400);
    }

    @Test
    void create_whenTypeIsNull_shouldReturn400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                      "operationDate": "2026-01-15T12:00:00Z",
                      "description": "Test",
                      "value": 10.00,
                      "categoryId": "69cd166d634d73e295cacf2b",
                      "source": "CREDIT_CARD"
                    }
                    """)
            .when().post("/api/v1/expenses")
            .then()
                .statusCode(400);
    }

    @Test
    void create_whenCategoryIdIsBlank_shouldReturn400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                      "operationDate": "2026-01-15T12:00:00Z",
                      "description": "Test",
                      "value": 10.00,
                      "type": "DEBIT",
                      "categoryId": "",
                      "source": "CREDIT_CARD"
                    }
                    """)
            .when().post("/api/v1/expenses")
            .then()
                .statusCode(400);
    }

    @Test
    void update_withValidPayload_shouldReturn200() {
        when(updateExpenseUseCase.execute(eq(FakeExpenses.RESTAURANT_DINNER.id()), any()))
                .thenReturn(Uni.createFrom().item(FakeExpenses.RESTAURANT_DINNER));

        given()
            .contentType(ContentType.JSON)
            .body(VALID_PAYLOAD)
            .when().put("/api/v1/expenses/" + FakeExpenses.RESTAURANT_DINNER.id())
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(FakeExpenses.RESTAURANT_DINNER.id()))
                .body("description", equalTo(FakeExpenses.RESTAURANT_DINNER.description()));
    }

    @Test
    void update_whenNotFound_shouldReturn404WithErrorCode() {
        var unknownId = "unknown-id";
        when(updateExpenseUseCase.execute(eq(unknownId), any()))
                .thenReturn(Uni.createFrom().failure(EntityNotFoundException.buildForId(Expense.class, unknownId)));

        given()
            .contentType(ContentType.JSON)
            .body(VALID_PAYLOAD)
            .when().put("/api/v1/expenses/" + unknownId)
            .then()
                .statusCode(404)
                .body("errorCode", equalTo(4000))
                .body("message", containsString(unknownId));
    }
}
