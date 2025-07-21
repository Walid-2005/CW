package com.example.demo;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;

public class EndGame {
    private static EndGame singleInstance = null;

    private EndGame() {}

    public static EndGame getInstance() {
        if (singleInstance == null)
            singleInstance = new EndGame();
        return singleInstance;
    }

    public void endGameShow(Scene endGameScene, Group root, Stage primaryStage, long score) {
        // Load external CSS
        endGameScene.getStylesheets().add(getClass().getResource("/com/example/demo/endgame.css").toExternalForm());

        // Load background GIF
        URL bgUrl = getClass().getResource("/com/example/demo/background.gif");
        if (bgUrl != null) {
            Image background = new Image(bgUrl.toExternalForm());
            ImageView backgroundView = new ImageView(background);
            backgroundView.setFitWidth(Main.WIDTH);
            backgroundView.setFitHeight(Main.HEIGHT);
            backgroundView.setPreserveRatio(false);
            root.getChildren().add(backgroundView);
        } else {
            System.out.println("❌ background.gif not found.");
        }

        // Load game-over.png
        URL overUrl = getClass().getResource("/com/example/demo/game-over.png");
        double gameOverY = 200;
        if (overUrl != null) {
            Image gameOver = new Image(overUrl.toExternalForm());
            ImageView gameOverView = new ImageView(gameOver);
            gameOverView.setFitWidth(620);
            gameOverView.setPreserveRatio(true);
            gameOverView.relocate((Main.WIDTH - 630) / 2.0, gameOverY);
            root.getChildren().add(gameOverView);
        } else {
            System.out.println("❌ game-over.png not found.");
        }

        // Score
        Text scoreText = new Text("Score: " + score);
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font(30));
        scoreText.relocate(Main.WIDTH - 220, 40);
        root.getChildren().add(scoreText);

        // Button layout
        int buttonWidth = 120;
        int buttonHeight = 40;
        double buttonY = gameOverY + 320;
        double spacing = 60;
        double totalWidth = 2 * buttonWidth + spacing;
        double startX = (Main.WIDTH - totalWidth) / 2;

        // Quit button
        Button quitButton = new Button("QUIT");
        quitButton.setPrefSize(buttonWidth, buttonHeight);
        quitButton.getStyleClass().add("endgame-button");
        quitButton.relocate(startX, buttonY);
        root.getChildren().add(quitButton);

        quitButton.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Quit Dialog");
            alert.setHeaderText("Quit from this page");
            alert.setContentText("Are you sure?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                root.getChildren().clear();
                primaryStage.close();
            }
        });

        // Restart button
        Button restartButton = new Button("RESTART");
        restartButton.setPrefSize(buttonWidth, buttonHeight);
        restartButton.getStyleClass().add("endgame-button");
        restartButton.relocate(startX + buttonWidth + spacing, buttonY);
        root.getChildren().add(restartButton);

        restartButton.setOnMouseClicked(e -> {
            root.getChildren().clear();
            Main.restartGame(primaryStage);
        });
    }
}
