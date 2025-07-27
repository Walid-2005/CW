package com.example.demo;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.util.Random;

class GameScene {
    private static int HEIGHT = 500;
    private static int n = 4;
    private final static int distanceBetweenCells = 10;
    private static double LENGTH = (HEIGHT - ((n + 1) * distanceBetweenCells)) / (double) n;
    private TextMaker textMaker = TextMaker.getSingleInstance();
    private Cell[][] cells = new Cell[n][n];
    private Group root;
    private long score = 0;
    private long highScore = 0;
    private Text scoretext;
    private Text highScoreText;
    private boolean isDarkMode = false;
    private ImageView backgroundView;
    private final File highScoreFile = new File("src/main/resources/highscore.txt");

    static void setN(int number) {
        n = number;
        LENGTH = (HEIGHT - ((n + 1) * distanceBetweenCells)) / (double) n;
    }

    static double getLENGTH() {
        return LENGTH;
    }

    private void randomFillNumber(int turn) {
        Cell[][] emptyCells = new Cell[n][n];
        int a = 0;
        int b = 0;
        int aForBound = 0, bForBound = 0;
        outer:
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (cells[i][j].getNumber() == 0) {
                    emptyCells[a][b] = cells[i][j];
                    if (b < n - 1) {
                        bForBound = b;
                        b++;
                    } else {
                        aForBound = a;
                        a++;
                        b = 0;
                        if (a == n)
                            break outer;
                    }
                }
            }
        }

        Text text;
        Random random = new Random();
        boolean putTwo = random.nextDouble() < 0.9;
        int xCell = random.nextInt(aForBound + 1);
        int yCell = random.nextInt(bForBound + 1);
        if (putTwo) {
            text = textMaker.madeText("2", emptyCells[xCell][yCell].getX(), emptyCells[xCell][yCell].getY(), root);
            emptyCells[xCell][yCell].setTextClass(text);
            root.getChildren().add(text);
            emptyCells[xCell][yCell].setColorByNumber(2);
        } else {
            text = textMaker.madeText("4", emptyCells[xCell][yCell].getX(), emptyCells[xCell][yCell].getY(), root);
            emptyCells[xCell][yCell].setTextClass(text);
            root.getChildren().add(text);
            emptyCells[xCell][yCell].setColorByNumber(4);
        }
    }

    private int haveEmptyCell() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (cells[i][j].getNumber() == 0)
                    return 1;
                if (cells[i][j].getNumber() == 2048)
                    return 0;
            }
        }
        return -1;
    }

    private void moveLeft() {
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < n; j++) {
                moveHorizontally(i, j, passDestination(i, j, 'l'), -1);
            }
            for (int j = 0; j < n; j++) {
                cells[i][j].setModify(false);
            }
        }
    }

    private void moveRight() {
        for (int i = 0; i < n; i++) {
            for (int j = n - 1; j >= 0; j--) {
                moveHorizontally(i, j, passDestination(i, j, 'r'), 1);
            }
            for (int j = 0; j < n; j++) {
                cells[i][j].setModify(false);
            }
        }
    }

    private void moveUp() {
        for (int j = 0; j < n; j++) {
            for (int i = 1; i < n; i++) {
                moveVertically(i, j, passDestination(i, j, 'u'), -1);
            }
            for (int i = 0; i < n; i++) {
                cells[i][j].setModify(false);
            }
        }
    }

    private void moveDown() {
        for (int j = 0; j < n; j++) {
            for (int i = n - 1; i >= 0; i--) {
                moveVertically(i, j, passDestination(i, j, 'd'), 1);
            }
            for (int i = 0; i < n; i++) {
                cells[i][j].setModify(false);
            }
        }
    }

    private void moveHorizontally(int i, int j, int des, int sign) {
        if (isValidDesH(i, j, des, sign)) {
            score += cells[i][j].adder(cells[i][des + sign]);
            updateScore();
            cells[i][des].setModify(true);
        } else if (des != j) {
            cells[i][j].changeCell(cells[i][des]);
        }
    }

    private void moveVertically(int i, int j, int des, int sign) {
        if (isValidDesV(i, j, des, sign)) {
            score += cells[i][j].adder(cells[des + sign][j]);
            updateScore();
            cells[des][j].setModify(true);
        } else if (des != i) {
            cells[i][j].changeCell(cells[des][j]);
        }
    }

    private boolean isValidDesH(int i, int j, int des, int sign) {
        return (des + sign < n && des + sign >= 0
                && cells[i][des + sign].getNumber() == cells[i][j].getNumber()
                && !cells[i][des + sign].getModify()
                && cells[i][des + sign].getNumber() != 0);
    }

    private boolean isValidDesV(int i, int j, int des, int sign) {
        return (des + sign < n && des + sign >= 0
                && cells[des + sign][j].getNumber() == cells[i][j].getNumber()
                && !cells[des + sign][j].getModify()
                && cells[des + sign][j].getNumber() != 0);
    }

    private int passDestination(int i, int j, char direct) {
        int coordinate = j;
        if (direct == 'l') {
            for (int k = j - 1; k >= 0; k--) {
                if (cells[i][k].getNumber() != 0) return k + 1;
                if (k == 0) return 0;
            }
        } else if (direct == 'r') {
            for (int k = j + 1; k < n; k++) {
                if (cells[i][k].getNumber() != 0) return k - 1;
                if (k == n - 1) return n - 1;
            }
        } else if (direct == 'd') {
            for (int k = i + 1; k < n; k++) {
                if (cells[k][j].getNumber() != 0) return k - 1;
                if (k == n - 1) return n - 1;
            }
        } else if (direct == 'u') {
            for (int k = i - 1; k >= 0; k--) {
                if (cells[k][j].getNumber() != 0) return k + 1;
                if (k == 0) return 0;
            }
        }
        return coordinate;
    }

    private boolean haveSameNumberNearly(int i, int j) {
        return (i < n - 1 && cells[i + 1][j].getNumber() == cells[i][j].getNumber()) ||
               (j < n - 1 && cells[i][j + 1].getNumber() == cells[i][j].getNumber());
    }

    private boolean canNotMove() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (haveSameNumberNearly(i, j)) return false;
            }
        }
        return true;
    }

    private void updateScore() {
        scoretext.setText(score + "");
        if (score > highScore) {
            highScore = score;
            highScoreText.setText("HIGH SCORE: " + highScore);
            saveHighScore();
        }
    }

    private void loadHighScore() {
        try {
            if (highScoreFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(highScoreFile));
                highScore = Long.parseLong(reader.readLine());
                reader.close();
            }
        } catch (IOException | NumberFormatException e) {
            highScore = 0;
        }
    }

    private void saveHighScore() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(highScoreFile));
            writer.write(Long.toString(highScore));
            writer.close();
        } catch (IOException ignored) {}
    }

    void game(Scene gameScene, Group root, Stage primaryStage, Scene endGameScene, Group endGameRoot) {
        this.root = root;
        loadHighScore();

        Image background = new Image(getClass().getResource("background.gif").toExternalForm());
        backgroundView = new ImageView(background);
        backgroundView.setFitWidth(Main.WIDTH);
        backgroundView.setFitHeight(Main.HEIGHT);
        backgroundView.setPreserveRatio(false);
        backgroundView.setMouseTransparent(true);
        root.getChildren().add(0, backgroundView);
         
        Image logo = new Image(getClass().getResource("/com/example/demo/2048_LOGO.png").toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(290);
        logoView.setPreserveRatio(true);
        logoView.setLayoutX((Main.WIDTH - 270) / 2.0); // centered horizontally
        logoView.setLayoutY(-80); // positioned above the grid
        root.getChildren().add(logoView);
        
    for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double offsetX = (Main.WIDTH - ((n * LENGTH) + (n + 1) * distanceBetweenCells)) / 2;
                double offsetY = (Main.HEIGHT - ((n * LENGTH) + (n + 1) * distanceBetweenCells)) / 2;
                cells[i][j] = new Cell(offsetX + (j) * LENGTH + (j + 1) * distanceBetweenCells,
                        offsetY + (i) * LENGTH + (i + 1) * distanceBetweenCells, LENGTH, root);
            }
        }
                        // Add New Game button under logo
            Button newGameButton = new Button("New Game");
            newGameButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
            newGameButton.setFont(Font.font("Arial", 13));
            newGameButton.setLayoutX((Main.WIDTH - -150 ) / 2.0); // Centered
            newGameButton.setLayoutY(logoView.getLayoutY() + logoView.getFitHeight() + 212); // Just below logo
            root.getChildren().add(newGameButton);

            newGameButton.setOnAction(e -> {
                Main.restartGame(primaryStage); // Make sure this method exists in Main.java
            });

        Text label = new Text("SCORE:");
        label.setFont(Font.font("Arial", 18));
        label.setFill(Color.WHITE);
        label.relocate(790, 16);
        root.getChildren().add(label);

        scoretext = new Text("0");
        scoretext.setFont(Font.font("Arial", 18));
        scoretext.setFill(Color.WHITE);
        scoretext.relocate(864, 16);
        root.getChildren().add(scoretext);

        highScoreText = new Text("HIGH SCORE: " + highScore);
        highScoreText.setFont(Font.font("Arial", 18));
        highScoreText.setFill(Color.WHITE);
        highScoreText.relocate(20, 20);
        root.getChildren().add(highScoreText);

        Button toggleMode = new Button("Dark Mode");
        toggleMode.setLayoutX(300);
        toggleMode.setLayoutY(130);
        toggleMode.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        root.getChildren().add(toggleMode);

        toggleMode.setOnAction(e -> {
            isDarkMode = !isDarkMode;
            String gifPath = isDarkMode ? "dark-background.gif" : "background.gif";
            Image newBackground = new Image(getClass().getResource(gifPath).toExternalForm());
            backgroundView.setImage(newBackground);
            toggleMode.setText(isDarkMode ? "Light Mode" : "Dark Mode");
            toggleMode.setStyle(isDarkMode
                    ? "-fx-background-color: white; -fx-text-fill: black;"
                    : "-fx-background-color: black; -fx-text-fill: white;");
            Cell.setDarkMode(isDarkMode);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    cells[i][j].setColorByNumber(cells[i][j].getNumber());
                }
            }
            root.requestFocus();
        });

        randomFillNumber(1);
        randomFillNumber(1);

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, key -> Platform.runLater(() -> {
            int emptyCellCheck;
            if (key.getCode() == KeyCode.DOWN) moveDown();
            else if (key.getCode() == KeyCode.UP) moveUp();
            else if (key.getCode() == KeyCode.LEFT) moveLeft();
            else if (key.getCode() == KeyCode.RIGHT) moveRight();

            emptyCellCheck = haveEmptyCell();
            if (emptyCellCheck == -1 && canNotMove()) {
                primaryStage.setScene(endGameScene);
                EndGame.getInstance().endGameShow(endGameScene, endGameRoot, primaryStage, score);
                root.getChildren().clear();
                score = 0;
            } else if (emptyCellCheck == 1) {
                randomFillNumber(2);
            }
        }));
    }
}
