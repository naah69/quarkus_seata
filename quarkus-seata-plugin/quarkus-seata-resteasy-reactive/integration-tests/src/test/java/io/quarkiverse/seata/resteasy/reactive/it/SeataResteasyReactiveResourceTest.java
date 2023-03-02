package io.quarkiverse.seata.resteasy.reactive.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SeataResteasyReactiveResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/seata-resteasy-reactive")
                .then()
                .statusCode(200)
                .body(is("Hello seata-resteasy-reactive"));
    }
}
