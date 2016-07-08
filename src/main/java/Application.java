import com.floriantoenjes.analyzer.model.Country;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.util.List;

public class Application {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static void main(String[] args) {
        Country country = new Country("deu", "Deutschland");

        Session session = sessionFactory.openSession();

        session.beginTransaction();

        session.save(country);

        session.getTransaction().commit();

        session.close();


        session = sessionFactory.openSession();

        Criteria criteria = session.createCriteria(Country.class);

        List<Country> countries = criteria.list();

        session.close();

        countries.forEach(System.out::println);
    }

}
