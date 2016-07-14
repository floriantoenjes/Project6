package com.floriantoenjes.analyzer;

import com.floriantoenjes.analyzer.data.CountryDao;
import com.floriantoenjes.analyzer.model.Country;
import com.floriantoenjes.analyzer.model.Country.CountryBuilder;
import com.floriantoenjes.presentation.Menu;
import com.floriantoenjes.util.Prompter;

import java.util.List;

public class Application {
    private static final CountryDao dao = new CountryDao();

    public static void main(String[] args) {
//        createMockData();
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
        String code = "Code";
        String name = "Name";
        String alr = "Adult Literacy Rate";
        String intUsers = "Internet Users";


        // Get greatest length for each value to determine column width
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
        System.out.println();
    }

    private static void addCountry() {
        List<Country> countries = dao.getCountryList();
        printCountryTable(countries);
        System.out.println("Enter the data of the new country.");
        String code;
        while (true) {
            final String tmpCode = Prompter.prompt("Code> ");
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
            final String tmpCode = Prompter.prompt("Code of country to edit> ");
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
            final String tmpCode = Prompter.prompt("Code> ");
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

/*
    private static void createMockData() {
        Country germany = new Country("deu", "Deutschland");
        Country italy = new Country("ita", "Italia");
        Country cameroon = new Country("cam", "Cameroon");
        Country france = new Country("fra", "France");
        Country brazil = new CountryBuilder("bra", "Brazil")
                .withAdultLiteracyRate(355829)
                .withInternetUsers(518239382)
                .build();
        dao.save(germany, italy, cameroon, france, brazil);
    }
*/



}
