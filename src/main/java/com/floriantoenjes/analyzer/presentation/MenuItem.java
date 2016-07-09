package com.floriantoenjes.analyzer.presentation;

public class MenuItem {
    private String name;
    private Runnable runnable;

    public MenuItem(String name, Runnable runnable) {
        this.name = name;
        this.runnable = runnable;
    }

    public void execute() {
        runnable.run();
    }

    @Override
    public String toString() {
        return name;
    }
}
