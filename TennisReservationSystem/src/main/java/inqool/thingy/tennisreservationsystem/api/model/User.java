package inqool.thingy.tennisreservationsystem.api.model;

import inqool.thingy.tennisreservationsystem.service.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "Users")
@SQLRestriction("status = 'ALIVE'")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    Status status;
    private String name;

    public User() {}

    public User(String name) {
        this.name = name;
        this.status = Status.ALIVE;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }
}
