package com.floriantoenjes.presentation;

public class MenuItem {
    private final String name;
    private final Runnable runnable;

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
