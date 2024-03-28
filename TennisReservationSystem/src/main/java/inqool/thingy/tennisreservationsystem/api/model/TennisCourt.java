package inqool.thingy.tennisreservationsystem.api.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "TennisCourts")
@SQLRestriction("status = 'ALIVE'")
public class TennisCourt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "courtTypeId")
    private CourtType courtType;

    @Enumerated(EnumType.STRING)
    private final Status status;

    public TennisCourt() {
        this.status = Status.ALIVE;
    }

    public TennisCourt(CourtType courtType) {
        this();
        this.courtType = courtType;
    }

    public long getId() {
        return id;
    }

    public CourtType getCourtType() {
        return courtType;
    }

    public void setCourtType(CourtType courtType) {
        this.courtType = courtType;
    }
}
