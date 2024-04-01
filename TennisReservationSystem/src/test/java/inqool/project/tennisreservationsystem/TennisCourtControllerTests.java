package inqool.project.tennisreservationsystem;

import inqool.project.tennisreservationsystem.api.model.CourtType;
import inqool.project.tennisreservationsystem.api.model.TennisCourt;
import inqool.project.tennisreservationsystem.data.TestData;
import inqool.project.tennisreservationsystem.service.provider.ServiceProvider;
import io.restassured.RestAssured;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TennisCourtControllerTests {

    @LocalServerPort
    private int port;
    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    private static final SessionFactory SESSION_FACTORY = ServiceProvider.getSessionFactory();
    private static final List<CourtType> COURT_TYPES = new ArrayList<>();

    @BeforeAll
    public static void courtTypeInit() {
        try (Session session = SESSION_FACTORY.openSession()) {
            session.beginTransaction();

            COURT_TYPES.addAll(TestData.initCourtTypes(4));

            session.getTransaction().commit();
        }
    }

    @Test
    public void InsertNewTennisCourt() {
        TennisCourt tennisCourt = given()
                .param("courtTypeId", COURT_TYPES.get(0).getId())
                .when()
                .post("/api/court")
                .thenReturn().as(TennisCourt.class);

        assert tennisCourt.getCourtType().equals(COURT_TYPES.get(0));

        given()
                .param("courtTypeId", COURT_TYPES.get(0).getId())
                .when()
                .post("/api/court")
                .then().assertThat().statusCode(201);
    }

    @Test
    public void RetrieveTennisCourt() {
        TennisCourt tennisCourt = given()
                .param("courtTypeId", COURT_TYPES.get(1).getId())
                .when()
                .post("api/court")
                .thenReturn().as(TennisCourt.class);

        TennisCourt tennisCourtResponse = given()
                .pathParam("id", tennisCourt.getId())
                .get("/api/court/{id}")
                .thenReturn().as(TennisCourt.class);

        assert tennisCourt.getCourtType().equals(tennisCourtResponse.getCourtType());
    }

    @Test
    public void DeleteTennisCourt() {
        int tennisCourtsNumber = get("/api/courts")
                .thenReturn().as(TennisCourt[].class).length;

        TennisCourt tennisCourt = given()
                .param("courtTypeId", COURT_TYPES.get(2).getId())
                .when()
                .post("/api/court")
                .thenReturn().as(TennisCourt.class);

        given()
                .pathParam("id", tennisCourt.getId())
                .when()
                .delete("/api/court/{id}")
                .then().assertThat().statusCode(204);

        TennisCourt[] tennisCourts = get("/api/courts")
                .thenReturn().as(TennisCourt[].class);

        assert tennisCourts.length == tennisCourtsNumber;
    }

    @Test
    public void UpdateTennisCourt() {
        TennisCourt tennisCourt = given()
                .param("courtTypeId", COURT_TYPES.get(2).getId())
                .when()
                .post("/api/court")
                .thenReturn().as(TennisCourt.class);

        TennisCourt tennisCourtResponse = given()
                .pathParam("id", tennisCourt.getId())
                .queryParam("courtTypeId", COURT_TYPES.get(0).getId())
                .when()
                .put("/api/court/{id}")
                .thenReturn().as(TennisCourt.class);

        assert tennisCourtResponse.getCourtType().equals(COURT_TYPES.get(0));
    }

}
