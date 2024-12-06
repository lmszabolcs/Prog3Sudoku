package sudoku;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @class SudokuGUI
 * @brief Provides the graphical user interface for the Sudoku game.
 * <p>
 * This class handles the layout, event handling, and interaction logic for a Sudoku game.
 * It supports creating new games, saving/loading games, checking the grid, displaying a leaderboard,
 * and managing a timer.
 */
public class SudokuGUI extends JFrame {

    private final JTextField[][] grid = new JTextField[9][9]; ///< The 9x9 grid for the Sudoku game.
    private final SudokuGameManager gameManager; ///< Manages game logic and persistence.
    private Timer timer; ///< Timer to track elapsed game time.
    private JLabel timerLabel; ///< Label to display the elapsed game time.
    private int elapsedTime = 0; ///< Tracks elapsed game time in seconds.
    private boolean isTimerRunning = false; ///< Indicates if the timer is running.
    private Difficulty currentDifficulty; ///< The difficulty level of the current game.

    /**
     * Constructs a new SudokuGUI and initializes its components.
     * <p>
     * The constructor sets up the main window, menu bar, grid panel, action panel,
     * and timer, and makes the GUI visible.
     */
    public SudokuGUI() {
        setTitle("Sudoku Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLayout(new BorderLayout());

        gameManager = new SudokuGameManager(grid, elapsedTime);

        initializeMenuBar();
        initializeGridPanel();
        initializeActionPanel();
        initializeTimer();

        setVisible(true);
    }

    /**
     * The entry point for the application. Creates and displays the Sudoku GUI.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SudokuGUI::new);
    }

    /**
     * Initializes the menu bar with options for a new game, saving, loading, and viewing the leaderboard.
     */
    private void initializeMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenu fileItem = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem loadItem = new JMenuItem("Load");
        JMenuItem leaderboardItem = new JMenuItem("Leaderboard");

        menuBar.add(newGameItem);
        menuBar.add(fileItem);
        fileItem.add(saveItem);
        fileItem.add(loadItem);
        menuBar.add(leaderboardItem);
        setJMenuBar(menuBar);

        newGameItem.addActionListener(this::handleNewGame);
        saveItem.addActionListener(this::handleSaveGame);
        loadItem.addActionListener(this::handleLoadGame);
        leaderboardItem.addActionListener(e -> displayLeaderboard());
    }

