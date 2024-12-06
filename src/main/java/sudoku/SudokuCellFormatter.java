package sudoku;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

/**
 * @class SudokuCellFormatter
 * @brief Provides utility methods for formatting Sudoku cell JTextFields.
 * <p>
 * This class defines a static method to configure the appearance and behavior of
 * Sudoku grid cells, including alignment, size, font, background color, and borders.
 */
public class SudokuCellFormatter {

    /**
     * Formats a Sudoku grid cell with specified properties.
     *
     * @param cell       The JTextField representing a Sudoku cell.
     * @param row        The row index of the cell in the Sudoku grid (0-based).
     * @param col        The column index of the cell in the Sudoku grid (0-based).
     * @param isEditable Whether the cell should be editable by the user.
     *                   <p>
     *                   This method:
     *                   - Centers text horizontally.
     *                   - Sets the cell's size to 50x50 pixels.
     *                   - Adjusts background color and font based on the `isEditable` parameter.
     *                   - Configures borders to emphasize 3x3 subgrid boundaries.
     */
    public static void formatCell(JTextField cell, int row, int col, boolean isEditable) {
        cell.setHorizontalAlignment(JTextField.CENTER); // Align text to the center
        cell.setPreferredSize(new Dimension(50, 50)); // Set preferred cell size
        cell.setEditable(isEditable); // Enable or disable editing
        cell.setBackground(isEditable ? Color.WHITE : Color.LIGHT_GRAY); // Background color based on editability
        cell.setFont(new Font("Arial", isEditable ? Font.PLAIN : Font.BOLD, 30)); // Font style and size

        // Determine border thickness
        int top = (row % 3 == 0) ? 3 : 1;
        int left = (col % 3 == 0) ? 3 : 1;
        int bottom = (row == 8) ? 3 : 1;
        int right = (col == 8) ? 3 : 1;

        // Apply border
        cell.setBorder(new MatteBorder(top, left, bottom, right, Color.BLACK));
    }
}