package com.floriantoenjes.analyzer;

import com.floriantoenjes.analyzer.data.CountryDao;
import com.floriantoenjes.analyzer.model.Country;
import com.floriantoenjes.presentation.Menu;
import com.floriantoenjes.util.Prompter;

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
        String codeHead = "Code";
        String nameHead = "Name";
        String alrHead = "Adult Literacy Rate";
        String intUsersHead = "Internet Users";

        // Get greatest length for each value to determine column width
        int lengthCode = codeHead.length();
        int lengthName = nameHead.length();
        int lengthAlr = alrHead.length();
        int lengthIntUsers = intUsersHead.length();

        for (Country country : countries) {
            lengthCode = Math.max(lengthCode, country.getCode().length());
            lengthName = Math.max(lengthName, country.getName().length());
            Double alr = country.getAdultLiteracyRate();
            lengthAlr = (alr != null) ? Math.max(lengthAlr, alr.toString().length()) : lengthAlr;
            Double intUsers = country.getInternetUsers();
            lengthIntUsers = (intUsers != null) ? Math.max(lengthIntUsers, intUsers.toString().length()) : lengthIntUsers;
        }

        // Heading
        System.out.printf("%nCountries%n%n");

        // Table Headings
        System.out.printf(codeHead + " | ");
        System.out.printf("%-" + lengthName + "s | ", nameHead);
        System.out.printf(alrHead + " | ");
        System.out.printf(intUsersHead + "%n");

        // Horizontal Line
        StringBuilder horizontalLine = new StringBuilder();
        for (int i = 0; i < lengthCode + lengthName + lengthAlr + lengthIntUsers + 9; i++) {
            horizontalLine.append("-");
        }
        System.out.println(horizontalLine);

        // Table rows
        for (Country country : countries) {
            String code = String.format("%-" + lengthCode + "s | ", country.getCode());
            String name = String.format("%-" + lengthName + "s | ", country.getName());
            String alr = (country.getAdultLiteracyRate() != null) ? String.format("%" + lengthAlr + ".2f | ", country.getAdultLiteracyRate()) : String.format("%" + lengthAlr + "s | ", "--");
            String intUsers = (country.getInternetUsers() != null) ? String.format("%" + lengthIntUsers + ".2f | ", country.getInternetUsers()) : String.format("%" + lengthIntUsers + "s | ", "--");

            System.out.println(code + name + alr + intUsers);
        }
        System.out.println();
    }

    private static void addCountry() {
        List<Country> countries = dao.getCountryList();
        printCountryTable(countries);
        System.out.println("Enter the data of the new country.");
        String code;
        while (true) {
            final String tmpCode = Prompter.prompt("Code> ").toUpperCase();
            if (tmpCode.equals("quit")) {
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
            final String tmpCode = Prompter.prompt("Code of country to edit> ").toUpperCase();
            if (tmpCode.equals("quit")) {
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
            if (tmpCode.equals("quit")) {
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
