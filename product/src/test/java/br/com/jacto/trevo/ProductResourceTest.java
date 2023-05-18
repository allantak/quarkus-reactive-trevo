package br.com.jacto.trevo;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@QuarkusTest
@TestSecurity(authorizationEnabled = false)
public class ProductResourceTest {

    @Test
    public void testListProduct() {
        given()
                .when().get("/product")
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(1));
    }

    @Test
    public void testGetProductNameEndpoint() {
        given()
                .pathParam("productName", "Uniport")
                .when().get("/product/{productName}")
                .then()
                .statusCode(200)
                .body("productName", equalTo("Uniport"))
                .body("description", equalTo("Central de operações"))
                .body("areaSize", equalTo(200.0F));
    }

    @Test
    public void testGetProductNameEndpointNotFound() {
        given()
                .pathParam("productName", "nonexistent")
                .when().get("/product/{productName}")
                .then()
                .statusCode(404);
    }

    @Test
    public void testCreateProductEndpoint() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"productName\": \"example\", \"description\": \"Example description\", \"areaSize\": \"200\"}")
                .when().post("/product")
                .then()
                .statusCode(201)
                .header("Location", "http://localhost:8081/product/example");
    }

    @Test
    public void testDeleteProductEndpoint() {

        given()
                .contentType(ContentType.JSON)
                .body("{\"productName\": \"example2\", \"description\": \"Example description\", \"areaSize\": \"200\"}")
                .when().post("/product")
                .then()
                .statusCode(201)
                .header("Location", "http://localhost:8081/product/example2");


        given()
                .when().delete("/product/example2")
                .then()
                .statusCode(204);


        given()
                .when().get("/product/example2")
                .then()
                .statusCode(404);
    }

    @Test
    public void testDeleteProductEndpointNotFound() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"productName\": \"example3\", \"description\": \"Example description3\", \"areaSize\": \"200\"}")
                .when().post("/product")
                .then()
                .statusCode(201)
                .header("Location", "http://localhost:8081/product/example3");


        given()
                .when().delete("/product/nonexistent")
                .then()
                .statusCode(404);

    }


}