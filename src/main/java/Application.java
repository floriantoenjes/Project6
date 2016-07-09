import com.floriantoenjes.analyzer.model.Country;
import com.floriantoenjes.analyzer.presentation.Menu;
import com.floriantoenjes.analyzer.util.Prompter;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.criterion.Restrictions;
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
        Country france = new Country("fra", "France");

        save(germany, italy, cameroon, france);

        italy.setInternetUsers(500.33);
        update(italy);

        showMainMenu();
    }

    public static void showMainMenu() {
        System.out.printf("%nMain Menu%n");
        Menu mainMenu = new Menu();
        mainMenu.addMenuItem("List countries", Application::showCountries);
        mainMenu.addMenuItem("Edit country", Application::editCountry);
        mainMenu.addMenuItem("Add country", Application::addCountry);
        mainMenu.addMenuItem("Delete country", Application::deleteCountry);
        mainMenu.addMenuItem("Exit", () -> {
            System.out.println("Exiting...");
            System.exit(0);
        });
        mainMenu.show();
    }

    private static void addCountry() {
        List<Country> countries = listCountries();
        String code;
        while (true) {
            final String tmpCode = Prompter.prompt("Code> ");
            if (countries.stream().noneMatch(c -> c.getCode().equals(tmpCode))) {
                code = tmpCode;
                break;
            } else {
                System.out.println("A country with this code already exists!");
            }
        }
        String name = Prompter.prompt("Name> ");
        double adultLiteracyRate = Prompter.promptDouble("Adult Literacy Rate> ");
        double internetUsers = Prompter.promptDouble("Internet users > ");

        save(new Country(code, name, adultLiteracyRate, internetUsers));
        showMainMenu();
    }

    private static void deleteCountry() {
        Country country = getCountryByCode();
        delete(country);
        showMainMenu();
    }

    public static void save(Country... countries) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Arrays.stream(countries).forEach(session::save);
        session.getTransaction().commit();
        session.close();
    }

    public static List<Country> listCountries() {
        Session session = sessionFactory.openSession();
        session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(Country.class);
        List<Country> countries = criteria.list();
        session.close();
        return countries;
    }

    public static void showCountries() {
        formatCountries(listCountries());
        showMainMenu();
    }

    public static Country getCountryByCode() {
        String code = Prompter.prompt("Code> ");
        Session session = sessionFactory.openSession();
        session = sessionFactory.openSession();

        Criteria criteria = session.createCriteria(Country.class).add(Restrictions.eq("code", code));
        Country country = (Country) criteria.uniqueResult();

        session.close();
        return country;
    }

    public static void formatCountries(List<Country> countries) {

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

        // Sort countries by code
        countries.sort((c1, c2) -> {
            return c1.getCode().compareToIgnoreCase(c2.getCode());
        });

        for (Country country : countries) {
            System.out.printf("%-" + max_code + "s | ", country.getCode());
            System.out.printf("%-" + max_name + "s | ", country.getName());
            System.out.printf("%-" + max_alr + "s | ", country.getAdultLiteracyRate());
            System.out.printf("%-" + max_intUsers + "s", country.getInternetUsers());
            System.out.println();
        }
    }

    public static void editCountry() {
        Country country = getCountryByCode();
        country.setAdultLiteracyRate(Prompter.promptDouble("Adult Literacy Rate> "));
        country.setInternetUsers(Prompter.promptDouble("Internet users> "));
        update(country);

        showMainMenu();
    }

    public static void update(Country country) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.update(country);
        session.getTransaction().commit();
        session.close();
    }

    public static void delete(Country country) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(country);
        session.getTransaction().commit();
        session.close();
    }
}
