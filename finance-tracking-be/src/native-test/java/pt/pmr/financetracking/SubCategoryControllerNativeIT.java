package pt.pmr.financetracking;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusIntegrationTest
public class SubCategoryControllerNativeIT {

    private String categoryId;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        categoryId = given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "FOOD", "displayName": "Food"}
                    """)
            .when().post("/api/v1/categories")
            .then()
                .statusCode(201)
                .extract().path("id");

        baseUrl = "/api/v1/categories/" + categoryId + "/sub-categories";
    }

    @Test
    void fetchAll_shouldReturn200WithJsonArray() {
        given()
            .when().get(baseUrl)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void fetchAll_afterCreate_shouldContainCreatedSubCategory() {
        var code = "IT_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        var displayName = "Native IT SubCategory " + code;

        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "%s", "displayName": "%s"}
                    """.formatted(code, displayName))
            .when().post(baseUrl);

        given()
            .when().get(baseUrl)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("code.flatten()", org.hamcrest.Matchers.hasItem(code));
    }

    @Test
    void fetchById_whenFound_shouldReturn200WithSubCategory() {
        var code = "IT_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        var id = given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "%s", "displayName": "Native IT SubCategory"}
                    """.formatted(code))
            .when().post(baseUrl)
            .then()
                .statusCode(201)
                .extract().path("id");

        given()
            .when().get(baseUrl + "/" + id)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(id))
                .body("code", equalTo(code))
                .body("displayName", equalTo("Native IT SubCategory"))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
    }

    @Test
    void fetchById_whenNotFound_shouldReturn404WithErrorBody() {
        var nonExistentId = "507f1f77bcf86cd799439011";

        given()
            .when().get(baseUrl + "/" + nonExistentId)
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
        var code = "IT_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "%s", "displayName": "Native IT SubCategory"}
                    """.formatted(code))
            .when().post(baseUrl)
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("code", equalTo(code))
                .body("displayName", equalTo("Native IT SubCategory"))
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
            .when().post(baseUrl)
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
            .when().post(baseUrl)
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
            .when().post(baseUrl)
            .then()
                .statusCode(400);
    }

    @Test
    void update_withValidPayload_shouldReturn200WithUpdatedSubCategory() {
        var code = "IT_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        var id = given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "%s", "displayName": "Original Name"}
                    """.formatted(code))
            .when().post(baseUrl)
            .then()
                .statusCode(201)
                .extract().path("id");

        var updatedCode = code + "_UPD";

        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "%s", "displayName": "Updated Name"}
                    """.formatted(updatedCode))
            .when().put(baseUrl + "/" + id)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(id))
                .body("code", equalTo(updatedCode))
                .body("displayName", equalTo("Updated Name"));
    }

    @Test
    void update_whenNotFound_shouldReturn404WithErrorBody() {
        var nonExistentId = "507f1f77bcf86cd799439011";

        given()
            .contentType(ContentType.JSON)
            .body("""
                    {"code": "RESTAURANT", "displayName": "Restaurant"}
                    """)
            .when().put(baseUrl + "/" + nonExistentId)
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
            .when().put(baseUrl + "/507f1f77bcf86cd799439011")
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
            .when().put(baseUrl + "/507f1f77bcf86cd799439011")
            .then()
                .statusCode(400);
    }
}
