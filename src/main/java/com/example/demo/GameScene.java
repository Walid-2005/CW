package com.example.demo;


import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

/**
 * Main controller for the 2048 game screen.
 * <p>
 * This class sets up the grid, handles keyboard input, runs movement/merge logic,
 * updates scores and high score persistence, manages animations, theme toggling,
 * and draws the on-screen leaderboard. It also coordinates the transition to the
 * end-game screen.
 * </p>
 */
public class GameScene {
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
    private javafx.scene.shape.Rectangle leaderboardBg;
    private Text leaderboardTitle;

    /**
     * Sets the grid dimension (n x n) and recalculates the cell length based on HEIGHT.
     *
     * @param number grid size to use (e.g., 4 for a 4x4 board)
     */
    static void setN(int number) {
        n = number;
        LENGTH = (HEIGHT - ((n + 1) * distanceBetweenCells)) / (double) n;
    }

    /**
     * @return the current pixel length of a single cell, derived from HEIGHT and n
     */
    static double getLENGTH() {
        return LENGTH;
    }

    /**
     * Initializes the game scene: draws background and logo, builds the grid, wires buttons,
     * loads high score, spawns initial tiles, and registers keyboard handlers.
     *
     * @param gameScene    the JavaFX Scene for gameplay
     * @param root         the root node containing all game UI
     * @param primaryStage main application window
     * @param endGameScene the scene shown when the game ends
     * @param endGameRoot  root of the end-game UI
     * @param account      current player's account (used for leaderboard scoring)
     */
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

  // Leaderboard button
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

        // Create leaderboard background panel
        double panelW = 420;
        double panelH = 260;
        double panelX = (Main.WIDTH - panelW) / 2.0;
        double panelY = 200;

        leaderboardBg = new javafx.scene.shape.Rectangle(panelX, panelY, panelW, panelH);
        leaderboardBg.setArcWidth(24);
        leaderboardBg.setArcHeight(24);
        leaderboardBg.setFill(isDarkMode ? Color.rgb(20, 20, 20, 0.82) : Color.rgb(255, 255, 255, 0.82));
        leaderboardBg.setStroke(isDarkMode ? Color.WHITE : Color.BLACK);
        leaderboardBg.setStrokeWidth(1.2);
        leaderboardBg.setEffect(new javafx.scene.effect.DropShadow(24, Color.rgb(0, 0, 0, 0.35)));

        // Leaderboard title
        leaderboardTitle = new Text("Leaderboard");
        leaderboardTitle.setFont(Font.font("Arial", 24));
        leaderboardTitle.setFill(isDarkMode ? Color.WHITE : Color.BLACK);
        leaderboardTitle.setX(panelX + 24);
        leaderboardTitle.setY(panelY + 42);

        // Add leaderboard entries
        int max = Math.min(Account.getAccounts().size(), 5);
        double startY = panelY + 84;
        for (int i = 0; i < max; i++) {
            Account acc = Account.getAccounts().get(i);
            Text t = new Text((i + 1) + ". " + acc.getUserName() + " — " + acc.getScore());
            t.setFont(Font.font("Arial", 18));

            if (i == 0) t.setFill(Color.GOLD);
            else if (i == 1) t.setFill(Color.SILVER);
            else if (i == 2) t.setFill(Color.BROWN);
            else t.setFill(isDarkMode ? Color.WHITE : Color.BLACK);

            t.setX(panelX + 24);
            t.setY(startY + i * 34);

            leaderboardTexts.add(t);
        }

        // Add everything to root
        root.getChildren().add(leaderboardBg);
        root.getChildren().add(leaderboardTitle);
        root.getChildren().addAll(leaderboardTexts);

        leaderboardVisible = true;
    } else {
        root.getChildren().remove(leaderboardBg);
        root.getChildren().remove(leaderboardTitle);
        root.getChildren().removeAll(leaderboardTexts);
        leaderboardVisible = false;
    }
    root.requestFocus();
});

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
            else if (key.getCode() == KeyCode.T) spawnTest2048Tile();
            else return;
            emptyCellCheck = haveEmptyCell();
            if (emptyCellCheck == -1 && canNotMove()) {
                currentAccount.addToScore(score);
                currentAccount.addToScore(score);
            Account.saveAccounts();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(ev -> {
                primaryStage.setScene(endGameScene);
                EndGame.getInstance().endGameShow(endGameScene, endGameRoot, primaryStage, score);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), endGameRoot);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();

            } 
        }));
    }
    
/**
 * Runs the move animations for a batch of movements, then invokes a callback when done.
 *
 * @param moves      list of movements to animate
 * @param onFinished callback to run after animations complete (e.g., spawn tile)
 */
