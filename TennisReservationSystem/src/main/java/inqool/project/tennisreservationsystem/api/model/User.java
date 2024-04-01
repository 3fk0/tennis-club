package inqool.project.tennisreservationsystem.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Users")
public class User {

    @Id
    private String telephoneNumber;

    private String name;

    public User() { }

    public User(String telephoneNumber, String name) {
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
