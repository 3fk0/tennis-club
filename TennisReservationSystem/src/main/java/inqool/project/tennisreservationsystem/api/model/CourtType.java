package inqool.project.tennisreservationsystem.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class CourtType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
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

    public long getId() {
        return id;
    }

    public float getRentForHour() {
        return rentForHour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CourtType courtType = (CourtType) o;
        return rentForHour == courtType.rentForHour &&
                name.equals(courtType.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, rentForHour);
    }
}
