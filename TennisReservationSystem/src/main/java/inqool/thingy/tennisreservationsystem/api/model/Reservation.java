package inqool.thingy.tennisreservationsystem.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.SQLRestriction;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@SQLRestriction("status = 'ALIVE'")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "telephoneNumber")
    private User user;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "tennisCourtId")
    private TennisCourt tennisCourt;

    @Enumerated(EnumType.STRING)
    private GameType gameType;

    private LocalDateTime reservationStart;

    private LocalDateTime reservationEnd;
    @Enumerated(EnumType.STRING)
    private final Status status;

    private final LocalDateTime timeOfCreation;

    public Reservation() {
        this.status = Status.ALIVE;
        this.timeOfCreation = LocalDateTime.now();
    }

    public Reservation(User user, TennisCourt tennisCourt, GameType gameType,
                       LocalDateTime reservationStart, LocalDateTime reservationEnd) {
        this();
        this.user = user;
        this.tennisCourt = tennisCourt;
        this.gameType = gameType;
        this.reservationStart = reservationStart;
        this.reservationEnd = reservationEnd;
    }

    public float getPrice() {
        Duration duration = Duration.between(reservationStart, reservationEnd);
        return gameType.getFinalPrice(tennisCourt.getCourtType().getRentForHour(), duration);
    }

    public long getId() {
        return id;
    }

    public TennisCourt getTennisCourt() {
        return tennisCourt;
    }

    public GameType getGameType() {
        return gameType;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getReservationStart() {
        return reservationStart;
    }

    public LocalDateTime getReservationEnd() {
        return reservationEnd;
    }

    @JsonIgnore
    public LocalDateTime getTimeOfCreation() {
        return timeOfCreation;
    }
}
