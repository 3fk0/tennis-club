package inqool.thingy.tennisreservationsystem.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CourtType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    private float rentForHour;

    public CourtType() { }

    public CourtType(String name, float rentForHour) {
        this.name = name;
        this.rentForHour = rentForHour;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public float getRentForHour() {
        return rentForHour;
    }
}
