package com.example.demo;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
    private Text scoretext;
    private boolean isDarkMode = false;
    private ImageView backgroundView;

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
            scoretext.setText(score + "");
            cells[i][des].setModify(true);
        } else if (des != j) {
            cells[i][j].changeCell(cells[i][des]);
        }
    }

    private void moveVertically(int i, int j, int des, int sign) {
        if (isValidDesV(i, j, des, sign)) {
            score += cells[i][j].adder(cells[des + sign][j]);
            scoretext.setText(score + "");
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

    void game(Scene gameScene, Group root, Stage primaryStage, Scene endGameScene, Group endGameRoot) {
        this.root = root;

        Image background = new Image(getClass().getResource("background.gif").toExternalForm());
        backgroundView = new ImageView(background);
        backgroundView.setFitWidth(Main.WIDTH);
        backgroundView.setFitHeight(Main.HEIGHT);
        backgroundView.setPreserveRatio(false);
        backgroundView.setMouseTransparent(true); // <- required fix
        root.getChildren().add(0, backgroundView);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double offsetX = (Main.WIDTH - ((n * LENGTH) + (n + 1) * distanceBetweenCells)) / 2;
                double offsetY = (Main.HEIGHT - ((n * LENGTH) + (n + 1) * distanceBetweenCells)) / 2;
                cells[i][j] = new Cell(offsetX + (j) * LENGTH + (j + 1) * distanceBetweenCells,
                        offsetY + (i) * LENGTH + (i + 1) * distanceBetweenCells, LENGTH, root);
            }
        }

        Text label = new Text("SCORE :");
        label.setFont(Font.font(30));
        label.relocate(750, 100);
        root.getChildren().add(label);

        scoretext = new Text("0");
        scoretext.setFont(Font.font(20));
        scoretext.relocate(750, 150);
        root.getChildren().add(scoretext);

        Button toggleMode = new Button("Dark Mode");
        toggleMode.setLayoutX(750);
        toggleMode.setLayoutY(200);
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
                    root.requestFocus();
                }
            }
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
