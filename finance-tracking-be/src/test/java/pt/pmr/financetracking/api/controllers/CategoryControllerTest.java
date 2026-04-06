package pt.pmr.financetracking.api.controllers;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import pt.pmr.financetracking.domain.entities.Category;
import pt.pmr.financetracking.domain.entities.fake.FakeCategories;
import pt.pmr.financetracking.domain.exceptions.EntityNotFoundException;
import pt.pmr.financetracking.domain.usecases.CreateCategoryUseCase;
import pt.pmr.financetracking.domain.usecases.ReadCategoryUseCase;
import pt.pmr.financetracking.domain.usecases.UpdateCategoryUseCase;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@QuarkusTest
class CategoryControllerTest {

    @InjectMock
    ReadCategoryUseCase readCategoryUseCase;

    @InjectMock
    CreateCategoryUseCase createCategoryUseCase;

    @InjectMock
    UpdateCategoryUseCase updateCategoryUseCase;

    @Test
    void fetchAll_shouldReturn200WithAllCategories() {
        when(readCategoryUseCase.executeFindAll())
                .thenReturn(Multi.createFrom().items(FakeCategories.FOOD, FakeCategories.CAR));

        given()
            .when().get("/api/v1/categories")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(2))
                .body("[0].id", equalTo(FakeCategories.FOOD.id()))
                .body("[0].code", equalTo(FakeCategories.FOOD.code()))
                .body("[0].displayName", equalTo(FakeCategories.FOOD.displayName()))
                .body("[1].id", equalTo(FakeCategories.CAR.id()))
                .body("[1].code", equalTo(FakeCategories.CAR.code()))
                .body("[1].displayName", equalTo(FakeCategories.CAR.displayName()));
    }

    @Test
    void fetchAll_whenEmpty_shouldReturn200WithEmptyList() {
        when(readCategoryUseCase.executeFindAll())
                .thenReturn(Multi.createFrom().empty());

        given()
            .when().get("/api/v1/categories")
            .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    void fetchById_whenFound_shouldReturn200() {
        when(readCategoryUseCase.executeFindById(FakeCategories.FOOD.id()))
                .thenReturn(Uni.createFrom().item(FakeCategories.FOOD));

        given()
            .when().get("/api/v1/categories/" + FakeCategories.FOOD.id())
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(FakeCategories.FOOD.id()))
                .body("code", equalTo(FakeCategories.FOOD.code()))
                .body("displayName", equalTo(FakeCategories.FOOD.displayName()));
    }

    @Test
    void fetchById_whenNotFound_shouldReturn404WithErrorCode() {
        var unknownId = "unknown-id";
        when(readCategoryUseCase.executeFindById(unknownId))
                .thenReturn(Uni.createFrom().failure(EntityNotFoundException.buildForId(Category.class, unknownId)));

        given()
            .when().get("/api/v1/categories/" + unknownId)
            .then()
                .statusCode(404)
                .body("errorCode", equalTo(4000))
                .body("message", containsString(unknownId));
    }

    @Test
    void create_withValidPayload_shouldReturn201() {
        when(createCategoryUseCase.execute(any()))
                .thenReturn(Uni.createFrom().item(FakeCategories.FOOD));

        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "FOOD", "displayName": "Food"}
                    """)
            .when().post("/api/v1/categories")
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", equalTo(FakeCategories.FOOD.id()))
                .body("code", equalTo(FakeCategories.FOOD.code()))
                .body("displayName", equalTo(FakeCategories.FOOD.displayName()));
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
    void update_withValidPayload_shouldReturn200() {
        when(updateCategoryUseCase.execute(eq(FakeCategories.FOOD.id()), any()))
                .thenReturn(Uni.createFrom().item(FakeCategories.FOOD));

        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "FOOD", "displayName": "Food"}
                    """)
            .when().put("/api/v1/categories/" + FakeCategories.FOOD.id())
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(FakeCategories.FOOD.id()))
                .body("code", equalTo(FakeCategories.FOOD.code()))
                .body("displayName", equalTo(FakeCategories.FOOD.displayName()));
    }

    @Test
    void update_whenNotFound_shouldReturn404WithErrorCode() {
        var unknownId = "unknown-id";
        when(updateCategoryUseCase.execute(eq(unknownId), any()))
                .thenReturn(Uni.createFrom().failure(EntityNotFoundException.buildForId(Category.class, unknownId)));

        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "FOOD", "displayName": "Food"}
                    """)
            .when().put("/api/v1/categories/" + unknownId)
            .then()
                .statusCode(404)
                .body("errorCode", equalTo(4000))
                .body("message", containsString(unknownId));
    }
}
