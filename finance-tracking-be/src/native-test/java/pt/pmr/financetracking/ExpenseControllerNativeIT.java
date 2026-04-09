package pt.pmr.financetracking;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusIntegrationTest
public class ExpenseControllerNativeIT {

    @Test
    void fetchAll_shouldReturn200WithJsonArray() {
        given()
            .when().get("/api/v1/expenses")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
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
    void create_whenDescriptionIsBlank_shouldReturn400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                      "operationDate": "2026-01-15T12:00:00Z",
                      "description": "",
                      "value": 45.50,
                      "type": "DEBIT",
                      "categoryId": "507f1f77bcf86cd799439011",
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
                      "categoryId": "507f1f77bcf86cd799439011",
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
                      "categoryId": "507f1f77bcf86cd799439011",
                      "source": "CREDIT_CARD"
                    }
                    """)
            .when().post("/api/v1/expenses")
            .then()
                .statusCode(400);
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
                      "categoryId": "507f1f77bcf86cd799439011",
                      "source": "CREDIT_CARD"
                    }
                    """)
            .when().put("/api/v1/expenses/507f1f77bcf86cd799439011")
            .then()
                .statusCode(400);
    }
}
