package sudoku;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * @class SudokuCellFilter
 * @brief A custom DocumentFilter to restrict input in a Sudoku cell to digits 1-9.
 * <p>
 * This filter ensures that only numeric characters in the range 1-9
 * can be entered into the document. It also allows clearing text completely.
 */
public class SudokuCellFilter extends DocumentFilter {

    /**
     * Inserts text into the document if it matches the allowed pattern.
     *
     * @param fb     The FilterBypass used to mutate the Document.
     * @param offset The offset into the document to insert the content.
     * @param string The string to insert.
     * @param attr   The attributes to associate with the inserted content.
     * @throws BadLocationException If the insert would result in invalid content.
     *                              <p>
     *                              Only digits from 1-9 or an empty string are allowed.
     */
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string.matches("[1-9]|")) {
            fb.insertString(offset, string, attr);
        }
    }

    /**
     * Replaces text in the document with new text if it matches the allowed pattern.
     *
     * @param fb     The FilterBypass used to mutate the Document.
     * @param offset The offset into the document where the replacement begins.
     * @param length The length of the text to replace.
     * @param text   The text to replace with.
     * @param attrs  The attributes to associate with the replacement text.
     * @throws BadLocationException If the replace would result in invalid content.
     *                              <p>
     *                              Only digits from 1-9 or an empty string are allowed.
     */
    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text.matches("[1-9]|")) {
            fb.replace(0, fb.getDocument().getLength(), text, attrs);
        }
    }

    /**
     * Removes text from the document.
     *
     * @param fb     The FilterBypass used to mutate the Document.
     * @param offset The offset into the document where removal begins.
     * @param length The number of characters to remove.
     * @throws BadLocationException If the remove would result in invalid content.
     *                              <p>
     *                              Text removal is always allowed to ensure the cell can be cleared.
     */
    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        fb.remove(offset, length);
    }
}