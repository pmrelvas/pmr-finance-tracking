package pt.pmr.financetracking.integration;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.pmr.financetracking.domain.entities.fake.FakeCategories;
import pt.pmr.financetracking.domain.entities.fake.FakeSubCategories;
import pt.pmr.financetracking.domain.repositories.CategoryRepository;
import pt.pmr.financetracking.domain.repositories.SubCategoryRepository;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class SubCategoryControllerIT {

    private static final String CATEGORY_ID = FakeCategories.FOOD.id();
    private static final String BASE_URL = "/api/v1/categories/" + CATEGORY_ID + "/sub-categories";

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    SubCategoryRepository subCategoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll().await().indefinitely();
        categoryRepository.create(FakeCategories.FOOD).await().indefinitely();
    }

    @Test
    void fetchAll_whenEmpty_shouldReturn200WithEmptyList() {
        subCategoryRepository.deleteAll().await().indefinitely();

        given()
            .when().get(BASE_URL)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(0));
    }

    @Test
    void fetchAll_whenSubCategoriesExist_shouldReturn200WithAll() {
        subCategoryRepository.create(CATEGORY_ID, FakeSubCategories.RESTAURANT).await().indefinitely();
        subCategoryRepository.create(CATEGORY_ID, FakeSubCategories.FUEL).await().indefinitely();

        given()
            .when().get(BASE_URL)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("code", hasItem(FakeSubCategories.RESTAURANT.code()))
                .body("code", hasItem(FakeSubCategories.FUEL.code()));
    }

    @Test
    void fetchAll_withFilterText_shouldReturnPartialCaseInsensitiveMatches() {
        subCategoryRepository.create(CATEGORY_ID, FakeSubCategories.RESTAURANT).await().indefinitely();
        subCategoryRepository.create(CATEGORY_ID, FakeSubCategories.FUEL).await().indefinitely();

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
    void fetchAll_withFilterText_isCaseInsensitive() {
        subCategoryRepository.create(CATEGORY_ID, FakeSubCategories.RESTAURANT).await().indefinitely();

        given()
            .queryParam("searchTerm", "RESTA")
            .when().get(BASE_URL)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(1))
                .body("[0].code", equalTo(FakeSubCategories.RESTAURANT.code()));
    }

    @Test
    void fetchAll_withFilterText_whenNoneMatch_shouldReturnEmptyList() {
        subCategoryRepository.create(CATEGORY_ID, FakeSubCategories.RESTAURANT).await().indefinitely();

        given()
            .queryParam("searchTerm", "xyz_no_match")
            .when().get(BASE_URL)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(0));
    }

    @Test
    void fetchById_whenFound_shouldReturn200WithSubCategory() {
        var created = subCategoryRepository.create(CATEGORY_ID, FakeSubCategories.RESTAURANT).await().indefinitely();

        given()
            .when().get(BASE_URL + "/" + created.id())
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(created.id()))
                .body("code", equalTo(FakeSubCategories.RESTAURANT.code()))
                .body("displayName", equalTo(FakeSubCategories.RESTAURANT.displayName()))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
    }

    @Test
    void fetchById_whenNotFound_shouldReturn404WithErrorBody() {
        var nonExistentId = "507f1f77bcf86cd799439011";

        given()
            .when().get(BASE_URL + "/" + nonExistentId)
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
    void create_withValidPayload_shouldReturn201WithCreatedSubCategory() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "RESTAURANT", "displayName": "Restaurant"}
                    """)
            .when().post(BASE_URL)
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("code", equalTo("RESTAURANT"))
                .body("displayName", equalTo("Restaurant"))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
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
    void create_whenCodeIsMissing_shouldReturn400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"displayName": "Restaurant"}
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
    void update_withValidPayload_shouldReturn200WithUpdatedSubCategory() {
        var created = subCategoryRepository.create(CATEGORY_ID, FakeSubCategories.RESTAURANT).await().indefinitely();

        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "FAST_FOOD", "displayName": "Fast Food"}
                    """)
            .when().put(BASE_URL + "/" + created.id())
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(created.id()))
                .body("code", equalTo("FAST_FOOD"))
                .body("displayName", equalTo("Fast Food"));
    }

    @Test
    void update_whenNotFound_shouldReturn404WithErrorBody() {
        var nonExistentId = "507f1f77bcf86cd799439011";

        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "RESTAURANT", "displayName": "Restaurant"}
                    """)
            .when().put(BASE_URL + "/" + nonExistentId)
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
                    {"code": "", "displayName": "Restaurant"}
                    """)
            .when().put(BASE_URL + "/507f1f77bcf86cd799439011")
            .then()
                .statusCode(400);
    }

    @Test
    void update_whenDisplayNameIsBlank_shouldReturn400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "RESTAURANT", "displayName": ""}
                    """)
            .when().put(BASE_URL + "/507f1f77bcf86cd799439011")
            .then()
                .statusCode(400);
    }
}
