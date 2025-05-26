package textr;
import io.github.btj.termios.Terminal;

import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

public class ViewTest {
    private static final int ARROW_UP = 1000, ARROW_DOWN = 1001, ARROW_LEFT = 1002, ARROW_RIGHT = 1003;

    private String filepath;
    private int rowPosition;
    private int columnPosition;
    private int height;
    private int width;
    private int rowCursorPosition = 1;
    private int columnCursorPosition = 1;
    private boolean active;
    private boolean dirty;
    private int verticalScrollPosition;
    private int horizontalScrollPosition;
    private FileBuffer buffer;

    public ViewTest(String filepath, int rowPosition, int columnPosition, int width, int height, boolean active) throws IOException {
        this.verticalScrollPosition = 0;
        this.horizontalScrollPosition = 0;

        this.buffer = new FileBuffer(filepath);
        this.active = active;
        this.filepath = filepath;
        this.rowPosition = rowPosition;
        this.columnPosition = columnPosition;
        this.rowCursorPosition = rowPosition + 1;
        this.columnCursorPosition = columnPosition + 1;
        this.width = width;
        this.height = height;
        this.dirty = false;
        //updateAppearance(rowPosition, columnPosition, width, height );
    }

    /*
    public void updateView(){
        updateAppearance(rowPosition,columnPosition,width,height);
    }s
     */


    /*
    public void updateAppearance(int rowPosition, int columnPosition, int width, int height) {
        updateBorder(rowPosition,columnPosition,width,height);
        updateStatusBar();
        updateText();
        updateScrollbar();
    }
     */

    /*
    public void updateScrollbar() {
        int sizeScrollbar = height - 2;
        int lines = buffer.getBufferLength();
        int visibleHeight = height - 3;

        double sizeRatio = (double) visibleHeight / lines;
        int sizeVisibleBar = (int) Math.ceil(sizeScrollbar * sizeRatio);

        int maxScrollPosition = lines - visibleHeight;
        double scrollPositionRatio = (double) verticalScrollPosition / maxScrollPosition;
        int scrollbarHandlePosition = (int) Math.floor((sizeScrollbar - sizeVisibleBar) * scrollPositionRatio) + 1;

        for (int i = 1; i <= sizeScrollbar; i++) {
            if (i >= scrollbarHandlePosition && i < scrollbarHandlePosition + sizeVisibleBar) {
                Terminal.printText(rowPosition + i, columnPosition + width - 2, "#");
            } else {
                Terminal.printText(rowPosition + i, columnPosition + width - 2, "|");
            }
        }

        //Terminal.moveCursor(rowCursorPosition, columnCursorPosition);
    }
     */

    /*
    public void updateBorder(int rowPosition, int columnPosition, int width, int height) {
        String borderCharacter = "+";
        if (active) {
            borderCharacter = "*";
        }

        // print borders
        for (int i = 0; i < height; i++) {
            Terminal.printText(rowPosition + i, columnPosition, borderCharacter);
            Terminal.printText(rowPosition + i, columnPosition + width - 1, borderCharacter);
        }

        for (int i = 0; i < width; i++) {
            Terminal.printText(rowPosition, columnPosition + i, borderCharacter);
            Terminal.printText(rowPosition + height, columnPosition + i, borderCharacter);
        }
        Terminal.moveCursor(rowCursorPosition, columnCursorPosition);
    }
     */

    /*
    public void updateStatusBar(){
        String borderCharacter = "+";
        if (active) {
            borderCharacter = "*";
        }

        // get amount of characters in buffer and amount of lines

        int bufferLength = buffer.getBufferLength();

        int bufferAmountofChars = buffer.getBufferAmountofChars();




        String statusString = String.format("File:%s%s|Lines:%d|Chars:%d|%s|Row:%d|Col:%d",
                filepath, dirty ? "*" : "", bufferLength, bufferAmountofChars, active ? "Active" : "Inactive", rowCursorPosition-rowPosition+verticalScrollPosition, columnCursorPosition-columnPosition+horizontalScrollPosition);

        String statusBorder = new String(new char[width]).replace('\0', (borderCharacter).charAt(0));

        // clean status bar row
        Terminal.printText(rowPosition + height - 1, columnPosition + 1, new String(new char[width - 2]).replace('\0', ' '));

        Terminal.printText(rowPosition + height - 1, columnPosition + 1, statusString);
        Terminal.printText( rowPosition + height - 2, columnPosition + 1 , statusBorder);
        Terminal.moveCursor(rowCursorPosition, columnCursorPosition);
    }

     */

