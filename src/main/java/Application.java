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

    public static void showCountries() {
        formatCountries(getCountryList());
        showMainMenu();
    }

    private static void addCountry() {
        List<Country> countries = getCountryList();
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

    public static void editCountry() {
        Country country = getCountryByCode();
        country.setAdultLiteracyRate(Prompter.promptDouble("Adult Literacy Rate> "));
        country.setInternetUsers(Prompter.promptDouble("Internet users> "));
        update(country);
        showMainMenu();
    }

    private static void deleteCountry() {
        Country country = getCountryByCode();
        delete(country);
        showMainMenu();
    }

    public static Country getCountryByCode() {
        String code = Prompter.prompt("Code> ");
        Session session = sessionFactory.openSession();

        Criteria criteria = session.createCriteria(Country.class).add(Restrictions.eq("code", code));
        Country country = (Country) criteria.uniqueResult();

        session.close();
        return country;
    }

    public static List<Country> getCountryList() {
        Session session = sessionFactory.openSession();
        session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(Country.class);
        List<Country> countries = criteria.list();
        session.close();
        return countries;
    }

    public static void formatCountries(List<Country> countries) {
        String code = "Code";
        String name = "Name";
        String alr = "Adult Literacy Rate";
        String intUsers = "Internet Users";
        int max_code = code.length();
        int max_name = name.length();
        int max_alr = alr.length();
        int max_intUsers = intUsers.length();
        for (Country country : countries) {
            max_code = Math.max(max_code, country.getCode().length());
            max_name = Math.max(max_name, country.getName().length());
            max_alr = Math.max(max_alr, Double.toString(country.getAdultLiteracyRate()).length());
            max_intUsers = Math.max(max_intUsers, Double.toString(country.getInternetUsers()).toString().length());
        }

        // Heading
        System.out.printf("%nCountries%n%n");
        // Table Headings
        System.out.printf(code + " | ");
        System.out.printf("%-" + max_name + "s | ", name);
        System.out.printf(alr + " | ");
        System.out.printf(intUsers + "%n");

        StringBuilder horizontalLine = new StringBuilder();
        for (int i = 0; i < max_code + max_name + max_alr + max_intUsers + 9; i++) {
            horizontalLine.append("-");
        }
        System.out.println(horizontalLine);

        // Sort countries by code
        countries.sort( (c1, c2) -> c1.getCode().compareToIgnoreCase(c2.getCode()));

        // Table rows
        for (Country country : countries) {
            System.out.printf("%-" + max_code + "s | ", country.getCode());
            System.out.printf("%-" + max_name + "s | ", country.getName());
            System.out.printf("%-" + max_alr + "s | ", country.getAdultLiteracyRate());
            System.out.printf("%-" + max_intUsers + "s", country.getInternetUsers());
            System.out.println();
        }
    }


    public static Session startTransaction() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        return session;
    }

    public static void finishTransaction(Session session) {
        session.getTransaction().commit();
        session.close();
    }

    public static void save(Country... countries) {
        Session session = startTransaction();
        Arrays.stream(countries).forEach(session::save);
        finishTransaction(session);
    }

    public static void update(Country... countries) {
        Session session = startTransaction();
        Arrays.stream(countries).forEach(session::update);
        finishTransaction(session);
    }

    public static void delete(Country... countries) {
        Session session = startTransaction();
        Arrays.stream(countries).forEach(session::delete);
        finishTransaction(session);
    }
}
