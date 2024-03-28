package inqool.project.tennisreservationsystem.api.model.comparator;

import inqool.project.tennisreservationsystem.api.model.Reservation;

import java.util.Comparator;

public class ReservationCreationComparator implements Comparator<Reservation> {
    @Override
    public int compare(Reservation o1, Reservation o2) {
        return o1.getTimeOfCreation().compareTo(o2.getTimeOfCreation());
    }
}
