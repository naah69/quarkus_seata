package io.quarkiverse.seata.dapr.rest.client.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SeataDaprRestClientResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/seata-dapr-rest-client")
                .then()
                .statusCode(200)
                .body(is("Hello seata-dapr-rest-client"));
    }
}
