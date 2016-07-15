package com.floriantoenjes.analyzer;

import com.floriantoenjes.analyzer.data.CountryDao;
import com.floriantoenjes.analyzer.model.Country;
import com.floriantoenjes.presentation.Menu;
import com.floriantoenjes.util.Prompter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.util.ArrayList;
import java.util.List;

public class Application {
    private static final CountryDao dao = new CountryDao();

    public static void main(String[] args) {
        showMainMenu();
    }

    private static void showMainMenu() {
        System.out.println("Main Menu");
        Menu mainMenu = new Menu();
        mainMenu.addMenuItem("List countries", Application::showCountries);
        mainMenu.addMenuItem("Show statistics", Application::showStatistics);
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
        printCountryTable(dao.getCountryList());
        showMainMenu();
    }

    private static void printCountryTable(List<Country> countries) {

        // Table headers
        String codeHeading = "Code";
        String nameHeading = "Name";
        String alrHeading = "Adult Literacy Rate";
        String intUsersHeading = "Internet Users";

        // Get greatest length for each value to determine column width
        int codeLength = codeHeading.length();
        int nameLength = nameHeading.length();
        int alrLength = alrHeading.length();
        int intUsersLength = intUsersHeading.length();

        for (Country country : countries) {
            codeLength = Math.max(codeLength, country.getCode().length());
            nameLength = Math.max(nameLength, country.getName().length());

            Double alr = country.getAdultLiteracyRate();
            alrLength = (alr != null) ? Math.max(alrLength, alr.toString().length()) : alrLength;

            Double intUsers = country.getInternetUsers();
            intUsersLength = (intUsers != null) ? Math.max(intUsersLength, intUsers.toString().length()) : intUsersLength;
        }

        // Heading
        System.out.printf("%nCountries%n%n");

        // Table Headings
        System.out.printf(codeHeading + " | ");
        System.out.printf("%-" + nameLength + "s | ", nameHeading);
        System.out.printf(alrHeading + " | ");
        System.out.printf(intUsersHeading + "%n");

        // Horizontal Line
        StringBuilder horizontalLine = new StringBuilder();
        for (int i = 0; i < codeLength + nameLength + alrLength + intUsersLength + 9; i++) {
            horizontalLine.append("-");
        }
        System.out.println(horizontalLine);

        // Table rows
        for (Country country : countries) {
            String code = String.format("%-" + codeLength + "s | ", country.getCode());
            String name = String.format("%-" + nameLength + "s | ", country.getName());

            String alr = (country.getAdultLiteracyRate() != null) ? String.format("%" + alrLength + ".2f | ",
                    country.getAdultLiteracyRate()) : String.format("%" + alrLength + "s | ", "--");

            String intUsers = (country.getInternetUsers() != null) ? String.format("%" + intUsersLength + ".2f | ",
                    country.getInternetUsers()) : String.format("%" + intUsersLength + "s | ", "--");

            System.out.println(code + name + alr + intUsers);
        }
        System.out.println();
    }

    private static void showStatistics() {
        List<Country> countries = dao.getCountryList();
        double minAlr = 0;
        double maxAlr = 0;

        double minIntUsers = 0;
        double maxIntUsers = 0;

        for (Country country : countries) {
            Double thisAlr = country.getAdultLiteracyRate();
            if (thisAlr == null) {
                continue;
            } else if (minAlr == 0) {
                minAlr = thisAlr;
            }
            minAlr = Math.min(minAlr, thisAlr);
            maxAlr = Math.max(maxAlr, thisAlr);
        }

        for (Country country : countries) {
            Double thisIntUsers = country.getInternetUsers();
            if (thisIntUsers == null) {
                continue;
            } else if (minIntUsers == 0) {
                minIntUsers = thisIntUsers;
            }
            minIntUsers = Math.min(minIntUsers, thisIntUsers);
            maxIntUsers = Math.max(maxIntUsers, thisIntUsers);
        }

        List<Double> alrCorrelation = new ArrayList<>();
        List<Double> intUsersCorrelation = new ArrayList<>();
        for (Country country : countries) {
            Double thisAlr = country.getAdultLiteracyRate();
            Double thisIntUsers = country.getInternetUsers();
            if (thisAlr != null && thisIntUsers != null) {
                alrCorrelation.add(thisAlr);
                intUsersCorrelation.add(thisIntUsers);
            }
        }

        double correlation = new PearsonsCorrelation().correlation(
                ArrayUtils.toPrimitive(alrCorrelation.toArray(new Double[alrCorrelation.size()])),
                ArrayUtils.toPrimitive(intUsersCorrelation.toArray(new Double[intUsersCorrelation.size()]))
        );

        System.out.printf("Statistics%n%n");

        System.out.printf("Minimum Adult Literacy Rate: %.2f%% %n", minAlr);
        System.out.printf("Maximum Adult Literacy Rate: %.2f%% %n", maxAlr);
        System.out.println();

        System.out.printf("Minimum Rate of Internet Users: %.2f%% %n", minIntUsers);
        System.out.printf("Maximum Rate of Internet Users: %.2f%% %n", maxIntUsers);
        System.out.println();

        System.out.printf("Correlation Coefficient: %.2f %n", correlation);
        System.out.println();

        showMainMenu();
    }

    private static void addCountry() {
        List<Country> countries = dao.getCountryList();
        printCountryTable(countries);
        System.out.println("Enter the data of the new country.");
        String code;
        while (true) {
            final String tmpCode = Prompter.prompt("Code> ").toUpperCase();
            if (tmpCode.equals("QUIT")) {
                showMainMenu();
                return;
            } else if (countries.stream().noneMatch(c -> c.getCode().equals(tmpCode))) {
                code = tmpCode;
                break;
            } else {
                System.out.println("A country with this code already exists!");
            }
        }
        String name = Prompter.prompt("Name> ");
        double adultLiteracyRate = Prompter.promptDouble("Adult Literacy Rate> ");
        double internetUsers = Prompter.promptDouble("Internet users > ");

        dao.save(new Country(code, name, adultLiteracyRate, internetUsers));

        System.out.println();
        showMainMenu();
    }

    private static void editCountry() {
        List<Country> countries = dao.getCountryList();
        printCountryTable(countries);
        String code;
        while (true) {
            final String tmpCode = Prompter.prompt("Code of Country to Edit> ").toUpperCase();
            if (tmpCode.equals("QUIT")) {
                showMainMenu();
                return;
            } else if (countries.stream().anyMatch(c -> c.getCode().equals(tmpCode))) {
                code = tmpCode;
                break;
            } else {
                System.out.println("No country with this code exists!");
            }
        }
        Country country = countries.stream().filter(c -> c.getCode().equals(code)).findFirst().get();
        country.setAdultLiteracyRate(Prompter.promptDouble("Adult Literacy Rate> "));
        country.setInternetUsers(Prompter.promptDouble("Internet users> "));

        dao.update(country);

        System.out.println();
        showMainMenu();
    }

    private static void deleteCountry() {
        List<Country> countries = dao.getCountryList();
        printCountryTable(countries);
        String code;
        while (true) {
            final String tmpCode = Prompter.prompt("Code> ").toUpperCase();
            if (tmpCode.equals("QUIT")) {
                showMainMenu();
                return;
            } else if (countries.stream().anyMatch(c -> c.getCode().equals(tmpCode))) {
                code = tmpCode;
                break;
            } else {
                System.out.println("No country with this code exists!");
            }
        }
        Country country = countries.stream().filter(c -> c.getCode().equals(code)).findFirst().get();

        dao.delete(country);

        System.out.println();
        showMainMenu();
    }
}
