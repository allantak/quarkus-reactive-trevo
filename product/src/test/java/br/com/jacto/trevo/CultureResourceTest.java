package br.com.jacto.trevo;

import br.com.jacto.trevo.dto.culture.CultureForm;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@QuarkusTest
@TestSecurity(authorizationEnabled = false)
public class CultureResourceTest {

    @Test
    public void testGetAllCulturesEndpoint() {
        given()
                .when().get("/culture")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$.size()", equalTo(2));
    }

    @Test
    public void testGetIdCultureEndpoint() {
        given()
                .when().get("/culture/61f25068-1ff6-42c8-8de5-211dceb56449")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("cultureId", equalTo("61f25068-1ff6-42c8-8de5-211dceb56449"))
                .body("cultureName", equalTo("cerejeiras"));
    }

    @Test
    public void testGetIdCultureEndpointNotFound() {
        given()
                .when().get("/culture/nonexistent")
                .then()
                .statusCode(404);
    }


    @Test
    public void testCreateCultureEndPoint() {
        CultureForm cultureForm = new CultureForm();
        cultureForm.setCultureName("Cerejeiras");
        cultureForm.setProductName("Uniport");

        given()
                .contentType(ContentType.JSON)
                .body(cultureForm)
                .when().post("/culture")
                .then()
                .statusCode(201);
    }

    @Test
    public void testCreateCultureEndPointNotFound() {
        CultureForm cultureForm = new CultureForm();
        cultureForm.setCultureName("Cerejeiras");
        cultureForm.setProductName("nonexist");

        given()
                .contentType(ContentType.JSON)
                .body(cultureForm)
                .when().post("/culture")
                .then()
                .statusCode(404);
    }

    @Test
    public void testDeleteCultureEndPoint() {
        given()
                .pathParam("id", "3d85bde6-8e9e-4d5f-b648-be9b9ea77899")
                .when().delete("/culture/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    public void testDeleteCultureEndPointNotFound() {
        given()
                .pathParam("id", "nonexist")
                .when().delete("/culture/{id}")
                .then()
                .statusCode(404);
    }


}
