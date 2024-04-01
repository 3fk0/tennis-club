package inqool.project.tennisreservationsystem;

import inqool.project.tennisreservationsystem.api.controllers.CourtTypeController;
import inqool.project.tennisreservationsystem.api.model.CourtType;
import inqool.project.tennisreservationsystem.data.TestData;
import inqool.project.tennisreservationsystem.service.provider.ServiceProvider;
import io.restassured.RestAssured;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CourtTypeControllerTests {

    @LocalServerPort
    private int port;

    private static final SessionFactory SESSION_FACTORY = ServiceProvider.getSessionFactory();
    @Autowired
    private CourtTypeController courtTypeController;
    private final static ArrayList<CourtType> COURT_TYPES = new ArrayList<>();
    private final String[] tableNames = {"COURTTYPE"};

    @BeforeAll
    public static void setUp() {
        try (Session session = SESSION_FACTORY.openSession()) {
            session.beginTransaction();

            COURT_TYPES.addAll(TestData.initCourtTypes(4));

            session.getTransaction().commit();
        }
    }

    @BeforeEach
    public void setUpPort() {
        courtTypeController.reload();
        RestAssured.port = port;
    }

    @Test
    public void RetrieveCourtTypeByID() {
        CourtType courtType = COURT_TYPES.get(0);

        CourtType courtTypeResponse = given()
                .pathParam("id", courtType.getId())
                .when().get("/api/courtType/{id}")
                .thenReturn().as(CourtType.class);

        assert courtTypeResponse.equals(courtType);
    }
}
