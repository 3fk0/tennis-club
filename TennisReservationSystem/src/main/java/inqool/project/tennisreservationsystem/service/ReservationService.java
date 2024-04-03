package inqool.project.tennisreservationsystem.service;

import inqool.project.tennisreservationsystem.api.model.Reservation;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for Reservation.
 *
 * @author Boris Lukačovič
 */
@Service
public class ReservationService extends EntityService<Reservation, Long> {
    public ReservationService() {
        super(Reservation.class);
    }

    public List<Reservation> getReservationsByCourtId(long id) {
        List<Reservation> result;
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            String sqlQuery = "select reservation from Reservation reservation " +
                    "where reservation.tennisCourt.id = :id";
            result = session.createQuery(sqlQuery, Reservation.class)
                    .setParameter("id", id)
                    .getResultList();

            session.getTransaction().commit();
        }

        return result;
    }

    public List<Reservation> getReservationsByPhoneNumber(String phoneNumber) {
        List<Reservation> result;
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            String sqlQuery = "select reservation from Reservation reservation " +
                    "where reservation.user.telephoneNumber like :phoneNumber";
            result = session.createQuery(sqlQuery, Reservation.class)
                    .setParameter("phoneNumber", phoneNumber)
                    .getResultList();

            session.getTransaction().commit();
        }

        return result;
    }

}
