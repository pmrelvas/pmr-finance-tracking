package pt.pmr.financetracking.integration;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.pmr.financetracking.domain.entities.fake.FakeCategories;
import pt.pmr.financetracking.domain.repositories.CategoryRepository;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class CategoryControllerIT {

    @Inject
    CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll().await().indefinitely();
    }

    @Test
    void fetchAll_whenEmpty_shouldReturn200WithEmptyList() {
        given()
            .when().get("/api/v1/categories")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(0));
    }

    @Test
    void fetchAll_whenCategoriesExist_shouldReturn200WithAll() {
        categoryRepository.create(FakeCategories.FOOD).await().indefinitely();
        categoryRepository.create(FakeCategories.CAR).await().indefinitely();

        given()
            .when().get("/api/v1/categories")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(2))
                .body("code", hasItem(FakeCategories.FOOD.code()))
                .body("code", hasItem(FakeCategories.CAR.code()))
                .body("displayName", hasItem(FakeCategories.FOOD.displayName()))
                .body("displayName", hasItem(FakeCategories.CAR.displayName()));
    }

    @Test
    void fetchById_whenFound_shouldReturn200WithCategory() {
        var created = categoryRepository.create(FakeCategories.FOOD).await().indefinitely();

        given()
            .when().get("/api/v1/categories/" + created.id())
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(created.id()))
                .body("code", equalTo(FakeCategories.FOOD.code()))
                .body("displayName", equalTo(FakeCategories.FOOD.displayName()))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
    }

    @Test
    void fetchById_whenNotFound_shouldReturn404WithErrorBody() {
        var nonExistentId = "507f1f77bcf86cd799439011";

        given()
            .when().get("/api/v1/categories/" + nonExistentId)
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
    void create_withValidPayload_shouldReturn201WithCreatedCategory() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "FOOD", "displayName": "Food"}
                    """)
            .when().post("/api/v1/categories")
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("code", equalTo("FOOD"))
                .body("displayName", equalTo("Food"))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
    }

    @Test
    void create_whenCodeIsBlank_shouldReturn400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "", "displayName": "Food"}
                    """)
            .when().post("/api/v1/categories")
            .then()
                .statusCode(400);
    }

    @Test
    void create_whenCodeIsMissing_shouldReturn400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"displayName": "Food"}
                    """)
            .when().post("/api/v1/categories")
            .then()
                .statusCode(400);
    }

    @Test
    void create_whenDisplayNameIsBlank_shouldReturn400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "FOOD", "displayName": ""}
                    """)
            .when().post("/api/v1/categories")
            .then()
                .statusCode(400);
    }

    @Test
    void create_whenDisplayNameIsMissing_shouldReturn400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "FOOD"}
                    """)
            .when().post("/api/v1/categories")
            .then()
                .statusCode(400);
    }

    @Test
    void update_withValidPayload_shouldReturn200WithUpdatedCategory() {
        var created = categoryRepository.create(FakeCategories.FOOD).await().indefinitely();

        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "FOOD_UPDATED", "displayName": "Food Updated"}
                    """)
            .when().put("/api/v1/categories/" + created.id())
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(created.id()))
                .body("code", equalTo("FOOD_UPDATED"))
                .body("displayName", equalTo("Food Updated"));
    }

    @Test
    void update_whenNotFound_shouldReturn404WithErrorBody() {
        var nonExistentId = "507f1f77bcf86cd799439011";

        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "FOOD", "displayName": "Food"}
                    """)
            .when().put("/api/v1/categories/" + nonExistentId)
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
    void update_whenCodeIsBlank_shouldReturn400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "", "displayName": "Food"}
                    """)
            .when().put("/api/v1/categories/507f1f77bcf86cd799439011")
            .then()
                .statusCode(400);
    }

    @Test
    void update_whenDisplayNameIsBlank_shouldReturn400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "FOOD", "displayName": ""}
                    """)
            .when().put("/api/v1/categories/507f1f77bcf86cd799439011")
            .then()
                .statusCode(400);
    }
}
