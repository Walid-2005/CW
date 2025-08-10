## GitHub Repo

[https://github.com/Walid-2005/CW.git](https://github.com/Walid-2005/CW.git)

## Compilation Instructions

- **Step 1:** Ensure all Java classes (`Main`, `GameScene`, `Cell`, `Account`, etc.) are in the same package ( `com.example.demo`) under `src/main/java/`.

- **Step 2:** Make sure JavaFX is installed or configured. This project uses Maven to handle JavaFX dependencies via the `javafx-maven-plugin`.

- **Step 3:** Compile and run the application:

    - **Via Terminal:**
      ```bash
      mvn clean javafx:run
      ```

    - **Via IntelliJ IDEA:**
        1. Open the project in IntelliJ.
        2. Wait for Maven to load dependencies.
        3. Right-click the `Main` class and select **Run**.

## Requirements

- Java 17 or compatible version
- Maven 3.x
- JavaFX SDK (managed automatically via Maven)


## Implemented and Working Properly

- **Tile Movement & Merging:**  
  Fully functional 2048 mechanics with merging and shifting logic based on user input (arrow keys only).

- **Animations:**  
  Smooth transitions for tile movements using `TranslateTransition` to enhance visual flow. A Random tile pop-up animations was also implemented.

- **Dark Mode Toggle:**  
  Users can dynamically switch between light and dark themes via a toggle button.

- **User Accounts:**  
  Allows players to enter a username. Score is tracked per user during the session.

- **Leaderboard:**  
  Displays top scores from all users during the current game session.

- **Restart & Quit Buttons:**  
  Functional buttons that allow players to restart the game or quit the application.

- **Game Over Detection and moves over :**  
  Automatically detects when no valid moves are left and displays a game over screen with a smooth animation.

- **Visual Enhancements:**  
  Includes a 2048 logo image (created by myself on Canva), animated background GIF, and autumn-themed color-coded tiles (to match background) for a polished UI.

- **Win Popup animation:**
  When the player reaches the 2048 tile, a popup animation is displayed to celebrate the win.  
  This animation briefly overlays a congratulatory message (created by myself on Canva)  on the game board using a fade-in and fade-out effect, providing a clear visual 
  cue that the player has achieved the main goal of the game but is allowed to continue.


## Implemented but Not Working Properly

- **Leaderboard Updates on Mid-Game Quit:**  
  If the player quits the game mid-session, even with a score high enough to qualify for the leaderboard, 
  the score is not recorded. This happens because leaderboard updates are only triggered at the end of a completed game, not upon application exit.

## Features Not Implemented

- **New Levels:**  
  No new levels were implemented as 2048 is  not a level-based game, 
  gameplay is designed around continuous play until no moves remain.

- **Music/Sound Effects:**  
  Background music and sound effects were not included in the final version 
  as it doesn't match the classic 2048 gameplay experience.


## New Java Classes

- **EndGame** :  
  Handles the display and functionality of the game-over screen, including showing the final score, providing options to restart or quit, 
  and integrating with the leaderboard.


## Modified Java Classes

- **GameScene.java**:  
  Expanded to include dark mode toggle, leaderboard button, animations for tile movement, and enhanced game-over handling.

- **Main.java**:  
  Modified to prompt for username, load and save accounts, and handle leaderboard display.

- **Cell.java**:  
  Updated to support dynamic color changes based on dark/light mode and improved tile rendering logic.

- **TextMaker.java**:  
  Adjusted font sizing logic for better scaling and tile text clarity.

## Unexpected Problems

- **Tile Disappearance Bug**:  
  During movement animation testing, certain tiles would disappear. This was traced to animation logic conflicts in `Cell` and `GameScene`. 
  The solution was to reset the movement logic to a stable version before reintroducing animations in a controlled way.

- **Dark Mode Tile Color Not Changing**:  
  When the dark mode toggle was first implemented, the tile colors did not update when switching themes. This was due to the color update logic in `Cell.java` not being called on mode change.  
  The fix was to add a method to refresh all cells whenever dark mode is toggled so that the colors update instantly.

- **High Score Not Saving Between Sessions**:  
  Initially, the high score would reset after restarting the game or relaunching the application. This occurred because the high score file was not being saved in the correct persistent location (`src/main/resources/`).  
  The issue was resolved by ensuring the score is written to and read from this fixed location during save/load operations.
