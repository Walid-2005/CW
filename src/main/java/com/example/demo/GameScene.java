package com.example.demo;


import javafx.util.Duration;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
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
import java.util.*;

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
    private Account currentAccount;
    private boolean leaderboardVisible = false;
    private final List<Text> leaderboardTexts = new ArrayList<>();

    static void setN(int number) {
        n = number;
        LENGTH = (HEIGHT - ((n + 1) * distanceBetweenCells)) / (double) n;
    }

    static double getLENGTH() {
        return LENGTH;
    }

    void game(Scene gameScene, Group root, Stage primaryStage, Scene endGameScene, Group endGameRoot, Account account) {
        this.root = root;
        gameScene.getStylesheets().add(getClass().getResource("/com/example/demo/home_buttons.css").toExternalForm());
        this.currentAccount = account;
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
        logoView.setLayoutX((Main.WIDTH - 240) / 2.0);
        logoView.setLayoutY(-80);
        root.getChildren().add(logoView);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double offsetX = (Main.WIDTH - ((n * LENGTH) + (n + 1) * distanceBetweenCells)) / 2;
                double offsetY = (Main.HEIGHT - ((n * LENGTH) + (n + 1) * distanceBetweenCells)) / 2;
                cells[i][j] = new Cell(offsetX + (j) * LENGTH + (j + 1) * distanceBetweenCells,
                        offsetY + (i) * LENGTH + (i + 1) * distanceBetweenCells, LENGTH, root);
            }
        }

        Button newGameButton = new Button("New Game");
        newGameButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        newGameButton.setFont(Font.font("Arial", 13));
        newGameButton.setLayoutX((Main.WIDTH - -160) / 2.0);
        newGameButton.setLayoutY(logoView.getLayoutY() + logoView.getFitHeight() + 210);
        root.getChildren().add(newGameButton);
        newGameButton.setOnAction(e -> { 
            Main.restartGame(primaryStage);
            Cell.setDarkMode(false);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    cells[i][j].setColorByNumber(cells[i][j].getNumber());
                }
            }
        
        
        });



        Text label = new Text("SCORE:");
        label.setFont(Font.font("Arial", 18));
        label.setFill(Color.WHITE);
        label.relocate(790, 16);
        root.getChildren().add(label);

        scoretext = new Text("0");
        scoretext.setFont(Font.font("Arial", 18));
        scoretext.setFill(Color.WHITE);
        scoretext.relocate(859, 16);
        root.getChildren().add(scoretext);

        highScoreText = new Text("HIGH SCORE: " + highScore);
        highScoreText.setFont(Font.font("Arial", 18));
        highScoreText.setFill(Color.WHITE);
        highScoreText.relocate(20, 20);
        root.getChildren().add(highScoreText);

                Button leaderboardButton = new Button("Leaderboard");
                leaderboardButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
                leaderboardButton.setFont(Font.font("Arial", 13));
                leaderboardButton.setLayoutX(410);
                leaderboardButton.setLayoutY(130);
                root.getChildren().add(leaderboardButton);

                leaderboardButton.setOnAction(e -> {
            if (!leaderboardVisible) {
                leaderboardTexts.clear();
                Collections.sort(Account.getAccounts());
                int y = 220;
                Text title = new Text("🏆 Leaderboard");
                title.setFont(Font.font("Arial", 24));
                title.setFill(Color.WHITE);
                title.setX(320);
                title.setY(y);
                leaderboardTexts.add(title);
                root.getChildren().add(title);
                y += 40;
                for (int i = 0; i < Math.min(Account.getAccounts().size(), 5); i++) {
                    Account acc = Account.getAccounts().get(i);
                    Text t = new Text((i + 1) + ". " + acc.getUserName() + ": " + acc.getScore());
                    t.setFont(Font.font("Arial", 18));
                    t.setFill(Color.WHITE);
                    t.setX(250);
                    t.setY(y);
                    leaderboardTexts.add(t);
                    root.getChildren().add(t);
                    y += 30;
                }
                leaderboardVisible = true;
            } else {
                for (Text t : leaderboardTexts) {
                    root.getChildren().remove(t);
                }
                leaderboardVisible = false;
            }
            root.requestFocus(); // to re-enable keyboard input
        });


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
       
        //First Two numbers to be called on grid
        randomFillNumber(1);
        randomFillNumber(1);

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, key -> Platform.runLater(() -> {
            int emptyCellCheck;
            if (key.getCode() == KeyCode.DOWN) moveDown();
            else if (key.getCode() == KeyCode.UP) moveUp();
            else if (key.getCode() == KeyCode.LEFT) moveLeft();
            else if (key.getCode() == KeyCode.RIGHT) moveRight();
            else return;
            emptyCellCheck = haveEmptyCell();
            if (emptyCellCheck == -1 && canNotMove()) {
                currentAccount.addToScore(score);
                Account.saveAccounts();
                primaryStage.setScene(endGameScene);
                EndGame.getInstance().endGameShow(endGameScene, endGameRoot, primaryStage, score);
                root.getChildren().clear();
                score = 0;
            } else if (emptyCellCheck == 1) {
                randomFillNumber(2);
            }
        }));
    }
    
   private void animateMovements(List<Movement> moves, Runnable onFinished) {
    SequentialTransition seq = new SequentialTransition();

    for (Movement m : moves) {
        Cell fromCell = cells[m.fromRow][m.fromCol];
        Cell toCell = cells[m.toRow][m.toCol];

        TranslateTransition trans = new TranslateTransition(Duration.millis(100), fromCell.getRectangle());
        trans.setToX((toCell.getX() - fromCell.getX()));
        trans.setToY((toCell.getY() - fromCell.getY()));

        TranslateTransition textTrans = new TranslateTransition(Duration.millis(100), fromCell.getText());
        textTrans.setToX((toCell.getX() - fromCell.getX()));
        textTrans.setToY((toCell.getY() - fromCell.getY()));

        ParallelTransition pt = new ParallelTransition(trans, textTrans);
        pt.setOnFinished(e -> {
            if (m.value != 0) {
                toCell.setValue(m.value);
                fromCell.setValue(0);
            }
            // Reset translations to avoid ghosts
            fromCell.getRectangle().setTranslateX(0);
            fromCell.getRectangle().setTranslateY(0);
            fromCell.getText().setTranslateX(0);
            fromCell.getText().setTranslateY(0);
        });

        seq.getChildren().add(pt);
    }

    seq.setOnFinished(e -> {
        if (onFinished != null) onFinished.run();
    });

    seq.play();
}







    // Movement and Helper Methods

    private void randomFillNumber(int turn) {
        Cell[][] emptyCells = new Cell[n][n];
        int a = 0, b = 0, aForBound = 0, bForBound = 0;
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
                        if (a == n) break outer;
                    }
                }
            }
        }
        if (a == 0 && b == 0) return;

        Random random = new Random();
        boolean putTwo = random.nextDouble() < 0.9;
        int xCell = random.nextInt(aForBound + 1);
        int yCell = random.nextInt(bForBound + 1);
        int val = putTwo ? 2 : 4;
        Text text = textMaker.madeText(Integer.toString(val), emptyCells[xCell][yCell].getX(), emptyCells[xCell][yCell].getY(), root);
        emptyCells[xCell][yCell].setTextClass(text);
        root.getChildren().add(text);
        emptyCells[xCell][yCell].setColorByNumber(val);
    }

    private int haveEmptyCell() {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                if (cells[i][j].getNumber() == 0) return 1;
                if (cells[i][j].getNumber() == 2048) return 0;
            }
        return -1;
    }
    public class Movement {
        int fromRow, fromCol, toRow, toCol, value;

        public Movement(int fromRow, int fromCol, int toRow, int toCol, int value) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.value = value;
    }
}

    private boolean haveSameNumberNearly(int i, int j) {
        return (i < n - 1 && cells[i + 1][j].getNumber() == cells[i][j].getNumber()) ||
               (j < n - 1 && cells[i][j + 1].getNumber() == cells[i][j].getNumber());
    }

    private boolean canNotMove() {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (haveSameNumberNearly(i, j)) return false;
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



    private boolean isFirstMove = true;

private void moveLeft() {
    List<Movement> moves = new ArrayList<>();

    for (int i = 0; i < n; i++) {
        System.out.println("Row " + i + " -----------------------------");
        
        int[] originalRow = new int[n];
        for (int j = 0; j < n; j++) {
            originalRow[j] = cells[i][j].getValue();
        }

        int[] newRow = new int[n];
        boolean[] merged = new boolean[n];
        int insertCol = 0;

        for (int j = 0; j < n; j++) {
            int val = originalRow[j];
            if (val == 0) continue;

            if (insertCol > 0 && newRow[insertCol - 1] == val && !merged[insertCol - 1]) {
                newRow[insertCol - 1] *= 2;
                merged[insertCol - 1] = true;
                score += newRow[insertCol - 1];
                updateScore();
                moves.add(new Movement(i, j, i, insertCol - 1, newRow[insertCol - 1]));
                System.out.println("Merged: (" + i + "," + j + ") → (" + i + "," + (insertCol - 1) + ") = " + newRow[insertCol - 1]);
            } else {
                newRow[insertCol] = val;
                if (j != insertCol) {
                    moves.add(new Movement(i, j, i, insertCol, val));
                    System.out.println("Moved: (" + i + "," + j + ") → (" + i + "," + insertCol + ") = " + val);
                } else {
                    System.out.println("Stays in place: (" + i + "," + j + ") at (" + i + "," + insertCol + ")");
                }
                insertCol++;
            }
        }
    }
   if (isFirstMove) {
    animateMovements(moves, null);
    isFirstMove = false;
} else {
    animateMovements(moves, () -> randomFillNumber(1));
}


}

private void moveRight() {
    List<Movement> moves = new ArrayList<>();

    for (int i = 0; i < n; i++) {
        System.out.println("Row " + i + " -----------------------------");

        int[] originalRow = new int[n];
        for (int j = 0; j < n; j++) {
            originalRow[j] = cells[i][j].getValue();
        }

        int[] newRow = new int[n];
        boolean[] merged = new boolean[n];
        int insertCol = n - 1;

        for (int j = n - 1; j >= 0; j--) {
            int val = originalRow[j];
            if (val == 0) continue;

            if (insertCol < n - 1 && newRow[insertCol + 1] == val && !merged[insertCol + 1]) {
                newRow[insertCol + 1] *= 2;
                merged[insertCol + 1] = true;
                score += newRow[insertCol + 1];
                updateScore();
                moves.add(new Movement(i, j, i, insertCol + 1, newRow[insertCol + 1]));
                System.out.println("Merged: (" + i + "," + j + ") → (" + i + "," + (insertCol + 1) + ") = " + newRow[insertCol + 1]);
            } else {
                newRow[insertCol] = val;
                if (j != insertCol) {
                    moves.add(new Movement(i, j, i, insertCol, val));
                    System.out.println("Moved: (" + i + "," + j + ") → (" + i + "," + insertCol + ") = " + val);
                } else {
                    System.out.println("Stays in place: (" + i + "," + j + ") at (" + i + "," + insertCol + ")");
                }
                insertCol--;
            }
        }
    }
    if (isFirstMove) {
    animateMovements(moves, null);
    isFirstMove = false;
} else {
    animateMovements(moves, () -> randomFillNumber(1));
}

}

    

   private void moveUp() {
    List<Movement> moves = new ArrayList<>();

    for (int j = 0; j < n; j++) {
        System.out.println("Column " + j + " ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");

        int[] originalCol = new int[n];
        for (int i = 0; i < n; i++) {
            originalCol[i] = cells[i][j].getValue();
        }

        int[] newCol = new int[n];
        boolean[] merged = new boolean[n];
        int insertRow = 0;

        for (int i = 0; i < n; i++) {
            int val = originalCol[i];
            if (val == 0) continue;

            if (insertRow > 0 && newCol[insertRow - 1] == val && !merged[insertRow - 1]) {
                newCol[insertRow - 1] *= 2;
                merged[insertRow - 1] = true;
                score += newCol[insertRow - 1];
                updateScore();
                moves.add(new Movement(i, j, insertRow - 1, j, newCol[insertRow - 1]));
                System.out.println("Merged: (" + i + "," + j + ") → (" + (insertRow - 1) + "," + j + ") = " + newCol[insertRow - 1]);
            } else {
                newCol[insertRow] = val;
                if (i != insertRow) {
                    moves.add(new Movement(i, j, insertRow, j, val));
                    System.out.println("Moved: (" + i + "," + j + ") → (" + insertRow + "," + j + ") = " + val);
                } else {
                    System.out.println("Stays in place: (" + i + "," + j + ") at (" + insertRow + "," + j + ")");
                }
                insertRow++;
            }
        }
    }
    if (isFirstMove) {
    animateMovements(moves, null);
    isFirstMove = false;
} else {
    animateMovements(moves, () -> randomFillNumber(1));
}

}



    private void moveDown() {
    List<Movement> moves = new ArrayList<>();

    for (int j = 0; j < n; j++) {
        System.out.println("Column " + j + " ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");

        int[] originalCol = new int[n];
        for (int i = 0; i < n; i++) {
            originalCol[i] = cells[i][j].getValue();
        }

        int[] newCol = new int[n];
        boolean[] merged = new boolean[n];
        int insertRow = n - 1;

        for (int i = n - 1; i >= 0; i--) {
            int val = originalCol[i];
            if (val == 0) continue;

            if (insertRow < n - 1 && newCol[insertRow + 1] == val && !merged[insertRow + 1]) {
                newCol[insertRow + 1] *= 2;
                merged[insertRow + 1] = true;
                score += newCol[insertRow + 1];
                updateScore();
                moves.add(new Movement(i, j, insertRow + 1, j, newCol[insertRow + 1]));
                System.out.println("Merged: (" + i + "," + j + ") → (" + (insertRow + 1) + "," + j + ") = " + newCol[insertRow + 1]);
            } else {
                newCol[insertRow] = val;
                if (i != insertRow) {
                    moves.add(new Movement(i, j, insertRow, j, val));
                    System.out.println("Moved: (" + i + "," + j + ") → (" + insertRow + "," + j + ") = " + val);
                } else {
                    System.out.println("Stays in place: (" + i + "," + j + ") at (" + insertRow + "," + j + ")");
                }
                insertRow--;
            }
        }
    }
    if (isFirstMove) {
    animateMovements(moves, null);
    isFirstMove = false;
} else {
    animateMovements(moves, () -> randomFillNumber(1));
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
        // Valid merge
        score += cells[i][j].adder(cells[des + sign][j]);
        updateScore();
        cells[des][j].setModify(true);
    } else if (des != i && cells[des][j].getValue() == 0) {
        // Only move if destination is empty
        cells[i][j].changeCell(cells[des][j]);
    }
}



  
    private boolean isValidDesH(int i, int j, int des, int sign) {
        return (des + sign < n && des + sign >= 0 &&
                cells[i][des + sign].getNumber() == cells[i][j].getNumber() &&
                !cells[i][des + sign].getModify() &&
                cells[i][des + sign].getNumber() != 0);
    }

    private boolean isValidDesV(int i, int j, int des, int sign) {
        return (des + sign < n && des + sign >= 0 &&
                cells[des + sign][j].getNumber() == cells[i][j].getNumber() &&
                !cells[des + sign][j].getModify() &&
                cells[des + sign][j].getNumber() != 0);
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
}
