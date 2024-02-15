package com.CreateAPI.WebApplication;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations="classpath:application.properties")
class WebApplicationTests {

    @LocalServerPort
    private int port;

    @BeforeAll
    public static void setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Test
    public void testCreateUserAndGetUserInfo() {


        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body("{ \"username\": \"brettlee2@gmail.com\", \"password\": \"abc123\", \"firstName\": \"Steyn\", \"lastName\": \"Dale\" }")
                .when()
                .post("/v1/user")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // Get user info
        given()
                .port(port)
                .auth().preemptive().basic("brettlee2@gmail.com", "abc123")
                .when()
                .get("/v1/user/self")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("username", equalTo("brettlee2@gmail.com"))
                .body("firstName", equalTo("Steyn"))
                .body("lastName", equalTo("Dale"));
        given()
                .port(port)
                .when()
                .get("/healthz")
                .then()
                .statusCode(HttpStatus.OK.value());
    }
    @Test
    public void testUpdateUserAndGetUserInfo() {

        String plainTextPassword = "DaleS@858";
        String hashedPassword = passwordEncoder.encode(plainTextPassword);

        given()
                .port(port)
                .auth().preemptive().basic("brettlee2@gmail.com", "abc123")
                .contentType(ContentType.JSON)
                .body("{ \"password\": \"" + hashedPassword + "\", \"firstName\": \"Steyn Updated\", \"lastName\": \"Dale Updated\" }")
                .when()
                .put("/v1/user/self")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .port(port)
                .auth().preemptive().basic("brettlee2@gmail.com", hashedPassword)
                .when()
                .get("/v1/user/self")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("username", equalTo("brettlee2@gmail.com"))
                .body("firstName", equalTo("Steyn Updated"))
                .body("lastName", equalTo("Dale Updated"));
    }
}



