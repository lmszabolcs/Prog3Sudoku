package sudoku;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @class SudokuLeaderboard
 * @brief Manages the leaderboard for Sudoku game results.
 *
 * This class handles adding entries to the leaderboard, loading leaderboard data from a file,
 * saving leaderboard data to a file, and retrieving sorted entries.
 */
public class SudokuLeaderboard {
    private static final String LEADERBOARD_FILE = "leaderboard.json"; ///< The file where leaderboard data is stored.
    private List<SudokuLeaderboardEntry> entries; ///< The list of leaderboard entries.

    /**
     * Constructs a SudokuLeaderboard and loads entries from the leaderboard file.
     */
    public SudokuLeaderboard() {
        entries = new ArrayList<>();
        loadLeaderboard();
    }

    /**
     * Adds a new entry to the leaderboard.
     *
     * @param playerName The name of the player.
     * @param difficulty The difficulty level of the completed game.
     * @param elapsedTime The time taken to complete the game in seconds.
     *
     * The entries are sorted by difficulty (Hard, Medium, Easy) and then by elapsed time.
     * The updated leaderboard is saved to the file.
     */
    public void addEntry(String playerName, Difficulty difficulty, int elapsedTime) {
        entries.add(new SudokuLeaderboardEntry(playerName, difficulty, elapsedTime));
        entries.sort(Comparator.comparing(SudokuLeaderboardEntry::getDifficulty, Comparator.comparingInt(this::difficultyOrder))
                .thenComparingInt(SudokuLeaderboardEntry::getElapsedTime));
        saveLeaderboard();
    }

    /**
     * Retrieves the leaderboard entries.
     *
     * @return A list of SudokuLeaderboardEntry objects sorted by difficulty and elapsed time.
     */
    public List<SudokuLeaderboardEntry> getEntries() {
        return entries;
    }

    /**
     * Loads leaderboard entries from the file.
     *
     * This method reads the JSON file specified by `LEADERBOARD_FILE` and populates the
     * `entries` list. If the file does not exist or cannot be read, the list remains empty.
     */
    private void loadLeaderboard() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        try {
            File file = new File(LEADERBOARD_FILE);
            if (file.exists()) {
                entries = mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, SudokuLeaderboardEntry.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the leaderboard entries to the file.
     *
     * This method writes the `entries` list to the JSON file specified by `LEADERBOARD_FILE`.
     */
    private void saveLeaderboard() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(LEADERBOARD_FILE), entries);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines the sorting order for difficulties.
     *
     * @param difficulty The difficulty level to evaluate.
     * @return An integer representing the order: Hard (0), Medium (1), Easy (2).
     */
    private int difficultyOrder(Difficulty difficulty) {
        return switch (difficulty) {
            case HARD -> 0;
            case MEDIUM -> 1;
            case EASY -> 2;
        };
    }
}