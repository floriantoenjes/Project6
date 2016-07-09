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

        int max_code = 4;
        int max_name = 4;
        int max_alr = 19;
        int max_intUsers = 14;
        for (Country country : countries) {
            max_code = Math.max(max_code, country.getCode().length());
            max_name = Math.max(max_name, country.getName().length());
            max_alr = Math.max(max_alr, Double.toString(country.getAdultLiteracyRate()).length());
            max_intUsers = Math.max(max_intUsers, Double.toString(country.getInternetUsers()).toString().length());
        }

        System.out.printf("%nCountries%n%n");
        System.out.printf("Code | ");
        System.out.printf("%-" + max_name + "s | ", "Name");
        System.out.printf("Adult Literacy Rate | ");
        System.out.printf("Internet Users%n");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < max_code + max_name + max_alr + max_intUsers + 9; i++) {
            sb.append("-");
        }
        System.out.println(sb);

        for (Country country : countries) {
            System.out.printf("%-" + max_code + "s | ", country.getCode());
            System.out.printf("%-" + max_name + "s | ", country.getName());
            System.out.printf("%-" + max_alr + "s | ", country.getAdultLiteracyRate());
            System.out.printf("%-" + max_intUsers + "s", country.getInternetUsers());
            System.out.println();
        }

    }

    public static void delete(Country country) {
        Session session = sessionFactory.openSession();

        session.delete(country);

        session.close();
    }
}
