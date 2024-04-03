package inqool.project.tennisreservationsystem.data;

import inqool.project.tennisreservationsystem.api.model.CourtType;
import inqool.project.tennisreservationsystem.api.model.TennisCourt;
import inqool.project.tennisreservationsystem.api.model.User;
import inqool.project.tennisreservationsystem.service.CourtTypeService;
import inqool.project.tennisreservationsystem.service.TennisCourtService;
import inqool.project.tennisreservationsystem.service.UserService;
import inqool.project.tennisreservationsystem.service.provider.ServiceProvider;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Random;

import java.util.ArrayList;
import java.util.List;

/**
 * Test data for the system.
 * Can be also used for initialisation of courtTypes and tennisCourts.
 *
 * @author Boris Lukačovič
 */

public abstract class TestData {

    private static final List<String> COURT_TYPE_NAMES = List.of("Grass", "Clay", "Hard", "Artificial");
    private static final SessionFactory SESSION_FACTORY = ServiceProvider.getSessionFactory();
    private static final TennisCourtService TENNIS_COURT_SERVICE = new TennisCourtService();
    private static final CourtTypeService COURT_TYPE_SERVICE = new CourtTypeService();
    private static final UserService USER_SERVICE = new UserService();

    public static void initData() {
        try (Session session = SESSION_FACTORY.openSession()) {
            session.beginTransaction();


            List<CourtType> courtTypes = initCourtTypes(2);
            initTennisCourts(courtTypes, 4);

            session.getTransaction().commit();
        }
    }

    public static List<TennisCourt> initTennisCourts(List<CourtType> courtTypes, int numberOfCourts) {
        List<TennisCourt> result = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < numberOfCourts; i++) {
            int index = rand.nextInt(courtTypes.size());

            TennisCourt tennisCourt = TENNIS_COURT_SERVICE.insertEntity(new TennisCourt(courtTypes.get(index)));
            result.add(tennisCourt);
        }

        return result;
    }

    public static List<CourtType> initCourtTypes(int numberOfTypes) {
        List<CourtType> result = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < numberOfTypes; i++) {
            int index = rand.nextInt(COURT_TYPE_NAMES.size());
            float hourlyRentPrice = rand.nextFloat(5, 25);

            CourtType courtType = COURT_TYPE_SERVICE.insertEntity(new CourtType(COURT_TYPE_NAMES.get(index), hourlyRentPrice));
            result.add(courtType);
        }

        return result;
    }

    public static List<User> initUsers(List<String> names) {
        List<User> result = new ArrayList<>();
        Random rand = new Random();

        for (String name : names) {
            String phoneNum = String.valueOf(rand.nextInt(1000, 9999999));
            User user = USER_SERVICE.insertEntity(new User(phoneNum, name));
            result.add(user);
        }

        return result;
    }

}
