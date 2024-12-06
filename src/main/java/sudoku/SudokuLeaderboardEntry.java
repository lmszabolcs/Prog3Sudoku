package sudoku;

/**
 * @class SudokuLeaderboardEntry
 * @brief Represents an entry in the Sudoku leaderboard.
 *
 * This class stores the player's name, the difficulty level of the game, and the time taken to complete the game.
 */
public class SudokuLeaderboardEntry {
    private String playerName; ///< The name of the player.
    private Difficulty difficulty; ///< The difficulty level of the game.
    private int elapsedTime; ///< The time taken to complete the game in seconds.

    /**
     * Default constructor for SudokuLeaderboardEntry.
     */
    public SudokuLeaderboardEntry() {
    }

    /**
     * Constructs a SudokuLeaderboardEntry with the specified player name, difficulty, and elapsed time.
     *
     * @param playerName The name of the player.
     * @param difficulty The difficulty level of the game.
     * @param elapsedTime The time taken to complete the game in seconds.
     */
    public SudokuLeaderboardEntry(String playerName, Difficulty difficulty, int elapsedTime) {
        this.playerName = playerName;
        this.difficulty = difficulty;
        this.elapsedTime = elapsedTime;
    }

    /**
     * Gets the player's name.
     *
     * @return The name of the player.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Gets the difficulty level of the game.
     *
     * @return The difficulty level.
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Gets the time taken to complete the game.
     *
     * @return The elapsed time in seconds.
     */
    public int getElapsedTime() {
        return elapsedTime;
    }
}