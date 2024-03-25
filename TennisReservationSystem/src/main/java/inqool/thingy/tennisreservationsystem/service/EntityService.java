package inqool.thingy.tennisreservationsystem.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

/**
 * General CRD service for entities in the database based on their ids.
 *
 * @param <T> represents the type of the object being manipulated with
 */

public class EntityService<T> {

    private final SessionFactory sessionFactory = ServiceProvider.getSessionFactory();
    private final Class<T> tClass;

    public EntityService(Class<T> tClass) {
        this.tClass = tClass;
    }

    public T insertEntity(T newEntity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.persist(newEntity);

            session.getTransaction().commit();
        }

        return newEntity;
    }

    public Optional<T> getEntity(long id) {
        List<T> result;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String sqlQuery = "select e from " + tClass.getName() + " e where e.id = :id";

            result = session.createQuery(sqlQuery, tClass)
                    .setParameter("id", id)
                    .getResultList();

            session.getTransaction().commit();
        }

        return !result.isEmpty() ? Optional.of(result.get(0)) : Optional.empty();
    }

    public void softEntityDelete(long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String sqlQuery = "update " + tClass.getName() + " entity set entity.status = :status where entity.id = :id";
            session.createMutationQuery(sqlQuery)
                    .setParameter("status", Status.DELETED)
                    .setParameter("id", id)
                    .executeUpdate();

            session.getTransaction().commit();
        }
    }

    public void hardEntityDelete(long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String sqlQuery = "delete " + tClass.getName() + " entity where entity.id = :id";
            session.createMutationQuery(sqlQuery)
                    .setParameter("id", id)
                    .executeUpdate();

            session.getTransaction().commit();
        }
    }
}
