package sudoku;

import java.util.Random;

/**
 * @class SudokuGenerator
 * @brief Generates Sudoku puzzles of varying difficulty levels.
 * <p>
 * This class provides methods to generate a complete Sudoku grid,
 * remove cells to create a puzzle based on difficulty.
 */
public class SudokuGenerator {

    private static final int SIZE = 9;
    /// < The size of the Sudoku grid (9x9).
    private static final int SUBGRID_SIZE = 3;
    /// < The size of a subgrid (3x3).
    private static final Random random = new Random(); ///< Random number generator for shuffling and cell removal.

    /**
     * Generates a Sudoku puzzle based on the specified difficulty.
     *
     * @param difficulty The difficulty level of the puzzle ("Easy", "Medium", "Hard").
     * @return A 2D integer array representing the Sudoku puzzle grid.
     * @throws IllegalStateException If an invalid difficulty level is provided.
     *                               <p>
     *                               The number of clues (pre-filled cells) varies by difficulty:
     *                               - Easy: 60 clues
     *                               - Medium: 30 clues
     *                               - Hard: 15 clues
     */
    public int[][] generatePuzzle(Difficulty difficulty) {
        int clues = switch (difficulty) {
            case EASY -> 60;
            case MEDIUM -> 30;
            case HARD -> 15;
            default -> throw new IllegalStateException("Unexpected value: " + difficulty);
        };

        int[][] puzzle = generateCompleteGrid();
        removeCells(puzzle, SIZE * SIZE - clues);
        return puzzle;
    }

    /**
     * Generates a complete, valid Sudoku grid.
     *
     * @return A 2D integer array representing a complete Sudoku grid.
     * <p>
     * This method uses a backtracking algorithm to fill the grid with
     * numbers while adhering to Sudoku rules.
     */
    private int[][] generateCompleteGrid() {
        int[][] grid = new int[SIZE][SIZE];
        fillGrid(grid, 0); // Start filling from the first cell
        return grid;
    }

    /**
     * Fills the Sudoku grid using a backtracking algorithm.
     *
     * @param grid      The Sudoku grid to fill.
     * @param cellIndex The current cell index being filled (row * SIZE + col).
     * @return True if the grid is successfully filled, false otherwise.
     * <p>
     * This method recursively tries all possible numbers for each cell
     * and backtracks if no valid number can be placed.
     */
    private boolean fillGrid(int[][] grid, int cellIndex) {
        if (cellIndex == SIZE * SIZE) return true; // All cells are filled

        int row = cellIndex / SIZE;
        int col = cellIndex % SIZE;

        if (grid[row][col] != 0) return fillGrid(grid, cellIndex + 1); // Skip already filled cells

        int[] numbers = generateShuffledNumbers();
        for (int num : numbers) {
            if (isSafe(grid, row, col, num)) {
                grid[row][col] = num;
                if (fillGrid(grid, cellIndex + 1)) return true;
                grid[row][col] = 0; // Backtrack
            }
        }

        return false; // No valid number found for this cell
    }

    /**
     * Generates an array of numbers 1-9 in random order.
     *
     * @return An array of shuffled numbers from 1 to 9.
     */
    private int[] generateShuffledNumbers() {
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9};

        for (int i = SIZE - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            swap(numbers, i, j);
        }

        return numbers;
    }

    /**
     * Swaps two elements in an array.
     *
     * @param array The array in which the elements are swapped.
     * @param i     The index of the first element.
     * @param j     The index of the second element.
     */
    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * Checks if placing a number in a cell is safe according to Sudoku rules.
     *
     * @param grid The Sudoku grid.
     * @param row  The row index of the cell.
     * @param col  The column index of the cell.
     * @param num  The number to place in the cell.
     * @return True if it is safe to place the number, false otherwise.
     * <p>
     * This method checks the row, column, and the 3x3 subgrid for duplicates.
     */
    private boolean isSafe(int[][] grid, int row, int col, int num) {
        // Check row
        for (int i = 0; i < SIZE; i++) {
            if (grid[row][i] == num) return false;
        }

        // Check column
        for (int i = 0; i < SIZE; i++) {
            if (grid[i][col] == num) return false;
        }

        // Check 3x3 subgrid
        int subGridRowStart = (row / SUBGRID_SIZE) * SUBGRID_SIZE;
        int subGridColStart = (col / SUBGRID_SIZE) * SUBGRID_SIZE;

        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                if (grid[subGridRowStart + i][subGridColStart + j] == num) return false;
            }
        }

        return true;
    }

    /**
     * Removes a specified number of cells from the grid to create a puzzle.
     *
     * @param grid          The Sudoku grid.
     * @param cellsToRemove The number of cells to remove.
     *                      <p>
     *                      This method randomly selects cells and sets them to 0 (empty).
     */
    private void removeCells(int[][] grid, int cellsToRemove) {
        while (cellsToRemove > 0) {
            int cellIndex = random.nextInt(SIZE * SIZE);
            int row = cellIndex / SIZE;
            int col = cellIndex % SIZE;

            if (grid[row][col] != 0) {
                grid[row][col] = 0;
                cellsToRemove--;
            }
        }
    }
}