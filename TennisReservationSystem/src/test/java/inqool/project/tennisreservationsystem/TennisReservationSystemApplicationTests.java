package inqool.project.tennisreservationsystem;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.post;
import static org.hamcrest.Matchers.equalTo;

import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TennisReservationSystemApplicationTests {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    void contextLoads() {
        post("/api/user?name=John").then().assertThat()
                .body("name", equalTo("John"));
    }

}