private void animateMovements(List<Movement> moves, Runnable onFinished) {
    if (moves.isEmpty()) return; // ← No moves = no spawn

    SequentialTransition seq = new SequentialTransition();

    for (Movement m : moves) {
        Cell fromCell = cells[m.fromRow][m.fromCol];
        Cell toCell = cells[m.toRow][m.toCol];

        TranslateTransition trans = new TranslateTransition(Duration.millis(100), fromCell.getRectangle());
        trans.setToX(toCell.getX() - fromCell.getX());
        trans.setToY(toCell.getY() - fromCell.getY());

        TranslateTransition textTrans = new TranslateTransition(Duration.millis(100), fromCell.getText());
        textTrans.setToX(toCell.getX() - fromCell.getX());
        textTrans.setToY(toCell.getY() - fromCell.getY());

        ParallelTransition pt = new ParallelTransition(trans, textTrans);
        pt.setOnFinished(e -> {
            if (m.value != 0) {
                toCell.setValue(m.value);
                fromCell.setValue(0);
            }
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

    /**
     * Spawns a new tile (2 or 4) at a random empty position and plays a pop animation.
     *
     * @param turn used to keep original behavior for initial spawns
     */
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

    Text text = textMaker.madeText(Integer.toString(val),
            emptyCells[xCell][yCell].getX(), emptyCells[xCell][yCell].getY(), root);
    emptyCells[xCell][yCell].setTextClass(text);
    root.getChildren().add(text);
    emptyCells[xCell][yCell].setColorByNumber(val);

    // POP-OUT ANIMATION
    emptyCells[xCell][yCell].getRectangle().setScaleX(0.1);
    emptyCells[xCell][yCell].getRectangle().setScaleY(0.1);
    text.setScaleX(0.1);
    text.setScaleY(0.1);

    ScaleTransition scaleRect = new ScaleTransition(Duration.millis(150), emptyCells[xCell][yCell].getRectangle());
    scaleRect.setToX(1.0);
    scaleRect.setToY(1.0);

    ScaleTransition scaleText = new ScaleTransition(Duration.millis(150), text);
    scaleText.setToX(1.0);
    scaleText.setToY(1.0);

    ParallelTransition popEffect = new ParallelTransition(scaleRect, scaleText);
    popEffect.play();
}

    /**
     * Checks whether there is an empty cell or a 2048 tile.
     *
     * @return 1 if an empty cell exists, 0 if a 2048 tile exists, -1 otherwise
     */
    private int haveEmptyCell() {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                if (cells[i][j].getNumber() == 0) return 1;
                if (cells[i][j].getNumber() == 2048) return 0;
            }
        return -1;
    }

    /**
     * Immutable record of a single tile movement or merge during a move step.
     */
    public class Movement {
        int fromRow, fromCol, toRow, toCol, value;

        /**
         * Constructs a movement from one cell to another, optionally carrying a merged value.
         *
         * @param fromRow source row
         * @param fromCol source column
         * @param toRow   destination row
         * @param toCol   destination column
         * @param value   resulting value to set at destination (0 if only a slide)
         */
        public Movement(int fromRow, int fromCol, int toRow, int toCol, int value) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.value = value;
    }
}

    /**
     * @return true if any horizontally/vertically adjacent pair shares the same number
     */
    private boolean haveSameNumberNearly(int i, int j) {
        return (i < n - 1 && cells[i + 1][j].getNumber() == cells[i][j].getNumber()) ||
               (j < n - 1 && cells[i][j + 1].getNumber() == cells[i][j].getNumber());
    }

    /**
     * @return true if no more valid merges are possible anywhere on the board
     */
    private boolean canNotMove() {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (haveSameNumberNearly(i, j)) return false;
        return true;
    }

    /**
     * Updates the score text and persists a higher high score when reached.
     */
    private void updateScore() {
        scoretext.setText(score + "");
        if (score > highScore) {
            highScore = score;
            highScoreText.setText("HIGH SCORE: " + highScore);
            saveHighScore();
        }
    }
        
    /**
     * Loads the saved high score from file. Falls back to 0 if missing or invalid.
     */
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

    /**
     * Writes the current high score to file.
     */
    private void saveHighScore() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(highScoreFile));
            writer.write(Long.toString(highScore));
            writer.close();
        } catch (IOException ignored) {}
    }
    
    /**
     * Debug helper: force-spawn a 2048 tile and show a congratulatory popup.
     */
    private void spawnTest2048Tile() {
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            if (cells[i][j].getValue() == 0) {
                cells[i][j].setValue(2048);
                show2048Popup(); 
                return;
            }
        }
    }
}

    private boolean reached2048 = false;

    /**
     * Shows a temporary image in the center of the screen and fades it out.
     */
