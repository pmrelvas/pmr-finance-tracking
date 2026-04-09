package pt.pmr.financetracking.integration;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.pmr.financetracking.domain.entities.fake.FakeCategories;
import pt.pmr.financetracking.domain.repositories.CategoryRepository;
import pt.pmr.financetracking.domain.repositories.ExpenseRepository;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class ExpenseControllerIT {

    @Inject
    ExpenseRepository expenseRepository;

    @Inject
    CategoryRepository categoryRepository;

    private String categoryId;
    private String subCategoryId;

    @BeforeEach
    void setUp() {
        expenseRepository.deleteAll().await().indefinitely();
        categoryRepository.deleteAll().await().indefinitely();

        var category = categoryRepository.create(FakeCategories.FOOD).await().indefinitely();
        categoryId = category.id();
        subCategoryId = category.subCategories().get(0).id();
    }

    private String validPayload() {
        return """
                {
                  "operationDate": "2026-01-15T12:00:00Z",
                  "description": "Dinner at restaurant",
                  "value": 45.50,
                  "type": "DEBIT",
                  "categoryId": "%s",
                  "subCategoryId": "%s",
                  "source": "CREDIT_CARD"
                }
                """.formatted(categoryId, subCategoryId);
    }

    @Test
    void fetchAll_whenEmpty_shouldReturn200WithEmptyList() {
        given()
            .when().get("/api/v1/expenses")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(0));
    }

    @Test
    void fetchAll_whenExpensesExist_shouldReturn200WithAll() {
        given()
            .contentType(ContentType.JSON)
            .body(validPayload())
            .when().post("/api/v1/expenses");

        given()
            .when().get("/api/v1/expenses")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(1))
                .body("[0].description", equalTo("Dinner at restaurant"));
    }

    @Test
    void fetchById_whenFound_shouldReturn200WithExpense() {
        var id = given()
            .contentType(ContentType.JSON)
            .body(validPayload())
            .when().post("/api/v1/expenses")
            .then()
                .statusCode(201)
                .extract().path("id");

        given()
            .when().get("/api/v1/expenses/" + id)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(id))
                .body("description", equalTo("Dinner at restaurant"))
                .body("value", equalTo(45.5f))
                .body("type", equalTo("DEBIT"))
                .body("source", equalTo("CREDIT_CARD"))
                .body("category.id", equalTo(categoryId))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
    }

    @Test
    void fetchById_whenNotFound_shouldReturn404WithErrorBody() {
        var nonExistentId = "507f1f77bcf86cd799439011";

        given()
            .when().get("/api/v1/expenses/" + nonExistentId)
            .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("errorCode", equalTo(4000))
                .body("message", containsString(nonExistentId))
                .body("fields", hasSize(1))
                .body("fields[0].name", equalTo("id"))
                .body("fields[0].value", equalTo(nonExistentId));
    }

    @Test
    void create_withValidPayload_shouldReturn201WithCreatedExpense() {
        given()
            .contentType(ContentType.JSON)
            .body(validPayload())
            .when().post("/api/v1/expenses")
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("description", equalTo("Dinner at restaurant"))
                .body("value", equalTo(45.5f))
                .body("type", equalTo("DEBIT"))
                .body("source", equalTo("CREDIT_CARD"))
                .body("category.id", equalTo(categoryId))
                .body("subCategory.id", equalTo(subCategoryId))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
    }

    @Test
    void create_withoutSubCategory_shouldReturn201() {
        String payload = """
                {
                  "operationDate": "2026-01-15T12:00:00Z",
                  "description": "Generic food expense",
                  "value": 20.00,
                  "type": "DEBIT",
                  "categoryId": "%s",
                  "source": "CASH"
                }
                """.formatted(categoryId);

        given()
            .contentType(ContentType.JSON)
            .body(payload)
            .when().post("/api/v1/expenses")
            .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("subCategory", equalTo(null));
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
                      "categoryId": "%s",
                      "source": "CREDIT_CARD"
                    }
                    """.formatted(categoryId))
            .when().post("/api/v1/expenses")
            .then()
                .statusCode(400);
    }

    @Test
    void create_whenCategoryNotFound_shouldReturn404() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                      "operationDate": "2026-01-15T12:00:00Z",
                      "description": "Test",
                      "value": 10.00,
                      "type": "DEBIT",
                      "categoryId": "507f1f77bcf86cd799439011",
                      "source": "CASH"
                    }
                    """)
            .when().post("/api/v1/expenses")
            .then()
                .statusCode(404)
                .body("errorCode", equalTo(4000));
    }

    @Test
    void update_withValidPayload_shouldReturn200WithUpdatedExpense() {
        var id = given()
            .contentType(ContentType.JSON)
            .body(validPayload())
            .when().post("/api/v1/expenses")
            .then()
                .statusCode(201)
                .extract().path("id");

        String updatePayload = """
                {
                  "operationDate": "2026-01-15T12:00:00Z",
                  "description": "Updated dinner",
                  "value": 55.00,
                  "type": "CREDIT",
                  "categoryId": "%s",
                  "source": "DEBIT_CARD"
                }
                """.formatted(categoryId);

        given()
            .contentType(ContentType.JSON)
            .body(updatePayload)
            .when().put("/api/v1/expenses/" + id)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(id))
                .body("description", equalTo("Updated dinner"))
                .body("value", equalTo(55.0f))
                .body("type", equalTo("CREDIT"))
                .body("source", equalTo("DEBIT_CARD"));
    }

    @Test
    void update_whenNotFound_shouldReturn404WithErrorBody() {
        var nonExistentId = "507f1f77bcf86cd799439011";

        given()
            .contentType(ContentType.JSON)
            .body(validPayload())
            .when().put("/api/v1/expenses/" + nonExistentId)
            .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("errorCode", equalTo(4000))
                .body("message", containsString(nonExistentId));
    }

    @Test
    void update_whenDescriptionIsBlank_shouldReturn400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                      "operationDate": "2026-01-15T12:00:00Z",
                      "description": "",
                      "value": 45.50,
                      "type": "DEBIT",
                      "categoryId": "%s",
                      "source": "CREDIT_CARD"
                    }
                    """.formatted(categoryId))
            .when().put("/api/v1/expenses/507f1f77bcf86cd799439011")
            .then()
                .statusCode(400);
    }
}
