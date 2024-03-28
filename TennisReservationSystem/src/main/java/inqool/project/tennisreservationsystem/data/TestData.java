package inqool.project.tennisreservationsystem.data;

import inqool.project.tennisreservationsystem.api.model.CourtType;
import inqool.project.tennisreservationsystem.api.model.TennisCourt;
import inqool.project.tennisreservationsystem.service.provider.ServiceProvider;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import java.util.Random;

import java.util.ArrayList;
import java.util.List;

public abstract class TestData {

    private static final List<String> COURT_TYPE_NAMES = List.of("Grass", "Clay", "Hard", "Artificial");

    public static void initData() {
        SessionFactory sessionFactory = ServiceProvider.getSessionFactory();

        List<CourtType> courtTypes = getRandomCourtType(2);
        List<TennisCourt> tennisCourts = getTestCourts(courtTypes, 4);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            courtTypes.forEach(session::persist);
            tennisCourts.forEach(session::persist);

            session.getTransaction().commit();
        }
    }

    public static List<TennisCourt> getTestCourts(List<CourtType> courtTypes, int numberOfCourts) {
        List<TennisCourt> result = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < numberOfCourts; i++) {
            int index = rand.nextInt(courtTypes.size());
            result.add(new TennisCourt(courtTypes.get(index)));
        }

        return result;
    }

    public static List<CourtType> getRandomCourtType(int numberOfTypes) {
        List<CourtType> result = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < numberOfTypes; i++) {
            int index = rand.nextInt(COURT_TYPE_NAMES.size());
            float hourlyRentPrice = rand.nextFloat(5, 25);

            result.add(new CourtType(COURT_TYPE_NAMES.get(index), hourlyRentPrice));
        }

        return result;
    }

}
