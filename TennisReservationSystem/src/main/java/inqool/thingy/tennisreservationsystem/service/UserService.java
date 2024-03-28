package inqool.thingy.tennisreservationsystem.service;

import inqool.thingy.tennisreservationsystem.api.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService extends EntityService<User, String> {

    public UserService() {
        super(User.class);
    }
}
