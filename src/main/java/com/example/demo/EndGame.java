package com.example.demo;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;

/**
 * Handles the display and functionality of the end game screen.
 * Implements the singleton pattern to ensure only one instance is used.
 */
public class EndGame {

    /** Singleton instance of the EndGame screen. */
    private static EndGame singleInstance = null;

    /** Private constructor to enforce singleton usage. */
    private EndGame() {}

    /**
     * Returns the single instance of EndGame.
     *
     * @return the EndGame instance
     */
    public static EndGame getInstance() {
        if (singleInstance == null)
            singleInstance = new EndGame();
        return singleInstance;
    }

    /**
     * Displays the end game screen with background, game over image, score, and action buttons.
     *
     * @param endGameScene the JavaFX Scene for the end game
     * @param root the root group where all UI elements are placed
     * @param primaryStage the primary stage of the application
     * @param score the final score to display
     */
    public void endGameShow(Scene endGameScene, Group root, Stage primaryStage, long score) {
        // Load external CSS styling
        endGameScene.getStylesheets().add(getClass().getResource("/com/example/demo/endgame.css").toExternalForm());

        // Load and display background GIF
        URL bgUrl = getClass().getResource("/com/example/demo/background.gif");
        if (bgUrl != null) {
            Image background = new Image(bgUrl.toExternalForm());
            ImageView backgroundView = new ImageView(background);
            backgroundView.setFitWidth(Main.WIDTH);
            backgroundView.setFitHeight(Main.HEIGHT);
            backgroundView.setPreserveRatio(false);
            root.getChildren().add(backgroundView);
        } else {
            System.out.println(" background.gif not found.");
        }

        // Load and display "game over" image
        URL overUrl = getClass().getResource("/com/example/demo/game-over.png");
        double gameOverY = 15;
        if (overUrl != null) {
            Image gameOver = new Image(overUrl.toExternalForm());
            ImageView gameOverView = new ImageView(gameOver);
            gameOverView.setFitWidth(420);
            gameOverView.setPreserveRatio(true);
            gameOverView.relocate((Main.WIDTH - 430) / 2.0, gameOverY);
            root.getChildren().add(gameOverView);
        } else {
            System.out.println(" game-over.png not found.");
        }

        // Display the score
        Text scoreText = new Text("Score: " + score);
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font(30));
        scoreText.relocate(Main.WIDTH - 220, 40);
        root.getChildren().add(scoreText);

        // Button layout calculations
        int buttonWidth = 120;
        int buttonHeight = 40;
        double buttonY = gameOverY + 400;
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
