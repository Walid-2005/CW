package com.example.demo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Represents a single tile (cell) in the 2048 game grid.
 * Each cell contains a rectangle for background color,
 * a text element for displaying the value, and logic
 * for updating visuals when the value changes.
 */
public class Cell {

    /** The rectangle representing the tile background. */
    private Rectangle rectangle;

    /** The JavaFX root group where this cell is drawn. */
    private Group root;

    /** The text element displaying the number on the tile. */
    private Text textClass;

    /** Flag indicating whether the cell's content has been modified. */
    private boolean modify = false;

    /** Tracks whether the game is currently in dark mode. */
    private static boolean isDarkMode = false;

    /**
     * Sets whether this cell has been modified.
     *
     * @param modify true if modified, false otherwise
     */
    void setModify(boolean modify) {
        this.modify = modify;
    }

    /**
     * Checks if the cell has been modified.
     *
     * @return true if modified, false otherwise
     */
    boolean getModify() {
        return modify;
    }

    /**
     * Constructs a cell at the given position and size.
     *
     * @param x     the X-coordinate of the cell
     * @param y     the Y-coordinate of the cell
     * @param scale the width/height of the cell
     * @param root  the JavaFX root group to add the cell to
     */
    Cell(double x, double y, double scale, Group root) {
        rectangle = new Rectangle();
        rectangle.setX(x);
        rectangle.setY(y);
        rectangle.setHeight(scale);
        rectangle.setWidth(scale);
        this.root = root;
        rectangle.setFill(Color.rgb(224, 226, 226, 0.5));
        this.textClass = TextMaker.getSingleInstance().madeText("0", x, y, root);
        root.getChildren().add(rectangle);
    }

    /**
     * Sets the text object used by this cell.
     *
     * @param textClass the JavaFX text object
     */
    void setTextClass(Text textClass) {
        this.textClass = textClass;
    }

    /**
     * Swaps the text and color between this cell and another.
     *
     * @param cell the other cell to swap with
     */
    void changeCell(Cell cell) {
        TextMaker.changeTwoText(textClass, cell.getTextClass());
        root.getChildren().remove(cell.getTextClass());
        root.getChildren().remove(textClass);

        if (!cell.getTextClass().getText().equals("0")) {
            root.getChildren().add(cell.getTextClass());
        }
        if (!textClass.getText().equals("0")) {
            root.getChildren().add(textClass);
        }
        setColorByNumber(getNumber());
        cell.setColorByNumber(cell.getNumber());
    }

    /**
     * Merges the value of this cell into another, adding the numbers together.
     *
     * @param cell the target cell to add into
     * @return the new merged value
     */
    int adder(Cell cell) {
        int addedValue = cell.getNumber() + this.getNumber();
        cell.getTextClass().setText((cell.getNumber() + this.getNumber()) + "");
        textClass.setText("0");
        root.getChildren().remove(textClass);
        cell.setColorByNumber(cell.getNumber());
        setColorByNumber(getNumber());
        return addedValue;
    }

    /**
     * Sets the background color of the cell based on its number.
     * Different colors are used for light and dark modes.
     *
     * @param number the number to determine the color
     */
    void setColorByNumber(int number) {
        if (!isDarkMode) {
            switch (number) {
                case 0: rectangle.setFill(Color.rgb(224, 226, 226, 0.5)); break;
                case 2: rectangle.setFill(Color.rgb(255, 228, 181, 0.6)); break;
                case 4: rectangle.setFill(Color.rgb(255, 204, 153, 0.7)); break;
                case 8: rectangle.setFill(Color.rgb(255, 153, 102, 0.75)); break;
                case 16: rectangle.setFill(Color.rgb(255, 128, 0, 0.8)); break;
                case 32: rectangle.setFill(Color.rgb(255, 102, 0, 0.85)); break;
                case 64: rectangle.setFill(Color.rgb(204, 85, 0, 0.85)); break;
                case 128: rectangle.setFill(Color.rgb(153, 76, 0, 0.9)); break;
                case 256: rectangle.setFill(Color.rgb(178, 34, 34, 0.9)); break;
                case 512: rectangle.setFill(Color.rgb(139, 0, 0, 0.9)); break;
                case 1024: rectangle.setFill(Color.rgb(128, 0, 32, 0.95)); break;
                case 2048: rectangle.setFill(Color.rgb(112, 41, 99, 1)); break;
            }
        } else {
            switch (number) {
                case 0: rectangle.setFill(Color.rgb(60, 60, 60, 0.5)); break;
                case 2: rectangle.setFill(Color.rgb(85, 85, 85, 0.6)); break;
                case 4: rectangle.setFill(Color.rgb(102, 85, 85, 0.65)); break;
                case 8: rectangle.setFill(Color.rgb(120, 70, 50, 0.7)); break;
                case 16: rectangle.setFill(Color.rgb(140, 60, 30, 0.75)); break;
                case 32: rectangle.setFill(Color.rgb(160, 50, 20, 0.8)); break;
                case 64: rectangle.setFill(Color.rgb(180, 40, 10, 0.85)); break;
                case 128: rectangle.setFill(Color.rgb(200, 30, 30, 0.9)); break;
                case 256: rectangle.setFill(Color.rgb(180, 0, 70, 0.9)); break;
                case 512: rectangle.setFill(Color.rgb(150, 0, 100, 0.9)); break;
                case 1024: rectangle.setFill(Color.rgb(120, 0, 120, 0.95)); break;
                case 2048: rectangle.setFill(Color.rgb(90, 0, 90, 1)); break;
            }
        }
    }

    /**
     * Enables or disables dark mode for all cells.
     *
     * @param dark true to enable dark mode, false to disable
     */
    public static void setDarkMode(boolean dark) {
        isDarkMode = dark;
    }

    /**
     * Gets the X-coordinate of the cell.
     *
     * @return the X position
     */
    double getX() {
        return rectangle.getX();
    }

    /**
     * Gets the Y-coordinate of the cell.
     *
     * @return the Y position
     */
    double getY() {
        return rectangle.getY();
    }

    /**
     * Returns the cell's value (alias for {@link #getNumber()}).
     *
     * @return the value of the cell
     */
    public int getValue() {
        return this.getNumber();
    }

    /**
     * Parses and returns the number displayed in the cell.
     *
     * @return the numeric value of the cell
     */
    int getNumber() {
        try {
            return Integer.parseInt(textClass.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Sets the cell's value and updates its color and font size.
     *
     * @param value the new value for the cell
     */
    public void setValue(int value) {
        textClass.setText(Integer.toString(value));
        setColorByNumber(value);

        int digits = String.valueOf(value).length();
        int fontSize;
        if (digits <= 2) fontSize = 40;
        else if (digits == 3) fontSize = 34;
        else if (digits == 4) fontSize = 28;
        else fontSize = 22;

        textClass.setFont(javafx.scene.text.Font.font("Verdana", javafx.scene.text.FontWeight.BOLD, fontSize));

        if (value == 0) {
            root.getChildren().remove(textClass);
        } else {
            if (!root.getChildren().contains(textClass)) {
                root.getChildren().add(textClass);
            }
        }
    }

    /**
     * Gets the rectangle representing the cell's background.
     * Used for animations and styling.
     *
     * @return the rectangle object
     */
    public Rectangle getRectangle() {
        return rectangle;
    }

    /**
     * Gets the JavaFX text object representing the cell's number.
     *
     * @return the text object
     */
    public Text getText() {
        return textClass;
    }

    /**
     * Gets the cell's text object (duplicate accessor).
     *
     * @return the text object
     */
    public Text getTextClass() {
        return textClass;
    }
}
