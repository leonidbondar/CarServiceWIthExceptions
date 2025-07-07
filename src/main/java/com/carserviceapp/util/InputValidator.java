package com.carserviceapp.util;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Utility class for robust user input validation.
 */
public class InputValidator {
    private static final Scanner scanner = new Scanner(System.in);

    // Regex for positive integers
    private static final Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("^\\d+$");
    // Regex for decimal numbers (can be positive or negative, allows optional decimal part)
    private static final Pattern DECIMAL_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");
    // Regex for non-empty strings
    private static final Pattern NON_EMPTY_STRING_PATTERN = Pattern.compile("^\\s*\\S+.*$"); // At least one non-whitespace character

    /**
     * Gets a non-empty string input from the user.
     * @param prompt The message to display to the user.
     * @return The validated string input.
     */
    public static String getStringInput(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine();
            if (NON_EMPTY_STRING_PATTERN.matcher(input).matches()) {
                return input.trim();
            } else {
                System.out.println("Input cannot be empty. Please try again.");
            }
        }
    }

    /**
     * Gets a positive integer input from the user without throwing exceptions.
     * Uses regex for validation.
     * @param prompt The message to display to the user.
     * @return The validated positive integer input.
     */
    public static int getPositiveIntegerInput(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine();
            if (POSITIVE_INTEGER_PATTERN.matcher(input).matches()) {
                try {
                    int value = Integer.parseInt(input);
                    if (value >= 0) { // Allow 0 for specific menu choices like "create new"
                        return value;
                    } else {
                        System.out.println("Input must be a non-negative integer. Please try again.");
                    }
                } catch (NumberFormatException e) {
                    // This catch block is theoretically unreachable if regex matches, but good for safety
                    System.out.println("Invalid numeric input. Please enter a whole number. Error: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid input. Please enter a valid non-negative whole number.");
            }
        }
    }

    /**
     * Gets a decimal number input from the user without throwing exceptions.
     * Uses regex for validation.
     * @param prompt The message to display to the user.
     * @return The validated decimal input.
     */
    public static double getDecimalInput(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine();
            if (DECIMAL_PATTERN.matcher(input).matches()) {
                try {
                    return Double.parseDouble(input);
                } catch (NumberFormatException e) {
                    // This catch block is theoretically unreachable if regex matches
                    System.out.println("Invalid numeric input. Please enter a valid number. Error: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid input. Please enter a valid number (e.g., 123, 12.34).");
            }
        }
    }

    /**
     * Gets a yes/no input from the user.
     * @param prompt The message to display to the user.
     * @return true if 'yes' or 'y', false if 'no' or 'n'.
     */
    public static boolean getYesNoInput(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt + " (yes/no): ");
            input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("yes") || input.equals("y")) {
                return true;
            } else if (input.equals("no") || input.equals("n")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'yes' or 'no'.");
            }
        }
    }

    /**
     * Closes the scanner. Should be called when the application exits.
     */
    public static void closeScanner() {
        scanner.close();
    }
}