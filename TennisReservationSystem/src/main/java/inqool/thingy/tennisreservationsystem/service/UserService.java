package inqool.thingy.tennisreservationsystem.service;

import inqool.thingy.tennisreservationsystem.api.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private SessionFactory sessionFactory;

    public UserService() {
        System.out.println("Balls");
        try (StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build())
        {
            try {
                sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            } catch (Exception e) {
                StandardServiceRegistryBuilder.destroy(registry);
            }
        }
    }

    public List<User> getAllUsers() {
        List<User> users;

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            users = session.createQuery("SELECT u from User u", User.class).list();

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
}
