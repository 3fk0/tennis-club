package inqool.project.tennisreservationsystem.service;

import inqool.project.tennisreservationsystem.api.model.User;
import org.springframework.stereotype.Service;

/**
 * Service class for User.
 *
 * @author Boris Lukačovič
 */
@Service
public class UserService extends EntityService<User, String> {

    public UserService() {
        super(User.class);
    }
}
