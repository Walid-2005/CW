package com.example.demo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Utility class for creating and managing {@link Text} objects used in game cells.
 * Implements the singleton pattern to ensure consistent text rendering.
 */
class TextMaker {

    /** Singleton instance of the TextMaker class. */
    private static TextMaker singleInstance = null;

    /** Private constructor to prevent external instantiation. */
    private TextMaker() {}

    /**
     * Returns the single instance of TextMaker.
     *
     * @return the singleton instance of TextMaker
     */
    static TextMaker getSingleInstance() {
        if (singleInstance == null)
            singleInstance = new TextMaker();
        return singleInstance;
    }

    /**
     * Creates a styled {@link Text} object for a cell with appropriate font size and position.
     *
     * @param input  the text to display
     * @param xCell  the x-coordinate of the cell
     * @param yCell  the y-coordinate of the cell
     * @param root   the root {@link Group} to which the text belongs
     * @return the created {@link Text} object
     */
    Text madeText(String input, double xCell, double yCell, Group root) {
        double length = GameScene.getLENGTH();

        int value;
        try {
            value = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            value = 0;
        }

        int digits = (value == 0) ? 1 : String.valueOf(value).length();
        int fontSize;

        if (digits <= 2) fontSize = 40;
        else if (digits == 3) fontSize = 34;
        else if (digits == 4) fontSize = 28;
        else fontSize = 22;

        Text text = new Text(input);
        text.setFont(Font.font("Verdana", FontWeight.BOLD, fontSize));
        text.relocate((xCell + (1.2) * length / 7.0), (yCell + 2 * length / 7.0));
        text.setFill(Color.WHITE);

        return text;
    }

    /**
     * Swaps the text content and position between two {@link Text} objects.
     *
     * @param first  the first {@link Text} object
     * @param second the second {@link Text} object
     */
    static void changeTwoText(Text first, Text second) {
        String temp;
        temp = first.getText();
        first.setText(second.getText());
        second.setText(temp);

        double tempNumber;
        tempNumber = first.getX();
        first.setX(second.getX());
        second.setX(tempNumber);

        tempNumber = first.getY();
        first.setY(second.getY());
        second.setY(tempNumber);
    }
}
