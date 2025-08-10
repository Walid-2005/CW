package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.util.Optional;

/**
 * Entry point for the 2048 game application.
 * <p>
 * Manages application lifecycle, user login, and initialization of game and leaderboard screens.
 * Implements methods for starting, restarting, and showing the leaderboard.
 */
public class Main extends Application {

    /** Width of the application window. */
    static final int WIDTH = 900;

    /** Height of the application window. */
    static final int HEIGHT = 900;

    /** Root node for the main game scene. */
    private Group gameRoot;

    /** Main game scene. */
    private Scene gameScene;

    /** The currently logged-in account. */
    private static Account currentAccount;

    /**
     * Initializes and starts the application.
     *
     * @param primaryStage the primary window (stage) for the application
     */
    @Override
    public void start(Stage primaryStage) {
        Parameters params = getParameters();
        boolean debugMode = params.getRaw().contains("--debug");

        // Load saved accounts
        Account.loadAccounts();

        // Handle user login or debug account
        if (debugMode) {
            currentAccount = Account.makeNewAccount("debugUser");
        } else {
            TextInputDialog dialog = new TextInputDialog("Player");
            dialog.setTitle("Enter Username");
            dialog.setHeaderText("Welcome to 2048!");
            dialog.setContentText("Enter your username:");
            Optional<String> result = dialog.showAndWait();
            String username = result.orElse("Player");

            Account existing = Account.accountHaveBeenExist(username);
            if (existing != null) {
                currentAccount = existing;
            } else {
                currentAccount = Account.makeNewAccount(username);
            }
        }

        // Create game and endgame scenes
        gameRoot = new Group();
        gameScene = new Scene(gameRoot, WIDTH, HEIGHT);
        Group endgameRoot = new Group();
        Scene endGameScene = new Scene(endgameRoot, WIDTH, HEIGHT);

        // Start game
        GameScene game = new GameScene();
        game.game(gameScene, gameRoot, primaryStage, endGameScene, endgameRoot, currentAccount);

        // Display main game scene
        primaryStage.setScene(gameScene);
        primaryStage.setTitle("2048");
        primaryStage.show();

        // Ensure focus for keyboard input
        gameRoot.requestFocus();

        // Save accounts when closing
        primaryStage.setOnCloseRequest(e -> Account.saveAccounts());
    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Restarts the game with the same user account.
     *
     * @param primaryStage the main application stage
     */
    public static void restartGame(Stage primaryStage) {
        Group gameRoot = new Group();
        Scene gameScene = new Scene(gameRoot, WIDTH, HEIGHT);
        Group endGameRoot = new Group();
        Scene endGameScene = new Scene(endGameRoot, WIDTH, HEIGHT);

        GameScene game = new GameScene();
        game.game(gameScene, gameRoot, primaryStage, endGameScene, endGameRoot, currentAccount);

        primaryStage.setScene(gameScene);
        gameRoot.requestFocus();
    }

    /**
     * Displays the leaderboard scene.
     * <p>
     * Reads leaderboard entries from a text file and displays them.
     * Provides a back button to return to the game.
     *
     * @param stage the current stage to display the leaderboard on
     */
    public static void showLeaderboard(Stage stage) {
        Group leaderboardRoot = new Group();
        Scene leaderboardScene = new Scene(leaderboardRoot, WIDTH, HEIGHT);

        // Leaderboard title
        Text title = new Text("Leaderboard");
        title.setFont(Font.font("Arial", 30));
        title.setFill(Color.WHITE);
        title.setLayoutX(350);
        title.setLayoutY(50);
        leaderboardRoot.getChildren().add(title);

        // Load leaderboard entries
        File leaderboardFile = new File("src/main/resources/leaderboard.txt");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(leaderboardFile));
            String line;
            int yOffset = 100;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    Text entry = new Text(parts[0] + ": " + parts[1]);
                    entry.setFont(Font.font("Arial", 18));
                    entry.setFill(Color.WHITE);
                    entry.setLayoutX(350);
                    entry.setLayoutY(yOffset);
                    leaderboardRoot.getChildren().add(entry);
                    yOffset += 30;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Back button
        Button backButton = new Button("Back");
        backButton.setLayoutX(450);
        backButton.setLayoutY(HEIGHT - 60);
        leaderboardRoot.getChildren().add(backButton);
        backButton.setOnAction(e -> restartGame(stage));

        stage.setScene(leaderboardScene);
    }
}
