import com.floriantoenjes.analyzer.model.Country;
import com.floriantoenjes.analyzer.presentation.Menu;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.util.Arrays;
import java.util.List;

public class Application {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static void main(String[] args) {
        Country germany = new Country("deu", "Deutschland");
        Country italy = new Country("ita", "Italia");
        Country cameroon = new Country("cam", "Cameroon");

        save(Arrays.asList(germany, italy, cameroon));

        showMainMenu();
    }

    public static void showMainMenu() {
        Menu mainMenu = new Menu();
        System.out.printf("%nMain Menu%n");
        mainMenu.addMenuItem("List countries", Application::listCountries);
        mainMenu.addMenuItem("Exit", () -> {
            System.out.println("Exiting...");
            System.exit(0);
        });
        mainMenu.show();
    }

    public static void save(List<Country> countries) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        countries.stream().forEach(session::save);
        session.getTransaction().commit();
        session.close();
    }

    public static void listCountries() {
        Session session = sessionFactory.openSession();
        session = sessionFactory.openSession();

        Criteria criteria = session.createCriteria(Country.class);
        List<Country> countries = criteria.list();
        session.close();

        formatData(countries);

        showMainMenu();
    }

    public static void formatData(List<Country> countries) {
        System.out.printf("%nCountries%n%n");
        System.out.printf("Code | ");
        System.out.printf("%-11s | ", "Name");
        System.out.printf("Adult Literacy Rate | ");
        System.out.printf("Internet Users%n");
        System.out.println("----------------------------------------------------");
        countries.stream().forEach((country) -> {
            System.out.printf("%-4s | ", country.getCode());
            System.out.printf(country.getName() + " | ");
            System.out.printf("%-19s | ", country.getAdultLiteracyRate());
            System.out.printf("%-14s",country.getInternetUsers());
            System.out.println();
        });
    }

    public static void delete(Country country) {
        Session session = sessionFactory.openSession();

        session.delete(country);

        session.close();
    }
}