    /**
     * Initializes the grid panel containing the 9x9 Sudoku cells.
     * <p>
     * Each cell is formatted and configured to accept only valid input through a document filter.
     * A document listener is added to each cell to detect when the grid is filled.
     */
    private void initializeGridPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(9, 9));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                grid[row][col] = new JTextField();
                SudokuCellFormatter.formatCell(grid[row][col], row, col, false);
                ((AbstractDocument) grid[row][col].getDocument()).setDocumentFilter(new SudokuCellFilter());
                grid[row][col].getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        checkIfGridIsFilled();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        checkIfGridIsFilled();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        checkIfGridIsFilled();
                    }
                });
                gridPanel.add(grid[row][col]);
            }
        }
        add(gridPanel, BorderLayout.CENTER);
    }

    /**
     * Checks if the grid is fully filled and validates it if true.
     */
    private void checkIfGridIsFilled() {
        boolean allCellsFilled = true;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (grid[row][col].getText().isEmpty()) {
                    allCellsFilled = false;
                    break;
                }
            }
            if (!allCellsFilled) {
                break;
            }
        }
        if (allCellsFilled) {
            handleCheck(null);
        }
    }

    /**
     * Initializes the action panel containing buttons for checking the grid.
     */
    private void initializeActionPanel() {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        JButton checkButton = new JButton("Check");

        checkButton.addActionListener(this::handleCheck);

        actionPanel.add(checkButton);
        actionPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        add(actionPanel, BorderLayout.EAST);
    }

    /**
     * Initializes the timer and its associated label.
     * <p>
     * The timer updates the elapsed time every second while running.
     */
    private void initializeTimer() {
        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(timerLabel, BorderLayout.NORTH);

        timer = new Timer(1000, e -> {
            if (isTimerRunning) {
                elapsedTime++;
                updateTimerLabel();
            }
        });
    }

    /**
     * Handles the creation of a new Sudoku game based on the selected difficulty.
     *
     * @param e The ActionEvent triggered by the "New Game" menu item.
     */
    private void handleNewGame(ActionEvent e) {
        pauseTimer();
        String[] options = {"Cancel", "Easy", "Medium", "Hard"};
        int response = JOptionPane.showOptionDialog(null,
                "Choose Difficulty Level",
                "New Game",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);

        if (response > 0) {
            startNewGame( Difficulty.valueOf(options[response].toUpperCase()));
            resumeTimer();
        }
    }

    /**
     * Handles saving the current game state.
     *
     * @param e The ActionEvent triggered by the "Save" menu item.
     */
    private void handleSaveGame(ActionEvent e) {
        if (gameManager.isGridEmpty()) {
            JOptionPane.showMessageDialog(this, "Cannot save an empty grid.");
            return;
        }

        pauseTimer();
        while (true) {
            String saveName = JOptionPane.showInputDialog(this, "Enter save name:");
            if (saveName == null) {
                resumeTimer();
                return;
            }
            if (!saveName.trim().isEmpty()) {
                if (saveName.equalsIgnoreCase("leaderboard")) {
                    JOptionPane.showMessageDialog(this, "Save name cannot be 'leaderboard'.");
                    continue;
                }
                gameManager.setElapsedTime(elapsedTime);
                gameManager.saveGame(saveName.trim(), currentDifficulty);
                resumeTimer();
                return;
            }
            JOptionPane.showMessageDialog(this, "Save name cannot be empty.");
        }
    }

    /**
     * Handles loading a saved game state.
     *
     * @param e The ActionEvent triggered by the "Load" menu item.
     */
    private void handleLoadGame(ActionEvent e) {
        List<String> saveFiles = getSaveFiles();
        if (saveFiles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No saved games found.");
            return;
        }

        String selectedSave = (String) JOptionPane.showInputDialog(this,
                "Select a save to load:",
                "Load Game",
                JOptionPane.PLAIN_MESSAGE,
                null,
                saveFiles.toArray(),
                saveFiles.get(0));

        if (selectedSave != null) {
            try {
                gameManager.loadGame(selectedSave);
                elapsedTime = gameManager.getElapsedTime();
                updateTimerLabel();
                resumeTimer();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to load the game: " + ex.getMessage());
            }
        }
    }

    /**
     * Updates the timer label with the current elapsed time.
     */
    private void updateTimerLabel() {
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        String time = String.format("Time: %02d:%02d", minutes, seconds);
        timerLabel.setText(time);
    }

    /**
     * Retrieves the list of saved game files.
     *
     * @return A list of save file names (without extensions).
     */
    private List<String> getSaveFiles() {
        File folder = new File(".");
        File[] listOfFiles = folder.listFiles((dir, name) -> name.endsWith(".json") && !name.equals("leaderboard.json"));
        List<String> saveFiles = new ArrayList<>();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                saveFiles.add(file.getName().replace(".json", ""));
            }
        }
        return saveFiles;
    }

    /**
     * Pauses the game timer.
     * <p>
     * Stops the timer and sets the running state to false.
     */
    private void pauseTimer() {
        if (isTimerRunning) {
            timer.stop();
            isTimerRunning = false;
        }
    }

    /**
     * Resumes the game timer.
     * <p>
     * Restarts the timer and sets the running state to true.
     */
    private void resumeTimer() {
        if (!isTimerRunning) {
            timer.start();
            isTimerRunning = true;
        }
    }

    /**
     * Handles the end of the game.
     * <p>
     * Prompts the user for their name, saves the game result to the leaderboard, and restarts the application.
     */
    private void handleGameEnd() {
        String playerName = JOptionPane.showInputDialog(this, "Enter your name:");
        if (playerName != null && !playerName.trim().isEmpty()) {
            gameManager.setElapsedTime(elapsedTime); // Ensure elapsed time is set
            gameManager.saveGameResult(playerName.trim(), currentDifficulty);
            displayLeaderboard();
        }
        dispose(); // Close the current window
        SudokuGUI.main(new String[]{}); // Restart the application by calling the main method
    }

    /**
     * Displays the leaderboard in a dialog.
     * <p>
     * Retrieves entries from the leaderboard and shows them in a formatted string.
     */
    private void displayLeaderboard() {
        List<SudokuLeaderboardEntry> entries = gameManager.getLeaderboardEntries();
        StringBuilder leaderboardText = new StringBuilder("Leaderboard:\n");
        for (SudokuLeaderboardEntry entry : entries) {
            leaderboardText.append(String.format("%s - %s - %d seconds\n", entry.getPlayerName(), entry.getDifficulty(), entry.getElapsedTime()));
        }
        JOptionPane.showMessageDialog(this, leaderboardText.toString());
    }

    /**
     * Handles the validation of the Sudoku grid.
     *
     * @param e The ActionEvent triggered by the "Check" button or grid filling.
     *          <p>
     *          Validates the grid using the SudokuValidator class and displays the result in a dialog.
     *          Highlights invalid cells in red if there are errors.
     */
    private void handleCheck(ActionEvent e) {
        SudokuValidator validator = new SudokuValidator(grid);
        pauseTimer();

        boolean allCellsFilled = true;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (grid[row][col].isEditable()) {
                    grid[row][col].setBackground(Color.WHITE);
                }
                if (grid[row][col].getText().isEmpty()) {
                    allCellsFilled = false;
                }
            }
        }

        if (validator.isValid()) {
            if (allCellsFilled) {
                handleGameEnd();
            } else {
                JOptionPane.showMessageDialog(this, "All entries are valid, but not all cells are filled.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "There are invalid entries!");
            for (Point p : validator.getInvalidCells()) {
                if (grid[p.x][p.y].isEditable()) {
                    grid[p.x][p.y].setBackground(Color.RED);
                }
            }
        }

        resumeTimer();
    }

    /**
     * Starts a new Sudoku game with the specified difficulty.
     *
     * @param difficulty The difficulty level ("Easy", "Medium", "Hard").
     */
    private void startNewGame(Difficulty difficulty) {
        if (difficulty.equals("Cancel")) {
            return;
        }

        currentDifficulty = difficulty; // Set the current difficulty level

        elapsedTime = 0;
        updateTimerLabel();

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                grid[row][col].setText("");
                SudokuCellFormatter.formatCell(grid[row][col], row, col, true);
            }
        }

        SudokuGenerator generator = new SudokuGenerator();
        int[][] puzzle = generator.generatePuzzle(difficulty);

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (puzzle[row][col] != 0) {
                    grid[row][col].setText(String.valueOf(puzzle[row][col]));
                    SudokuCellFormatter.formatCell(grid[row][col], row, col, false);
                }
            }
        }

        JOptionPane.showMessageDialog(this, "Starting new game with difficulty: " + difficulty);
        timer.start(); // Start the timer when the new game begins
    }
}