private void show2048Popup() {
    Image image = new Image(getClass().getResource("/com/example/demo/congrats_2048.png").toExternalForm());
    ImageView popupImage = new ImageView(image);

    // Optional: Resize to fit nicely
    popupImage.setFitWidth(300);
    popupImage.setPreserveRatio(true);

    // Center the image on screen
    popupImage.setLayoutX((Main.WIDTH - popupImage.getFitWidth()) / 2);
    popupImage.setLayoutY((Main.HEIGHT - 200) / 2);

    root.getChildren().add(popupImage);

    // Fade out over 6 seconds
    FadeTransition fade = new FadeTransition(Duration.seconds(6), popupImage);
    fade.setFromValue(1.0);
    fade.setToValue(0.0);
    fade.setOnFinished(e -> root.getChildren().remove(popupImage));
    fade.play();
}

    private boolean isFirstMove = true;

    /**
     * Handles a left move: compacts values leftward, merges once per tile, updates score,
     * collects animation steps, and spawns a new tile on completion.
     */
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

    if (!moves.isEmpty()) {
        if (isFirstMove) {
            isFirstMove = false;
            animateMovements(moves, () -> randomFillNumber(2));
        } else {
            animateMovements(moves, () -> randomFillNumber(1));
        }
    } else {
        // Fallback: no movement but check if a tile can be spawned
        if (!isFirstMove && haveEmptyCell() == 1) {
            randomFillNumber(1);
        }
    }
}

    /**
     * Handles a right move (mirror of left move).
     */
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

    if (!moves.isEmpty()) {
        if (isFirstMove) {
            isFirstMove = false;
            animateMovements(moves, () -> randomFillNumber(2));
        } else {
            animateMovements(moves, () -> randomFillNumber(1));
        }
    } else {
        if (!isFirstMove && haveEmptyCell() == 1) {
            randomFillNumber(1);
        }
    }
}


    
    /**
     * Handles an up move: compacts upward, merges once per tile, updates score,
     * collects animations, and spawns a new tile on completion.
     */
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

    if (!moves.isEmpty()) {
        if (isFirstMove) {
            isFirstMove = false;
            animateMovements(moves, () -> randomFillNumber(2));
        } else {
            animateMovements(moves, () -> randomFillNumber(1));
        }
    } else {
        if (!isFirstMove && haveEmptyCell() == 1) {
            randomFillNumber(1);
        }
    }
}

    /**
     * Handles a down move (mirror of up move).
     */
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

    if (!moves.isEmpty()) {
        if (isFirstMove) {
            isFirstMove = false;
            animateMovements(moves, () -> randomFillNumber(2));
        } else {
            animateMovements(moves, () -> randomFillNumber(1));
        }
    } else {
        if (!isFirstMove && haveEmptyCell() == 1) {
            randomFillNumber(1);
        }
    }
}



  
    /**
     * Legacy helper used by earlier movement logic to move horizontally with merge checks.
     */
  private void moveHorizontally(int i, int j, int des, int sign) {
        if (isValidDesH(i, j, des, sign)) {
            score += cells[i][j].adder(cells[i][des + sign]);
            updateScore();
            cells[i][des].setModify(true);
        } else if (des != j) {
            cells[i][j].changeCell(cells[i][des]);
        }
    }

    /**
     * Legacy helper used by earlier movement logic to move vertically with merge checks.
     */
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



  
    /**
     * Validates a horizontal merge destination.
     */
    private boolean isValidDesH(int i, int j, int des, int sign) {
        return (des + sign < n && des + sign >= 0 &&
                cells[i][des + sign].getNumber() == cells[i][j].getNumber() &&
                !cells[i][des + sign].getModify() &&
                cells[i][des + sign].getNumber() != 0);
    }

    /**
     * Validates a vertical merge destination.
     */
    private boolean isValidDesV(int i, int j, int des, int sign) {
        return (des + sign < n && des + sign >= 0 &&
                cells[des + sign][j].getNumber() == cells[i][j].getNumber() &&
                !cells[des + sign][j].getModify() &&
                cells[des + sign][j].getNumber() != 0);
    }

    /**
     * Finds the furthest empty coordinate a tile can slide to in a given direction.
     *
     * @param i      row index
     * @param j      column index
     * @param direct direction code: 'l', 'r', 'u', 'd'
     * @return destination coordinate along the axis being moved
     */
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
