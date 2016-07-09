package com.floriantoenjes.analyzer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Prompter {

    public static String prompt(String prompt, Object... args) {
        String str = "";

        System.out.printf(prompt, args);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            str = reader.readLine().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static int promptInt(String prompt, Object... args) {
        try {
            return Integer.parseInt(prompt(prompt, args));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static boolean promptForYes(String prompt, Object... args) {
        String input = prompt(prompt, args);

        if (!input.isEmpty() && Character.toLowerCase(input.charAt(0)) == 'y') {
            return true;
        } else {
            return false;
        }
    }
}
