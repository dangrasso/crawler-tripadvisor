package it.thecrawlers.persistence;
import org.hibernate.*;
import org.hibernate.cfg.*;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

// TODO: Auto-generated Javadoc
/**
 * The Class HibernateUtil.
 */
public class HibernateUtil {

/** The Constant sessionFactory. */
private static SessionFactory sessionFactory;
private static ServiceRegistry serviceRegistry;
    static {
        try {
            sessionFactory = configureSessionFactory();
        } catch (HibernateException hex) {
            hex.printStackTrace();
        }
    }

    /**
     * Gets the session.
     *
     * @return the session
     * @throws HibernateException the hibernate exception
     */
    public static Session getSession()
            throws HibernateException {
        return sessionFactory.openSession();
    }
    private static SessionFactory configureSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();
        configuration.configure("it/thecrawlers/persistence/hibernate.cfg.xml");
        serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();        
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        return sessionFactory;
    }
}