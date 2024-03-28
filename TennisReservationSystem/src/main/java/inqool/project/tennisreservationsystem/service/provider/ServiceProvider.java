package inqool.project.tennisreservationsystem.service.provider;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public abstract class ServiceProvider {
    private static SessionFactory SESSION_FACTORY = null;

    public static SessionFactory getSessionFactory() {
        System.out.println("Tennis Balls");
        if (SESSION_FACTORY == null) {
            try (StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure()
                    .build())
            {
                try {
                    SESSION_FACTORY = new MetadataSources(registry).buildMetadata().buildSessionFactory();
                } catch (Exception e) {
                    StandardServiceRegistryBuilder.destroy(registry);
                    throw e;
                }
            }
        }
        return SESSION_FACTORY;
    }
}
