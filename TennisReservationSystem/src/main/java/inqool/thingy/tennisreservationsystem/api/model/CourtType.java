package inqool.thingy.tennisreservationsystem.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CourtType {

    @Id
    private int courtTypeCode;
    private String name;

    private float rentForHour;

    public CourtType() { }

    public CourtType(int courtTypeCode, String name, float rentForHour) {
        this.name = name;
        this.courtTypeCode = courtTypeCode;
        this.rentForHour = rentForHour;
    }

    public String getName() {
        return name;
    }

    public int getCourtTypeCode() {
        return courtTypeCode;
    }

}
