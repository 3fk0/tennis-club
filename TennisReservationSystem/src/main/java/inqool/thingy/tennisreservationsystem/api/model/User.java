package inqool.thingy.tennisreservationsystem.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "Users")
@SQLRestriction("status = 'ALIVE'")
public class User {

    @Id
    private String telephoneNumber;

    private String name;
    @Enumerated(EnumType.STRING)
    private final Status status;

    public User() {
        this.status = Status.ALIVE;
    }

    public User(String telephoneNumber, String name) {
        this();
        this.telephoneNumber = telephoneNumber;
        this.name = name;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }
}
