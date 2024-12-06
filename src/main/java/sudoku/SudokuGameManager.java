package sudoku;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @class SudokuGameManager
 * @brief Manages the Sudoku game state, including saving, loading, and tracking progress.
 * <p>
 * This class provides methods to save and load the current game state, manage the elapsed time,
 * and interact with the leaderboard. It uses a 9x9 grid of JTextFields to represent the Sudoku board.
 */
public class SudokuGameManager {

    private final JTextField[][] grid;
/// < The elapsed time in seconds for the current game.
    private final SudokuLeaderboard leaderboard = new SudokuLeaderboard();
        /// < The 9x9 grid representing the Sudoku board.
    private int elapsedTime; ///< The leaderboard manager.

    /**
     * Constructs a SudokuGameManager with a given grid and initial elapsed time.
     *
     * @param grid        The 9x9 grid of JTextFields representing the Sudoku board.
     * @param elapsedTime The initial elapsed time in seconds.
     */
    public SudokuGameManager(JTextField[][] grid, int elapsedTime) {
        this.grid = grid;
        this.elapsedTime = elapsedTime;
    }

    /**
     * Saves the current game state to a JSON file.
     *
     * @param saveName   The name of the save file (without extension).
     * @param difficulty The difficulty level of the game.
     *                   <p>
     *                   This method saves:
     *                   - The current grid values.
     *                   - The original grid values.
     *                   - The elapsed time.
     *                   - The difficulty level.
     *                   <p>
     *                   If the grid is empty, the save operation is aborted with a warning message.
     */
    public void saveGame(String saveName, Difficulty difficulty) {
        if (isGridEmpty()) {
            JOptionPane.showMessageDialog(null, "Cannot save an empty grid.");
            return;
        }

        Map<String, Object> gameState = new HashMap<>();
        int[][] currentGrid = new int[9][9];
        int[][] originalGrid = new int[9][9];

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String text = grid[row][col].getText();
                currentGrid[row][col] = text.isEmpty() ? 0 : Integer.parseInt(text);
                originalGrid[row][col] = grid[row][col].isEditable() ? 0 : currentGrid[row][col];
            }
        }

        gameState.put("currentGrid", currentGrid);
        gameState.put("originalGrid", originalGrid);
        gameState.put("elapsedTime", elapsedTime);
        gameState.put("difficulty", difficulty);

        try {
            new ObjectMapper().writeValue(new File(saveName + ".json"), gameState);
            JOptionPane.showMessageDialog(null, "Game saved successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving game: " + e.getMessage());
        }
    }

    /**
     * Loads a game state from a JSON file.
     *
     * @param saveName The name of the save file (without extension).
     *                 <p>
     *                 This method restores:
     *                 - The current grid values.
     *                 - The original grid values (for setting editability).
     *                 - The elapsed time.
     *                 - The difficulty level.
     *                 <p>
     *                 Displays a success or error message based on the result of the operation.
     */
    public void loadGame(String saveName) {
        try {
            Map<String, Object> gameState = new ObjectMapper().readValue(new File(saveName + ".json"), Map.class);
            List<List<Integer>> currentGridList = (List<List<Integer>>) gameState.get("currentGrid");
            List<List<Integer>> originalGridList = (List<List<Integer>>) gameState.get("originalGrid");
            elapsedTime = (int) gameState.get("elapsedTime");
            String difficulty = (String) gameState.get("difficulty");

            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    int value = currentGridList.get(row).get(col);
                    grid[row][col].setText(value == 0 ? "" : String.valueOf(value));
                    SudokuCellFormatter.formatCell(grid[row][col], row, col, originalGridList.get(row).get(col) == 0);
                }
            }

            JOptionPane.showMessageDialog(null, "Game loaded successfully. Difficulty: " + difficulty);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading game: " + e.getMessage());
        }
    }

    /**
     * Retrieves the elapsed time for the current game.
     *
     * @return The elapsed time in seconds.
     */
    public int getElapsedTime() {
        return elapsedTime;
    }

    /**
     * Sets the elapsed time for the current game.
     *
     * @param elapsedTime The elapsed time in seconds.
     */
    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    /**
     * Checks if the grid is empty.
     *
     * @return True if all cells in the grid are empty, false otherwise.
     */
    public boolean isGridEmpty() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (!grid[row][col].getText().trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Saves the game result to the leaderboard.
     *
     * @param playerName The name of the player.
     * @param difficulty The difficulty level of the game.
     */
    public void saveGameResult(String playerName, Difficulty difficulty) {
        leaderboard.addEntry(playerName, difficulty, elapsedTime);
    }

    /**
     * Retrieves the leaderboard entries.
     *
     * @return A list of SudokuLeaderboardEntry objects representing the leaderboard entries.
     */
    public List<SudokuLeaderboardEntry> getLeaderboardEntries() {
        return leaderboard.getEntries();
    }
}