## Compilation Instructions

- **Step 1:** Ensure all Java classes (`Main`, `GameScene`, `Cell`, `Account`, etc.) are in the same package (e.g., `com.example.demo`) under `src/main/java/`.

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
- **Note:** IntelliJ may display warnings in `pom.xml`, but these can be safely ignored as long as the project builds and runs successfully.

## Requirements

- Java 19 or compatible version
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
  Automatically detects when no valid moves are left and displays a game over screen.

- **Visual Enhancements:**  
  Includes a 2048 logo image (created by myself on Canva), animated background GIF, and autumn-themed color-coded tiles (to match background) for a polished UI.


