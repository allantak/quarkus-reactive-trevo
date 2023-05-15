package br.com.jacto.trevo;

import br.com.jacto.trevo.model.OrderItem;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class OrderItemResourceTest {

    @Test
    public void testOrderProduct() {
        given()
                .when().get("/order")
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(2));
    }


    @Test
    public void testGetOrderByEmail() {
        String email = "allan@gmail.com";
        given()
                .pathParam("email", email)
                .when().get("/order/{email}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("email", equalTo(email))
                .body("phone", equalTo("999"))
                .body("clientName", equalTo("allan"));
    }

    @Test
    public void testGetOrderByEmailNotFound() {
        String email = "noexist";
        given()
                .pathParam("email", email)
                .when().get("/order/{email}")
                .then()
                .statusCode(404);
    }

    @Test
    public void testCreateOrder() {
        OrderItem orderItem = new OrderItem();
        orderItem.setEmail("test@gmail.com");
        orderItem.setPhone("9999");
        orderItem.setClientName("test");

        given()
                .contentType(ContentType.JSON)
                .body(orderItem)
                .when().post("/order")
                .then()
                .statusCode(201)
                .header("Location", equalTo("http://localhost:8081/product/" + orderItem.getEmail()));
    }


    @Test
    public void testDeleteOrder() {

        OrderItem orderItem = new OrderItem();
        orderItem.setEmail("test2@gmail.com");
        orderItem.setPhone("99999");
        orderItem.setClientName("test2");

        given()
                .contentType(ContentType.JSON)
                .body(orderItem)
                .when().post("/order")
                .then()
                .statusCode(201)
                .header("Location", equalTo("http://localhost:8081/product/" + orderItem.getEmail()));


        given()
                .pathParam("email", orderItem.getEmail())
                .when().delete("/order/{email}")
                .then()
                .statusCode(204);
    }

    @Test
    public void testDeleteOrderNotFound() {
        given()
                .pathParam("email", "noexist")
                .when().delete("/order/{email}")
                .then()
                .statusCode(404);
    }


}