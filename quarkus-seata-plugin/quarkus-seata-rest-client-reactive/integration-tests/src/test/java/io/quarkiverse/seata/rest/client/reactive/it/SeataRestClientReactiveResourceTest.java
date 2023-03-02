package io.quarkiverse.seata.rest.client.reactive.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SeataRestClientReactiveResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/seata-rest-client-reactive")
                .then()
                .statusCode(200)
                .body(is("Hello seata-rest-client-reactive"));
    }
}
