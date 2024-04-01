package inqool.project.tennisreservationsystem.api.model.comparator;

import inqool.project.tennisreservationsystem.api.model.Reservation;

import java.util.Comparator;

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
        }
        return 0;
    }
}
