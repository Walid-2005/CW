package com.example.demo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

class TextMaker {
    private static TextMaker singleInstance = null;

    private TextMaker() {

    }

    static TextMaker getSingleInstance() {
        if (singleInstance == null)
            singleInstance = new TextMaker();
        return singleInstance;
    }

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
        text.relocate((xCell + (1.2)* length / 7.0), (yCell + 2 * length / 7.0));
        text.setFill(Color.WHITE);

        return text;
    }

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
