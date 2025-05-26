package textr.json;

// this code is largely based on the code included in the assignment

/**
 * @param line 0-based
 * @param column 0-based
 */
public record TextLocation(int line, int column) {}