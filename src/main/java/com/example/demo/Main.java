package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application {
    static final int WIDTH = 900;
    static final int HEIGHT = 900;

    private Group gameRoot;
    private Scene gameScene;

    @Override
    public void start(Stage primaryStage) {
        // Initialize game and endgame roots/scenes
        gameRoot = new Group();
        gameScene = new Scene(gameRoot, WIDTH, HEIGHT); // no background color needed
        Group endgameRoot = new Group();
        Scene endGameScene = new Scene(endgameRoot, WIDTH, HEIGHT);

        // Start the game
        GameScene game = new GameScene();
        game.game(gameScene, gameRoot, primaryStage, endGameScene, endgameRoot);

        // Set scene and show
        primaryStage.setScene(gameScene);
        primaryStage.setTitle("2048");
        primaryStage.show();

        // Ensure the root has focus to receive keyboard input
        gameRoot.requestFocus();
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
        game.game(gameScene, gameRoot, primaryStage, endGameScene, endGameRoot);

        primaryStage.setScene(gameScene);
        gameRoot.requestFocus(); // important for keyboard input on restart
    }
}
