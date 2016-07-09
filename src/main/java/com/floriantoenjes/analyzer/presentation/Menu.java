package com.floriantoenjes.analyzer.presentation;

import com.floriantoenjes.analyzer.util.Prompter;

import java.util.ArrayList;

public class Menu  {
    private final ArrayList<MenuItem> menuItems = new ArrayList<>();

    public void show() {
        for (int i = 0; i < menuItems.size(); i++) {
            System.out.printf("%d) %s%n", i + 1, menuItems.get(i));
        }
        int selection;
        do {
            selection = Prompter.promptInt("Option? > ");
        } while ( selection < 1 || selection > menuItems.size());
        System.out.println();
        menuItems.get(selection -1).execute();
    }

    public void addMenuItem(String name, Runnable r) {
        menuItems.add(new MenuItem(name, r));
    }
}
