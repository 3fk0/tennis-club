package inqool.project.tennisreservationsystem.api.model.comparator;

import inqool.project.tennisreservationsystem.api.model.Reservation;

import java.util.Comparator;

/**
 * A comparator class for the comparison of tennisCourts and Reservation intervals
 * Two reservations are different when they are played on different courts, or they don't overlap.
 *
 * @author Boris Lukačovič
 */
public class ReservationIntervalComparator implements Comparator<Reservation> {
    @Override
    public int compare(Reservation o1, Reservation o2) {
        if (o1.getTennisCourt().getId() < o2.getTennisCourt().getId()) {
            return -1;
        } else if (o1.getTennisCourt().getId() > o2.getTennisCourt().getId()) {
            return 1;
        }

        if (o1.getReservationEnd().isBefore(o2.getReservationStart())) {
            return -1;
        } else if (o2.getReservationEnd().isBefore(o1.getReservationStart())) {
            return 1;
        } else if (o1.getReservationEnd().isEqual(o2.getReservationStart()) &&
                o1.getReservationStart().isBefore(o2.getReservationEnd())) {
            return -1;
        } else if (o2.getReservationEnd().isEqual(o1.getReservationStart()) &&
                o2.getReservationStart().isBefore(o1.getReservationEnd())) {
            return 1;
        }
        return 0;
    }
}
