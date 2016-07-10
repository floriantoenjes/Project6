import com.floriantoenjes.analyzer.model.Country;
import com.floriantoenjes.analyzer.presentation.Menu;
import com.floriantoenjes.analyzer.util.Prompter;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.criterion.Order;
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
        // Mock Data
        Country germany = new Country("deu", "Deutschland");
        Country italy = new Country("ita", "Italia");
        Country cameroon = new Country("cam", "Cameroon");
        Country france = new Country("fra", "France");
        Country brazil = new Country.CountryBuilder("bra", "Brazil")
                .withAdultLiteracyRate(355829)
                .withInternetUsers(518239382)
                .build();

        save(germany, italy, cameroon, france, brazil);

        italy.setInternetUsers(500.33);
        update(italy);
        // End Mock Data

        // Show Menu
        showMainMenu();
    }

    private static void showMainMenu() {
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

    private static void showCountries() {
        printCountryTable(getCountryList());
        showMainMenu();
    }

    private static void addCountry() {
        List<Country> countries = getCountryList();
        printCountryTable(countries);
        String code;
        while (true) {
            final String tmpCode = Prompter.prompt("Code (exit to quit)> ");
            if (tmpCode.equals("exit")) {
                showMainMenu();
                return;
            } else if (countries.stream().noneMatch(c -> c.getCode().equals(tmpCode))) {
                code = tmpCode;
                break;
            } else {
                System.out.println("A country with this code already exists! Type");
            }
        }
        String name = Prompter.prompt("Name> ");
        double adultLiteracyRate = Prompter.promptDouble("Adult Literacy Rate> ");
        double internetUsers = Prompter.promptDouble("Internet users > ");

        save(new Country(code, name, adultLiteracyRate, internetUsers));

        showMainMenu();
    }

    private static void editCountry() {
        List<Country> countries = getCountryList();
        printCountryTable(countries);
        String code;
        while (true) {
            final String tmpCode = Prompter.prompt("Code> ");
            if (countries.stream().anyMatch(c -> c.getCode().equals(tmpCode))) {
                code = tmpCode;
                break;
            } else {
                System.out.println("No country with this code exists!");
            }
        }
        Country country = countries.stream().filter(c -> c.getCode().equals(code)).findFirst().get();
        country.setAdultLiteracyRate(Prompter.promptDouble("Adult Literacy Rate> "));
        country.setInternetUsers(Prompter.promptDouble("Internet users> "));

        update(country);

        showMainMenu();
    }

    private static void deleteCountry() {
        List<Country> countries = getCountryList();
        printCountryTable(countries);
        String code;
        while (true) {
            final String tmpCode = Prompter.prompt("Code> ");
            if (countries.stream().anyMatch(c -> c.getCode().equals(tmpCode))) {
                code = tmpCode;
                break;
            } else {
                System.out.println("No country with this code exists!");
            }
        }
        Country country = countries.stream().filter(c -> c.getCode().equals(code)).findFirst().get();

        delete(country);

        showMainMenu();
    }

    /*  private static Country getCountryByCode(String code) {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(Country.class).add(Restrictions.eq("code", code));
        Country country = (Country) criteria.uniqueResult();
        session.close();
        return country;
    }*/

    @SuppressWarnings("unchecked")
    private static List<Country> getCountryList() {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(Country.class).addOrder(Order.asc("code"));
        List<Country> countries = criteria.list();
        session.close();
        return countries;
    }

    private static void printCountryTable(List<Country> countries) {
        String code = "Code";
        String name = "Name";
        String alr = "Adult Literacy Rate";
        String intUsers = "Internet Users";


        // Get greatest length for each value to determine table space
        int lengthCode = code.length();
        int lengthName = name.length();
        int lengthAlr = alr.length();
        int lengthIntUsers = intUsers.length();

        for (Country country : countries) {
            lengthCode = Math.max(lengthCode, country.getCode().length());
            lengthName = Math.max(lengthName, country.getName().length());
            lengthAlr = Math.max(lengthAlr, Double.toString(country.getAdultLiteracyRate()).length());
            lengthIntUsers = Math.max(lengthIntUsers, Double.toString(country.getInternetUsers()).length());
        }

        // Heading
        System.out.printf("%nCountries%n%n");

        // Table Headings
        System.out.printf(code + " | ");
        System.out.printf("%-" + lengthName + "s | ", name);
        System.out.printf(alr + " | ");
        System.out.printf(intUsers + "%n");

        // Horizontal Line
        StringBuilder horizontalLine = new StringBuilder();
        for (int i = 0; i < lengthCode + lengthName + lengthAlr + lengthIntUsers + 9; i++) {
            horizontalLine.append("-");
        }
        System.out.println(horizontalLine);

        // Table rows
        for (Country country : countries) {
            System.out.printf("%-" + lengthCode + "s | ", country.getCode());
            System.out.printf("%-" + lengthName + "s | ", country.getName());
            System.out.printf("%-" + lengthAlr + "s | ", country.getAdultLiteracyRate());
            System.out.printf("%-" + lengthIntUsers + "s", country.getInternetUsers());
            System.out.println();
        }
    }


    private static Session startTransaction() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        return session;
    }

    private static void finishTransaction(Session session) {
        session.getTransaction().commit();
        session.close();
    }

    private static void save(Country... countries) {
        Session session = startTransaction();
        Arrays.stream(countries).forEach(session::save);
        finishTransaction(session);
    }

    private static void update(Country... countries) {
        Session session = startTransaction();
        Arrays.stream(countries).forEach(session::update);
        finishTransaction(session);
    }

    private static void delete(Country... countries) {
        Session session = startTransaction();
        Arrays.stream(countries).forEach(session::delete);
        finishTransaction(session);
    }
}
