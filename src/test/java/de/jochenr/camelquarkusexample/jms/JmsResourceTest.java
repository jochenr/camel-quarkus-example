package de.jochenr.camelquarkusexample.jms;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class JmsResourceTest {

    @Test
    public void testJmsEndpoint() {
        given()
          .when().get("/services/jms/queuetest")
          .then()
             .statusCode(200)
             .body(is("success"));
    }

}
