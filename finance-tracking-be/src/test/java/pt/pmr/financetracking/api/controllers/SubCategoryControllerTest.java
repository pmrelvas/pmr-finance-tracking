package pt.pmr.financetracking.api.controllers;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import pt.pmr.financetracking.domain.entities.SubCategory;
import pt.pmr.financetracking.domain.entities.fake.FakeCategories;
import pt.pmr.financetracking.domain.entities.fake.FakeSubCategories;
import pt.pmr.financetracking.domain.exceptions.EntityNotFoundException;
import pt.pmr.financetracking.domain.usecases.CreateSubCategoryUseCase;
import pt.pmr.financetracking.domain.usecases.ReadSubCategoryUseCase;
import pt.pmr.financetracking.domain.usecases.UpdateSubCategoryUseCase;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@QuarkusTest
class SubCategoryControllerTest {

    private static final String BASE_URL = "/api/v1/categories/" + FakeCategories.FOOD.id() + "/sub-categories";

    @InjectMock
    ReadSubCategoryUseCase readSubCategoryUseCase;

    @InjectMock
    CreateSubCategoryUseCase createSubCategoryUseCase;

    @InjectMock
    UpdateSubCategoryUseCase updateSubCategoryUseCase;

    @Test
    void fetchAll_shouldReturn200WithAllSubCategories() {
        when(readSubCategoryUseCase.executeFindAll(eq(FakeCategories.FOOD.id()), any()))
                .thenReturn(Multi.createFrom().items(FakeSubCategories.RESTAURANT, FakeSubCategories.FUEL));

        given()
            .when().get(BASE_URL)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(2))
                .body("[0].id", equalTo(FakeSubCategories.RESTAURANT.id()))
                .body("[0].code", equalTo(FakeSubCategories.RESTAURANT.code()))
                .body("[0].displayName", equalTo(FakeSubCategories.RESTAURANT.displayName()))
                .body("[1].id", equalTo(FakeSubCategories.FUEL.id()))
                .body("[1].code", equalTo(FakeSubCategories.FUEL.code()))
                .body("[1].displayName", equalTo(FakeSubCategories.FUEL.displayName()));
    }

    @Test
    void fetchAll_whenEmpty_shouldReturn200WithEmptyList() {
        when(readSubCategoryUseCase.executeFindAll(eq(FakeCategories.FOOD.id()), any()))
                .thenReturn(Multi.createFrom().empty());

        given()
            .when().get(BASE_URL)
            .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    void fetchAll_withFilterText_shouldReturn200WithMatchingSubCategories() {
        when(readSubCategoryUseCase.executeFindAll(eq(FakeCategories.FOOD.id()), any()))
                .thenReturn(Multi.createFrom().items(FakeSubCategories.RESTAURANT));

        given()
            .queryParam("searchTerm", "resta")
            .when().get(BASE_URL)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(1))
                .body("[0].code", equalTo(FakeSubCategories.RESTAURANT.code()));
    }

    @Test
    void fetchById_whenFound_shouldReturn200() {
        when(readSubCategoryUseCase.executeFindById(FakeCategories.FOOD.id(), FakeSubCategories.RESTAURANT.id()))
                .thenReturn(Uni.createFrom().item(FakeSubCategories.RESTAURANT));

        given()
            .when().get(BASE_URL + "/" + FakeSubCategories.RESTAURANT.id())
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(FakeSubCategories.RESTAURANT.id()))
                .body("code", equalTo(FakeSubCategories.RESTAURANT.code()))
                .body("displayName", equalTo(FakeSubCategories.RESTAURANT.displayName()));
    }

    @Test
    void fetchById_whenNotFound_shouldReturn404WithErrorCode() {
        var unknownId = "unknown-id";
        when(readSubCategoryUseCase.executeFindById(eq(FakeCategories.FOOD.id()), eq(unknownId)))
                .thenReturn(Uni.createFrom().failure(EntityNotFoundException.buildForId(SubCategory.class, unknownId)));

        given()
            .when().get(BASE_URL + "/" + unknownId)
            .then()
                .statusCode(404)
                .body("errorCode", equalTo(4000))
                .body("message", containsString(unknownId));
    }

    @Test
    void create_withValidPayload_shouldReturn201() {
        when(createSubCategoryUseCase.execute(eq(FakeCategories.FOOD.id()), any()))
                .thenReturn(Uni.createFrom().item(FakeSubCategories.RESTAURANT));

        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "RESTAURANT", "displayName": "Restaurant"}
                    """)
            .when().post(BASE_URL)
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", equalTo(FakeSubCategories.RESTAURANT.id()))
                .body("code", equalTo(FakeSubCategories.RESTAURANT.code()))
                .body("displayName", equalTo(FakeSubCategories.RESTAURANT.displayName()));
    }

    @Test
    void create_whenCodeIsBlank_shouldReturn400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "", "displayName": "Restaurant"}
                    """)
            .when().post(BASE_URL)
            .then()
                .statusCode(400);
    }

    @Test
    void create_whenDisplayNameIsBlank_shouldReturn400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "RESTAURANT", "displayName": ""}
                    """)
            .when().post(BASE_URL)
            .then()
                .statusCode(400);
    }

    @Test
    void update_withValidPayload_shouldReturn200() {
        when(updateSubCategoryUseCase.execute(eq(FakeCategories.FOOD.id()), eq(FakeSubCategories.RESTAURANT.id()), any()))
                .thenReturn(Uni.createFrom().item(FakeSubCategories.RESTAURANT));

        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "RESTAURANT", "displayName": "Restaurant"}
                    """)
            .when().put(BASE_URL + "/" + FakeSubCategories.RESTAURANT.id())
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(FakeSubCategories.RESTAURANT.id()))
                .body("code", equalTo(FakeSubCategories.RESTAURANT.code()))
                .body("displayName", equalTo(FakeSubCategories.RESTAURANT.displayName()));
    }

    @Test
    void update_whenNotFound_shouldReturn404WithErrorCode() {
        var unknownId = "unknown-id";
        when(updateSubCategoryUseCase.execute(eq(FakeCategories.FOOD.id()), eq(unknownId), any()))
                .thenReturn(Uni.createFrom().failure(EntityNotFoundException.buildForId(SubCategory.class, unknownId)));

        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "RESTAURANT", "displayName": "Restaurant"}
                    """)
            .when().put(BASE_URL + "/" + unknownId)
            .then()
                .statusCode(404)
                .body("errorCode", equalTo(4000))
                .body("message", containsString(unknownId));
    }
}
