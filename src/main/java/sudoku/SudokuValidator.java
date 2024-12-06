package sudoku;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @class SudokuValidator
 * @brief Validates the current state of a Sudoku grid.
 *
 * This class checks if the current state of the Sudoku grid is valid and keeps track of invalid cells.
 */
public class SudokuValidator {

    private final JTextField[][] grid; ///< The Sudoku grid to validate.
    private final List<Point> invalidCells; ///< List of invalid cell coordinates.

    /**
     * Constructs a SudokuValidator with the specified grid.
     *
     * @param grid The Sudoku grid to validate.
     */
    public SudokuValidator(JTextField[][] grid) {
        this.grid = grid;
        this.invalidCells = new ArrayList<>();
    }

    /**
     * Checks if the current state is valid and saves the invalid cells.
     *
     * @return true if all entered numbers are valid, false otherwise.
     */
    public boolean isValid() {
        invalidCells.clear(); // Clear previous invalid cells

        if (isBoardEmpty()) {
            return false;
        }

        boolean isValid = true;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String value = grid[row][col].getText();
                if (!value.isEmpty()) {
                    int num = Integer.parseInt(value);
                    if (!isSafe(row, col, num)) {
                        invalidCells.add(new Point(row, col));
                        isValid = false;
                    }
                }
            }
        }
        return isValid;
    }

    /**
     * Checks if a number is valid at a given position.
     *
     * @param row The row index.
     * @param col The column index.
     * @param num The number to check.
     * @return true if the number is valid, false otherwise.
     */
    private boolean isSafe(int row, int col, int num) {
        // Check the row
        for (int i = 0; i < 9; i++) {
            if (i != col && grid[row][i].getText().equals(String.valueOf(num))) {
                return false;
            }
        }

        // Check the column
        for (int i = 0; i < 9; i++) {
            if (i != row && grid[i][col].getText().equals(String.valueOf(num))) {
                return false;
            }
        }

        // Check the 3x3 subgrid
        int subGridRowStart = (row / 3) * 3;
        int subGridColStart = (col / 3) * 3;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int currentRow = subGridRowStart + i;
                int currentCol = subGridColStart + j;
                if (currentRow != row && currentCol != col &&
                        grid[currentRow][currentCol].getText().equals(String.valueOf(num))) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks if the board is completely empty.
     *
     * @return true if the board is completely empty, false otherwise.
     */
    private boolean isBoardEmpty() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (!grid[row][col].getText().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the list of invalid cells.
     *
     * @return The list of invalid cell coordinates.
     */
    public List<Point> getInvalidCells() {
        return invalidCells;
    }
}