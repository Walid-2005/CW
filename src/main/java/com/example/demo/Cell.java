package com.example.demo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class Cell {
    private Rectangle rectangle;
    private Group root;
    private Text textClass;
    private boolean modify = false;
    private static boolean isDarkMode = false;

    void setModify(boolean modify) {
        this.modify = modify;
    }

    boolean getModify() {
        return modify;
    }

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

    void setTextClass(Text textClass) {
        this.textClass = textClass;
    }

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

    int adder(Cell cell) {
        int addedValue = cell.getNumber() + this.getNumber();
        cell.getTextClass().setText((cell.getNumber() + this.getNumber()) + "");
        textClass.setText("0");
        root.getChildren().remove(textClass);
        cell.setColorByNumber(cell.getNumber());
        setColorByNumber(getNumber());
        return addedValue;
    }

    void setColorByNumber(int number) {
        if (!isDarkMode) {
            switch (number) {
                case 0:
                    rectangle.setFill(Color.rgb(224, 226, 226, 0.5));
                    break;
                case 2:
                    rectangle.setFill(Color.rgb(255, 228, 181, 0.6));
                    break;
                case 4:
                    rectangle.setFill(Color.rgb(255, 204, 153, 0.7));
                    break;
                case 8:
                    rectangle.setFill(Color.rgb(255, 153, 102, 0.75));
                    break;
                case 16:
                    rectangle.setFill(Color.rgb(255, 128, 0, 0.8));
                    break;
                case 32:
                    rectangle.setFill(Color.rgb(255, 102, 0, 0.85));
                    break;
                case 64:
                    rectangle.setFill(Color.rgb(204, 85, 0, 0.85));
                    break;
                case 128:
                    rectangle.setFill(Color.rgb(153, 76, 0, 0.9));
                    break;
                case 256:
                    rectangle.setFill(Color.rgb(178, 34, 34, 0.9));
                    break;
                case 512:
                    rectangle.setFill(Color.rgb(139, 0, 0, 0.9));
                    break;
                case 1024:
                    rectangle.setFill(Color.rgb(128, 0, 32, 0.95));
                    break;
                case 2048:
                    rectangle.setFill(Color.rgb(112, 41, 99, 1));
                    break;
            }
        } else {
            switch (number) {
                case 0:
                    rectangle.setFill(Color.rgb(60, 60, 60, 0.5));
                    break;
                case 2:
                    rectangle.setFill(Color.rgb(85, 85, 85, 0.6));
                    break;
                case 4:
                    rectangle.setFill(Color.rgb(102, 85, 85, 0.65));
                    break;
                case 8:
                    rectangle.setFill(Color.rgb(120, 70, 50, 0.7));
                    break;
                case 16:
                    rectangle.setFill(Color.rgb(140, 60, 30, 0.75));
                    break;
                case 32:
                    rectangle.setFill(Color.rgb(160, 50, 20, 0.8));
                    break;
                case 64:
                    rectangle.setFill(Color.rgb(180, 40, 10, 0.85));
                    break;
                case 128:
                    rectangle.setFill(Color.rgb(200, 30, 30, 0.9));
                    break;
                case 256:
                    rectangle.setFill(Color.rgb(180, 0, 70, 0.9));
                    break;
                case 512:
                    rectangle.setFill(Color.rgb(150, 0, 100, 0.9));
                    break;
                case 1024:
                    rectangle.setFill(Color.rgb(120, 0, 120, 0.95));
                    break;
                case 2048:
                    rectangle.setFill(Color.rgb(90, 0, 90, 1));
                    break;
            }
        }
    }

    public static void setDarkMode(boolean dark) {
        isDarkMode = dark;
    }

    double getX() {
        return rectangle.getX();
    }

    double getY() {
        return rectangle.getY();
    }

    public int getValue() {
        return this.getNumber();
    }

    int getNumber() {
        try {
            return Integer.parseInt(textClass.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setValue(int value) {
        textClass.setText(Integer.toString(value));
        setColorByNumber(value);
        if (value == 0) {
            root.getChildren().remove(textClass);
        } else {
            if (!root.getChildren().contains(textClass)) {
                root.getChildren().add(textClass);
            }
        }
    }

    // Required for animation
    public Rectangle getRectangle() {
        return rectangle;
    }

    public Text getText() {
        return textClass;
    }

    public Text getTextClass() {
        return textClass;
    }
}
