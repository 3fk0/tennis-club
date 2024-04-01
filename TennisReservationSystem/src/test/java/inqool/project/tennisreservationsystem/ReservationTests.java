package inqool.project.tennisreservationsystem;

import inqool.project.tennisreservationsystem.api.model.CourtType;
import inqool.project.tennisreservationsystem.api.model.GameType;
import inqool.project.tennisreservationsystem.api.model.Reservation;
import inqool.project.tennisreservationsystem.api.model.TennisCourt;
import inqool.project.tennisreservationsystem.api.model.User;
import inqool.project.tennisreservationsystem.api.model.comparator.ReservationIntervalComparator;
import inqool.project.tennisreservationsystem.data.TestData;
import inqool.project.tennisreservationsystem.service.CourtTypeService;
import inqool.project.tennisreservationsystem.service.ReservationService;
import inqool.project.tennisreservationsystem.service.TennisCourtService;
import inqool.project.tennisreservationsystem.service.UserService;
import inqool.project.tennisreservationsystem.service.provider.ServiceProvider;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class ReservationTests {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private CourtTypeService courtTypeService;

    @Autowired
    private TennisCourtService tennisCourtService;

    @Autowired
    private UserService userService;
    private final static SessionFactory SESSION_FACTORY = ServiceProvider.getSessionFactory();

    private final ArrayList<TennisCourt> tennisCourts = new ArrayList<>();
    private final ArrayList<CourtType> courtTypes = new ArrayList<>();
    private final ArrayList<User> users = new ArrayList<>();

    private final String[] tableNames = {"RESERVATION", "TENNISCOURTS", "COURTTYPE", "USERS"};
    private final String[] userNames = {"John Wick", "Petty John", "Selam Bovier"};

    @BeforeEach
    public void setUp() {
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
    public void CreationAndDeletionOfInitData() {
        List<CourtType> courtTypes = courtTypeService.getAllEntities();
        List<TennisCourt> tennisCourts = tennisCourtService.getAllEntities();

        assert !courtTypes.isEmpty();
        assert !tennisCourts.isEmpty();
    }

    @Test
    public void CreatingOfSimpleReservation() {
        User testUser = users.get(0);
        TennisCourt tennisCourt = tennisCourts.get(0);
        LocalDateTime localDateTimeStart = LocalDateTime.now();
        LocalDateTime localDateTimeEnd = localDateTimeStart.plusHours(1);

        Reservation reservationTest = new Reservation(testUser, tennisCourt, GameType.SINGLES, localDateTimeStart, localDateTimeEnd);
        Reservation reservationResponse = reservationService.insertEntity(reservationTest);

        assert reservationResponse != null;

        User userRetrieval = userService.getEntity(testUser.getTelephoneNumber());

        assert userRetrieval != null;
        assert userRetrieval.getName().equals(testUser.getName());
    }

    @Test
    public void RetrievalOfReservationsByPhoneNumber() {
        User user1 = users.get(0);
        User user2 = users.get(1);
        TennisCourt tennisCourtOne = tennisCourts.get(0);
        TennisCourt tennisCourtTwo = tennisCourts.get(1);
        LocalDateTime localDateTimeStart = LocalDateTime.now();
        LocalDateTime localDateTimeEnd = localDateTimeStart.plusHours(1);

        Reservation reservationOne = new Reservation(user1, tennisCourtOne, GameType.SINGLES, localDateTimeStart, localDateTimeEnd);
        Reservation reservationTwo = new Reservation(user1, tennisCourtTwo, GameType.DOUBLES, localDateTimeStart, localDateTimeEnd);
        Reservation reservationThree = new Reservation(user2, tennisCourtOne, GameType.SINGLES, localDateTimeStart, localDateTimeEnd);

        reservationService.insertEntity(reservationOne);
        reservationService.insertEntity(reservationTwo);
        reservationService.insertEntity(reservationThree);

        List<Reservation> reservationsList = reservationService.getReservationsByPhoneNumber(user1.getTelephoneNumber());
        assert reservationsList.size() == 2;
        assert reservationsList.get(0).getUser().getName().equals("John Wick");
        assert reservationsList.get(1).getTennisCourt().getId() == tennisCourtTwo.getId();
    }

    @Test
    public void RetrievalOfReservationsByCourtId() {
        User user1 = users.get(0);
        User user2 = users.get(1);
        TennisCourt tennisCourtOne = tennisCourts.get(0);
        TennisCourt tennisCourtTwo = tennisCourts.get(1);
        LocalDateTime localDateTimeStart = LocalDateTime.now();
        LocalDateTime localDateTimeEnd = localDateTimeStart.plusHours(1);

        Reservation reservationOne = new Reservation(user1, tennisCourtOne, GameType.SINGLES, localDateTimeStart, localDateTimeEnd);
        Reservation reservationTwo = new Reservation(user1, tennisCourtTwo, GameType.DOUBLES, localDateTimeStart, localDateTimeEnd);
        Reservation reservationThree = new Reservation(user2, tennisCourtOne, GameType.SINGLES, localDateTimeStart, localDateTimeEnd);

        reservationService.insertEntity(reservationOne);
        reservationService.insertEntity(reservationTwo);
        reservationService.insertEntity(reservationThree);

        List<Reservation> reservations = reservationService.getReservationsByCourtId(tennisCourtTwo.getId());

        assert reservations.size() == 1;
        assert reservations.get(0).getTennisCourt().getCourtType().equals(tennisCourtTwo.getCourtType());
        assert reservations.get(0).getUser().getName().equals(user1.getName());
    }

    @Test
    public void ReservationIntervalComparatorTest() {
        var reservationIntervalComparator = new ReservationIntervalComparator();

        User user1 = users.get(0);
        User user2 = users.get(1);
        TennisCourt tennisCourtOne = tennisCourts.get(0);
        TennisCourt tennisCourtTwo = tennisCourts.get(1);
        LocalDateTime localDateTimeStart = LocalDateTime.now();
        LocalDateTime localDateTimeEnd = localDateTimeStart.plusHours(1);

        Reservation reservationOne = new Reservation(user1, tennisCourtOne, GameType.SINGLES, localDateTimeStart, localDateTimeEnd);
        Reservation reservationTwo = new Reservation(user1, tennisCourtTwo, GameType.DOUBLES, localDateTimeStart, localDateTimeEnd);
        Reservation reservationThree = new Reservation(user2, tennisCourtOne, GameType.SINGLES, localDateTimeStart, localDateTimeEnd);
        Reservation reservationFour = new Reservation(user2, tennisCourtTwo, GameType.SINGLES, localDateTimeStart, localDateTimeEnd);

        assert reservationIntervalComparator.compare(reservationOne, reservationTwo) < 0;
        assert reservationIntervalComparator.compare(reservationOne, reservationThree) == 0;
        assert reservationIntervalComparator.compare(reservationFour, reservationThree) > 0;
    }

}
