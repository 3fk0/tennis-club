package inqool.project.tennisreservationsystem;

import inqool.project.tennisreservationsystem.api.model.CourtType;
import inqool.project.tennisreservationsystem.api.model.GameType;
import inqool.project.tennisreservationsystem.api.model.Reservation;
import inqool.project.tennisreservationsystem.api.model.TennisCourt;
import inqool.project.tennisreservationsystem.api.model.User;
import inqool.project.tennisreservationsystem.data.TestData;
import inqool.project.tennisreservationsystem.service.UserService;
import inqool.project.tennisreservationsystem.service.provider.ServiceProvider;
import io.restassured.RestAssured;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReservationControllerTests {

    @LocalServerPort
    private int port;
    private final static SessionFactory SESSION_FACTORY = ServiceProvider.getSessionFactory();
    @Autowired
    private UserService userService;
    private final ArrayList<TennisCourt> tennisCourts = new ArrayList<>();
    private final ArrayList<CourtType> courtTypes = new ArrayList<>();
    private final ArrayList<User> users = new ArrayList<>();

    private final String[] tableNames = {"RESERVATION", "TENNISCOURTS", "COURTTYPE", "USERS"};
    private final String[] userNames = {"John Wick", "Petty John", "Selam Bovier"};

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        try (Session session = SESSION_FACTORY.openSession()) {
            session.beginTransaction();

            courtTypes.addAll(TestData.initCourtTypes(4));
            tennisCourts.addAll(TestData.initTennisCourts(courtTypes, 4));
            users.addAll(TestData.initUsers(Arrays.stream(userNames).toList()));

            session.getTransaction().commit();
        }
    }

    @AfterEach
    public void restartDB() {
        SessionFactory sessionFactory = ServiceProvider.getSessionFactory();

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            for (String tableName : tableNames) {
                String sqlQuery = "DELETE FROM " + tableName;
                session.createNativeQuery(sqlQuery, User.class).executeUpdate();
            }

            session.getTransaction().commit();
        }

        courtTypes.clear();
        tennisCourts.clear();
        users.clear();
    }

    @Test
    public void InsertNewReservation() {
        User user1 = users.get(0);
        user1.setTelephoneNumber("+421123456789");
        TennisCourt tennisCourtOne = tennisCourts.get(0);
        LocalDateTime localDateTimeStart = LocalDateTime.now();
        LocalDateTime localDateTimeEnd = localDateTimeStart.plusHours(1);
        Reservation reservationOne = new Reservation(user1, tennisCourtOne, GameType.SINGLES, localDateTimeStart, localDateTimeEnd);

        given()
                .contentType("application/json")
                .body(reservationOne)
                .when().post("/api/reservation")
                .then().assertThat().statusCode(201);
    }

    @Test
    public void GetInsertedReservation() {
        User user1 = users.get(0);
        user1.setTelephoneNumber("+421123456789");
        TennisCourt tennisCourtOne = tennisCourts.get(0);
        LocalDateTime localDateTimeStart = LocalDateTime.now();
        LocalDateTime localDateTimeEnd = localDateTimeStart.plusHours(1);
        Reservation reservationOne = new Reservation(user1, tennisCourtOne, GameType.SINGLES, localDateTimeStart, localDateTimeEnd);

        given()
                .contentType("application/json")
                .body(reservationOne)
                .when().post("/api/reservation")
                .then().assertThat().statusCode(201);

        Reservation[] reservations = given()
                .pathParam("id", tennisCourtOne.getId())
                .when().get("/api/court/{id}/reservation")
                .thenReturn().as(Reservation[].class);

        assert reservations.length == 1;

        Reservation reservation = given()
                .pathParam("id", reservations[0].getId())
                .when().get("/api/reservation/{id}")
                .thenReturn().as(Reservation.class);

        assert reservation.getUser().getName().equals(reservations[0].getUser().getName());
    }

    @Test
    public void InsertNewReservationWithoutUserInDB() {
        User user1 = new User("+421123456789", "Tester");
        TennisCourt tennisCourtOne = tennisCourts.get(0);
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusHours(1);
        LocalDateTime localDateTimeEnd = localDateTimeStart.plusHours(1);
        Reservation reservationOne = new Reservation(user1, tennisCourtOne, GameType.SINGLES, localDateTimeStart, localDateTimeEnd);

        given()
                .contentType("application/json")
                .body(reservationOne)
                .when().post("/api/reservation")
                .then().assertThat().statusCode(201);

        Reservation[] reservations = given()
                .queryParam("phoneNumber", "+421123456789")
                .queryParam("onlyFuture", true)
                .when().get("/api/reservation")
                .thenReturn().as(Reservation[].class);

        assert reservations.length == 1;
        assert reservations[0].getTennisCourt().getCourtType().equals(tennisCourtOne.getCourtType());

        assert  userService.hasEntity(reservations[0].getUser().getTelephoneNumber());
    }

    @Test
    public void TryToInsertCollisionReservation() {
        User user1 = users.get(0);
        User user2 = users.get(1);
        user1.setTelephoneNumber("+421123456789");
        user2.setTelephoneNumber("+420963852741");
        TennisCourt tennisCourtOne = tennisCourts.get(0);
        LocalDateTime localDateTimeStart = LocalDateTime.now();
        LocalDateTime localDateTimeEnd = localDateTimeStart.plusHours(1);
        Reservation reservationOne = new Reservation(user1, tennisCourtOne, GameType.SINGLES, localDateTimeStart, localDateTimeEnd);
        Reservation reservationTwo = new Reservation(user2, tennisCourtOne, GameType.DOUBLES, localDateTimeStart, localDateTimeEnd);

        given()
                .contentType("application/json")
                .body(reservationOne)
                .when().post("/api/reservation")
                .then().assertThat().statusCode(201);

        given()
                .contentType("application/json")
                .body(reservationTwo)
                .when().post("/api/reservation")
                .then().assertThat().statusCode(400);
    }

    @Test
    public void ReservationsShouldNotCollide() {
        User user1 = users.get(0);
        User user2 = users.get(1);
        user1.setTelephoneNumber("+421123456789");
        user2.setTelephoneNumber("+420963852741");
        TennisCourt tennisCourtOne = tennisCourts.get(0);
        TennisCourt tennisCourtTwo = tennisCourts.get(1);
        LocalDateTime localDateTimeStart = LocalDateTime.now();
        LocalDateTime localDateTimeEnd = localDateTimeStart.plusHours(1);
        Reservation reservationOne = new Reservation(user1, tennisCourtOne, GameType.SINGLES, localDateTimeStart, localDateTimeEnd);
        Reservation reservationTwo = new Reservation(user2, tennisCourtTwo, GameType.DOUBLES, localDateTimeStart, localDateTimeEnd);

        given()
                .contentType("application/json")
                .body(reservationOne)
                .when().post("/api/reservation")
                .then().assertThat().statusCode(201);

        given()
                .contentType("application/json")
                .body(reservationTwo)
                .when().post("/api/reservation")
                .then().assertThat().statusCode(201);
    }

    @Test
    public void GetReservationOnCourt() {
        User user1 = users.get(0);
        User user2 = users.get(1);
        user1.setTelephoneNumber("+421123456789");
        user2.setTelephoneNumber("+420963852741");
        TennisCourt tennisCourtOne = tennisCourts.get(0);
        LocalDateTime localDateTimeStart = LocalDateTime.now();
        LocalDateTime localDateTimeEnd = localDateTimeStart.plusHours(1);
        Reservation reservationOne = new Reservation(user1, tennisCourtOne, GameType.SINGLES, localDateTimeStart, localDateTimeEnd);
        Reservation reservationTwo = new Reservation(user2, tennisCourtOne, GameType.DOUBLES, localDateTimeStart.plusHours(2), localDateTimeEnd.plusHours(2));

        given()
                .contentType("application/json")
                .body(reservationOne)
                .when().post("/api/reservation")
                .then().assertThat().statusCode(201);

        given()
                .contentType("application/json")
                .body(reservationTwo)
                .when().post("/api/reservation")
                .then().assertThat().statusCode(201);

        Reservation[] reservations = given()
                .pathParam("id", tennisCourtOne.getId())
                .when().get("/api/court/{id}/reservation")
                .thenReturn().as(Reservation[].class);

        assert reservations.length == 2;
        assert reservations[0].getUser().getName().equals(user1.getName());
        assert reservations[1].getTennisCourt().getCourtType().equals(tennisCourtOne.getCourtType());
    }

    @Test
    public void UpdateUserInReservation() {
        User user1 = users.get(0);
        User user2 = users.get(1);
        user1.setTelephoneNumber("+421123456789");
        user2.setTelephoneNumber("+420963852741");
        TennisCourt tennisCourtOne = tennisCourts.get(0);
        LocalDateTime localDateTimeStart = LocalDateTime.now();
        LocalDateTime localDateTimeEnd = localDateTimeStart.plusHours(1);
        Reservation reservationOne = new Reservation(user1, tennisCourtOne, GameType.SINGLES, localDateTimeStart, localDateTimeEnd);
        Reservation reservationTwo = new Reservation(user2, tennisCourtOne, GameType.SINGLES, localDateTimeStart, localDateTimeEnd);

        given()
                .contentType("application/json")
                .body(reservationOne)
                .when().post("/api/reservation")
                .then().assertThat().statusCode(201);

        Reservation[] reservations = given()
                .pathParam("id", tennisCourtOne.getId())
                .when().get("/api/court/{id}/reservation")
                .thenReturn().as(Reservation[].class);

        assert reservations.length == 1;
        reservationTwo.setId(reservations[0].getId());

        Reservation reservation = given()
                .contentType("application/json")
                .body(reservationTwo)
                .when().put("/api/reservation")
                .thenReturn().as(Reservation.class);

        assert reservation.getUser().getName().equals(user2.getName());
    }

    @Test
    public void DeleteReservation() {
        User user1 = users.get(0);
        User user2 = users.get(1);
        user1.setTelephoneNumber("+421123456789");
        user2.setTelephoneNumber("+420963852741");
        TennisCourt tennisCourtOne = tennisCourts.get(0);
        LocalDateTime localDateTimeStart = LocalDateTime.now();
        LocalDateTime localDateTimeEnd = localDateTimeStart.plusHours(1);
        Reservation reservationOne = new Reservation(user1, tennisCourtOne, GameType.SINGLES, localDateTimeStart, localDateTimeEnd);
        Reservation reservationTwo = new Reservation(user2, tennisCourtOne, GameType.SINGLES, localDateTimeStart.plusHours(5), localDateTimeEnd.plusHours(5));

        given()
                .contentType("application/json")
                .body(reservationOne)
                .when().post("/api/reservation")
                .then().assertThat().statusCode(201);

        given()
                .contentType("application/json")
                .body(reservationTwo)
                .when().post("/api/reservation")
                .then().assertThat().statusCode(201);

        Reservation[] reservations = given()
                .pathParam("id", tennisCourtOne.getId())
                .when().get("/api/court/{id}/reservation")
                .thenReturn().as(Reservation[].class);

        assert reservations.length == 2;

        given()
                .pathParam("id", reservations[0].getId())
                .when().delete("/api/reservation/{id}")
                .then().assertThat().statusCode(204);


        reservations = given()
                .pathParam("id", tennisCourtOne.getId())
                .when().get("/api/court/{id}/reservation")
                .thenReturn().as(Reservation[].class);

        assert reservations.length == 1;
    }

    @Test
    public void TryToUpdateCollisionReservation() {
        User user1 = users.get(0);
        User user2 = users.get(1);
        user1.setTelephoneNumber("+421123456789");
        user2.setTelephoneNumber("+420963852741");
        TennisCourt tennisCourtOne = tennisCourts.get(0);
        LocalDateTime localDateTimeStart = LocalDateTime.now();
        LocalDateTime localDateTimeEnd = localDateTimeStart.plusHours(1);
        Reservation reservationOne = new Reservation(user1, tennisCourtOne, GameType.SINGLES, localDateTimeStart, localDateTimeEnd);
        Reservation reservationTwo = new Reservation(user2, tennisCourtOne, GameType.DOUBLES, localDateTimeStart, localDateTimeEnd.plusMinutes(30));
        Reservation reservationThree = new Reservation(user1, tennisCourtOne, GameType.SINGLES, localDateTimeEnd.plusMinutes(5), localDateTimeEnd.plusMinutes(25));

        given()
                .contentType("application/json")
                .body(reservationOne)
                .when().post("/api/reservation")
                .then().assertThat().statusCode(201);

        given()
                .contentType("application/json")
                .body(reservationThree)
                .when().post("/api/reservation")
                .then().assertThat().statusCode(201);

        Reservation[] reservations = given()
                .pathParam("id", tennisCourtOne.getId())
                .when().get("/api/court/{id}/reservation")
                .thenReturn().as(Reservation[].class);

        assert reservations.length == 2;
        reservationTwo.setId(reservations[0].getId());

        given()
                .contentType("application/json")
                .body(reservationTwo)
                .when().put("/api/reservation")
                .then().assertThat().statusCode(400);
    }
}
