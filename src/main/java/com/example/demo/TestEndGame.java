package com.example.demo;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestEndGame extends Application {
    @Override
    public void start(Stage primaryStage) {
        Group endGameRoot = new Group();
        Scene endGameScene = new Scene(endGameRoot, Main.WIDTH, Main.HEIGHT);
        EndGame.getInstance().endGameShow(endGameScene, endGameRoot, primaryStage, 1508); // test score
        primaryStage.setScene(endGameScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
