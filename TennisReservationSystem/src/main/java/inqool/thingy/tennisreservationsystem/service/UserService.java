package inqool.thingy.tennisreservationsystem.service;

import inqool.thingy.tennisreservationsystem.api.model.TennisCourt;
import inqool.thingy.tennisreservationsystem.api.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends EntityService<User> {

    private SessionFactory sessionFactory;

    public UserService() {
        super(User.class);
    }

    public List<User> getAllUsers() {
        List<User> users;

        Class<User> userClass = User.class;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            users = session.createQuery("SELECT u from User u", userClass).list();

            session.getTransaction().commit();
        }

        return users;
    }

    public boolean insertUser(User newUser) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.persist(newUser);

            session.getTransaction().commit();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public List<User> getUser(Long id) {
        List<User> users;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            users = session.createQuery("select user from User user where user.id = :name", User.class)
                    .setParameter("name", id, Long.TYPE)
                    .getResultList();

            session.getTransaction().commit();
        }
        return users;
    }

    public void deleteAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.createMutationQuery("update User user set user.status = :status")
                    .setParameter("status", Status.DELETED)
                    .executeUpdate();

            session.getTransaction().commit();
        }
    }
}
