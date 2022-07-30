package io.quarkiverse.seata.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SeataResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/seata")
                .then()
                .statusCode(200)
                .body(is("Hello seata"));
    }
}
