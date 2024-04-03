package inqool.project.tennisreservationsystem.api.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

/**
 * The class represents the entity of a Tennis Court
 *
 * @author Boris Lukačovič
 */

@Entity
@Table(name = "TennisCourts")
@SQLRestriction("deletedAt IS NULL")
public class TennisCourt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "courtTypeId")
    private CourtType courtType;

    private LocalDateTime deletedAt;

    public TennisCourt() { }

    public TennisCourt(CourtType courtType) {
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
