package inqool.project.tennisreservationsystem.service;

import inqool.project.tennisreservationsystem.service.provider.ServiceProvider;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * General CRUD services for entities in the database based on their
 * non-composite primary keys - ids.
 *
 * @param <T> represents the type of the object being manipulated with
 * @param <ID> represents the type of the primary key of the objects being manipulated with
 *
 * @author Boris Lukačovič
 */

public abstract class EntityService<T, ID> {

    private final SessionFactory sessionFactory = ServiceProvider.getSessionFactory();
    private final Class<T> tClass;


    public EntityService(Class<T> tClass) {
        this.tClass = tClass;
    }

    public T insertEntity(T newEntity) {
        T result;
        ID id;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.persist(newEntity);
            session.flush();
            id = (ID) session.getIdentifier(newEntity);

            session.getTransaction().commit();
        }

        result = getEntity(id);

        return result;
    }

    public T getEntity(ID id) {
        T result;

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            result = session.find(tClass, id);

            session.getTransaction().commit();
        }

        return result;
    }

    public List<T> getAllEntities() {
        List<T> result;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String sqlQuery = "select e from " + tClass.getName() + " e";

            result = session.createQuery(sqlQuery, tClass)
                    .getResultList();

            session.getTransaction().commit();
        }

        return result;
    }

    public void softEntityDelete(ID id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String sqlQuery = "update " + tClass.getName() + " entity set entity.deletedAt = :status where entity.id = :id";
            session.createMutationQuery(sqlQuery)
                    .setParameter("status", LocalDateTime.now())
                    .setParameter("id", id)
                    .executeUpdate();

            session.getTransaction().commit();
        }
    }

    public T updateEntity(T newEntity) {
        T result;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            result = session.merge(newEntity);

            session.getTransaction().commit();
        }
        return result;
    }

    public boolean hasEntity(ID id) {
        boolean result;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            result = session.find(tClass, id) != null;

            session.getTransaction().commit();
        }

        return result;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