    /*
    public void updateText() {
        // clear text area
        for (int i = 0; i < height - 3; i++) {
            Terminal.printText(rowPosition + i + 1, columnPosition + 1, new String(new char[width - 2]).replace('\0', ' '));
        }


        int maxContentRows = height - 3; // Adjust for borders and status bar
        int startRow = rowPosition + 1;
        int startColumn = columnPosition + 1;

        for (int i = 0; i < maxContentRows && i < buffer.getBufferLength(); i++) {
            String line = buffer.getLine(i + verticalScrollPosition);
            if (line.length() > horizontalScrollPosition) {
                int endIndex = Math.min(line.length(), horizontalScrollPosition + width - 2);
                line = line.substring(horizontalScrollPosition, endIndex);
                Terminal.printText(startRow + i, startColumn, line);
            }

        }
        Terminal.moveCursor(rowCursorPosition, columnCursorPosition);
        updateScrollbar();
    }

     */

    public int getRowPosition() {
        return rowPosition;
    }

    public int getColumnPosition() {
        return columnPosition;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public FileBuffer getBuffer() {
        return this.buffer;
    }

    public int getRowCursorPosition() {
        return this.rowCursorPosition;
    }

    public int getColumnCursorPosition() {
        return this.columnCursorPosition;
    }

    public String getFilepath() {
        return this.filepath;
    }

    public boolean getActive() {
        return this.active;
    }

    public boolean getDirty() {
        return this.dirty;
    }

    public void setActive(boolean active) {
        this.active = active;
        //Terminal.moveCursor(this.rowCursorPosition, this.columnCursorPosition);
        //updateAppearance(this.rowPosition, this.columnPosition, this.width, this.height);
    }
}
    /*
    public void moveCursor(int key) {
        switch (key) {
            case ARROW_UP:
                if (rowCursorPosition > rowPosition + 1) {
                    rowCursorPosition--;
                }
                else {
                    if (verticalScrollPosition != 0) {
                        verticalScrollPosition--;
                        //updateText();
                        //updateScrollbar();
                    }
                }
                break;
            case ARROW_DOWN:
                if  (buffer.getBufferLength() > rowCursorPosition - rowPosition + verticalScrollPosition) {
                    if (rowCursorPosition < rowPosition + height - 3) {
                        rowCursorPosition++;
                        if (rowCursorPosition > buffer.getBufferLength() + 1) {
                            this.columnCursorPosition = 2;
                        }
                        // never the cursor position be greater than the line length
                        if (columnCursorPosition > buffer.getLine(rowCursorPosition - rowPosition - 1 + verticalScrollPosition).length() + 2) {
                            columnCursorPosition = buffer.getLine(rowCursorPosition - rowPosition - 1 + verticalScrollPosition).length() + 2;
                        }
                    } else {
                        verticalScrollPosition++;
                        //updateText();
                        //updateScrollbar();
                    }
                }
                break;
            case ARROW_RIGHT:
                String currentLine = buffer.getLine(rowCursorPosition-rowPosition-1+ verticalScrollPosition);
                if (columnCursorPosition < columnPosition + width - 2 &&
                        columnCursorPosition <= currentLine.length() + 1 - horizontalScrollPosition) {
                    columnCursorPosition++;
                } else if (columnCursorPosition >= columnPosition + width - 2 &&
                        horizontalScrollPosition < currentLine.length() - width + 3) {
                    horizontalScrollPosition++;
                    //updateText();
                }
                break;
            case ARROW_LEFT:
                if (columnCursorPosition > columnPosition + 1) {
                    columnCursorPosition--;
                }
                else if (columnCursorPosition <= columnPosition + 1 && horizontalScrollPosition > 0) {
                    horizontalScrollPosition--;
                    //updateText();
                }
                else if (columnCursorPosition <=  2 && horizontalScrollPosition == 0 && rowCursorPosition - rowPosition > 1) {
                    String previousLine = buffer.getLine(rowCursorPosition-rowPosition-2+ verticalScrollPosition);
                    horizontalScrollPosition = Math.max(0, previousLine.length() - width + 3);
                    columnCursorPosition = Math.min(previousLine.length() + 2, width - 1);
                    rowCursorPosition--;
                    //updateText();
                }
                if (verticalScrollPosition > 0 && rowCursorPosition == rowPosition + 1) {
                    verticalScrollPosition--;
                    //updateScrollbar();
                    //updateText();
                }
                break;
        }

        if (columnCursorPosition < 2) {
            columnCursorPosition = 2;
            horizontalScrollPosition = 0;
            //updateText();
        }

        if (columnCursorPosition > buffer.getLine(rowCursorPosition-rowPosition-1+ verticalScrollPosition).length() + 2) {
            horizontalScrollPosition = Math.max(0, buffer.getLine(rowCursorPosition-rowPosition-1+ verticalScrollPosition).length() - width + 3);
            columnCursorPosition = buffer.getLine(rowCursorPosition-rowPosition-1+ verticalScrollPosition).length() + 2 - horizontalScrollPosition;
            //updateText();
        }

        //Terminal.moveCursor(rowCursorPosition, columnCursorPosition);
        //updateStatusBar();
    }

    public void insertChar(int input) throws IOException {
        this.dirty = true;
        int row = this.rowCursorPosition-rowPosition-1 + verticalScrollPosition;
        int at = this.columnCursorPosition-columnPosition-1 + horizontalScrollPosition;
        FileBuffer buffer = this.buffer;
        String line = buffer.getLine(row);
        char c = (char) input;
        StringBuilder sb = new StringBuilder(line);
        if (at <= sb.length() - horizontalScrollPosition) {
            sb.insert(at, c);
        } else {
            sb.append(c);
        }
        String linewithinsert = sb.toString();

        buffer.setContentBuffer(row, linewithinsert);
        this.moveCursor(ARROW_RIGHT);
        //this.updateText();
    }

    public void deleteChar() throws IOException {
        this.dirty = true;
        int row = this.rowCursorPosition-rowPosition-1 + verticalScrollPosition;
        int at = this.columnCursorPosition-columnPosition-1 + horizontalScrollPosition;
        FileBuffer buffer = this.buffer;
        String line = buffer.getLine(row);
        StringBuilder sb = new StringBuilder(line);
        int lengthPreviousLine = 0;
        if (at>0 && at <= sb.length()) {
            sb.deleteCharAt(at-1);
            String lineWithDelete = sb.toString();
            buffer.setContentBuffer(row, lineWithDelete);
            this.moveCursor(ARROW_LEFT);
        } else if (at == 0 && row > 0) {
            int previousLine = row-1;
            lengthPreviousLine = buffer.getLine(previousLine).length();
            StringBuilder sbTemp = new StringBuilder(previousLine);
            ArrayList<String> tempList = new ArrayList<String>();
            for (int i = 0; i < buffer.getContentBuffer().size(); i++) {
                if (i == previousLine) {
                    String remainingString = buffer.getLine(i+1);
                    sbTemp.append(buffer.getLine(previousLine));
                    sbTemp.append(remainingString);
                    tempList.add(sbTemp.toString());
                    i++;
                } else {
                    tempList.add(buffer.getLine(i));
                }
            }
            this.buffer.setNewContentBuffer(tempList);
            this.moveCursor(ARROW_UP);
            if (buffer.getLine(previousLine).isEmpty()) { this.columnCursorPosition = 2; }
            else { this.columnCursorPosition = lengthPreviousLine+2;}
        }
        //this.updateText();
    }

    public void addEmptyLine() throws IOException {
        this.dirty = true;
        int row = this.rowCursorPosition-rowPosition-1 + verticalScrollPosition;
        int at = this.columnCursorPosition-columnPosition-1 + horizontalScrollPosition;
        FileBuffer buffer = this.buffer;
        String line = buffer.getLine(row);

        ArrayList<String> tempList = new ArrayList<String>();
        for (int i = 0; i < buffer.getContentBuffer().size(); i++) {
            if (i == row) {
                String remainingString = line.substring(at);
                String firstPartOfString = line.substring(0, at);
                tempList.add(firstPartOfString);
                tempList.add(remainingString);
            } else {
                tempList.add(buffer.getLine(i));
            }
        }

        this.buffer.setNewContentBuffer(tempList);
        this.moveCursor(ARROW_DOWN);
        this.columnCursorPosition = 2;
        //this.updateText();

    }

    public void save() throws IOException {
        this.buffer.save();
        this.dirty = false;
        //this.updateStatusBar();
    }

}
*/