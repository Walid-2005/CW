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

public class Main extends Application {
    static final int WIDTH = 900;
    static final int HEIGHT = 900;

    private Group gameRoot;
    private Scene gameScene;
    private static Account currentAccount;

    @Override
    public void start(Stage primaryStage) {
        Parameters params = getParameters();
        boolean debugMode = params.getRaw().contains("--debug");

        // Load accounts from file
        Account.loadAccounts();

        if (debugMode) {
            currentAccount = Account.makeNewAccount("debugUser");
        } else {
            // Prompt for username
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

        // Initialize game and endgame roots/scenes
        gameRoot = new Group();
        gameScene = new Scene(gameRoot, WIDTH, HEIGHT); // no background color needed
        Group endgameRoot = new Group();
        Scene endGameScene = new Scene(endgameRoot, WIDTH, HEIGHT);

        // Start the game
        GameScene game = new GameScene();
        game.game(gameScene, gameRoot, primaryStage, endGameScene, endgameRoot, currentAccount);

        // Set scene and show
        primaryStage.setScene(gameScene);
        primaryStage.setTitle("2048");
        primaryStage.show();

        // Ensure the root has focus to receive keyboard input
        gameRoot.requestFocus();

        // Save accounts on close
        primaryStage.setOnCloseRequest(e -> Account.saveAccounts());
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Restart game method
    public static void restartGame(Stage primaryStage) {
        Group gameRoot = new Group();
        Scene gameScene = new Scene(gameRoot, WIDTH, HEIGHT);
        Group endGameRoot = new Group();
        Scene endGameScene = new Scene(endGameRoot, WIDTH, HEIGHT);

        GameScene game = new GameScene();
        game.game(gameScene, gameRoot, primaryStage, endGameScene, endGameRoot, currentAccount);

        primaryStage.setScene(gameScene);
        gameRoot.requestFocus(); // important for keyboard input on restart
    }

    // Show leaderboard method
    public static void showLeaderboard(Stage stage) {
        Group leaderboardRoot = new Group();
        Scene leaderboardScene = new Scene(leaderboardRoot, WIDTH, HEIGHT);

        Text title = new Text("Leaderboard");
        title.setFont(Font.font("Arial", 30));
        title.setFill(Color.WHITE);
        title.setLayoutX(350);
        title.setLayoutY(50);
        leaderboardRoot.getChildren().add(title);

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

        Button backButton = new Button("Back");
        backButton.setLayoutX(450);
        backButton.setLayoutY(HEIGHT - 60);
        leaderboardRoot.getChildren().add(backButton);
        backButton.setOnAction(e -> restartGame(stage));

        stage.setScene(leaderboardScene);
    }
}
