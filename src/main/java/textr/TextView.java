package textr;
import io.github.btj.termios.Terminal;
import textr.json.SimpleJsonObject;
import textr.json.SimpleJsonParser;
import textr.json.SimpleJsonParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static textr.KeyInput.*;



public class TextView  extends View implements BufferObserver{


        private int rowCursorPosition = 1;
        private int columnCursorPosition = 1;
        private int verticalScrollPosition;
        private int horizontalScrollPosition;
        private FileBuffer buffer;
        private List<Command> oldOperations;
        private int nbUndone;


        /**
         * Initialize a class View
         * @param filepath = filepath of the file in the view
         * @param rowPosition = row position in the Terminal
         * @param columnPosition = column position in the Terminal
         * @param width = width of the view
         * @param height = height of the view
         * @param active = if true this view can be edited and read
         * @throws IOException
         */
        public TextView(String filepath, int rowPosition, int columnPosition, int width, int height, boolean active) throws IOException {
            super(rowPosition, columnPosition, width, height, active);

            this.verticalScrollPosition = 0;
            this.horizontalScrollPosition = 0;

            this.buffer = new FileBuffer(filepath, this);



            this.rowCursorPosition = rowPosition + 1;
            this.columnCursorPosition = columnPosition + 1;

            oldOperations = new ArrayList<>();
            nbUndone = 0;
        }

        public TextView(int rowPosition, int columnPosition, int width, int height, boolean active) throws IOException {
            super(rowPosition, columnPosition, width, height, active);
            this.verticalScrollPosition = 0;
            this.horizontalScrollPosition = 0;
            this.rowCursorPosition = rowPosition + 1;
            this.columnCursorPosition = columnPosition + 1;
            oldOperations = new ArrayList<>();
            nbUndone = 0;
        }


        // Other existing code...

    @Override
    public void bufferChanged() {
        updateView();
    }

    @Override
    public void bufferLineSplit(int row) {
            // check if row split is below view
            if (row < verticalScrollPosition) {
                verticalScrollPosition++;
            }
        updateView();

    }

    @Override
    public void bufferLineMerged(int row) {
        if (row < verticalScrollPosition) {
            verticalScrollPosition--;

        }
        updateView();

    }

    /**
         * rerender the whole view
         */
        public void updateView() {
            updateBorder(this.getRowPosition(),columnPosition,width,height);
            updateStatusBar();
            updateText();
            updateVerticalScrollbar();
            updateHorizontalScrollbar();
        }

        /**
         * moves the vertical scrollbar to its correct position in the view
         */
        public void updateVerticalScrollbar() {
            int sizeScrollbar = height - 3;
            int lines = buffer.getBufferLength();
            int visibleHeight = height - 3;

            double sizeRatio = (double) visibleHeight / lines;
            int sizeVisibleBar = (int) Math.ceil(sizeScrollbar * sizeRatio);

            int maxScrollPosition = lines - visibleHeight;
            double scrollPositionRatio = (double) verticalScrollPosition / maxScrollPosition;
            int scrollbarHandlePosition = (int) Math.floor((sizeScrollbar - sizeVisibleBar) * scrollPositionRatio) + 1;

            for (int i = 1; i <= sizeScrollbar; i++) {
                if (i >= scrollbarHandlePosition && i < scrollbarHandlePosition + sizeVisibleBar) {
                    Manager.io.printText(rowPosition + i, columnPosition + width - 1, "#");
                } else {
                    Manager.io.printText(rowPosition + i, columnPosition + width - 1, "|");
                }
            }
            Manager.io.moveCursor(rowCursorPosition, columnCursorPosition);
        }

        /**
         * moves the horizontal scrollbar to its correct position in the view
         */
        public void updateHorizontalScrollbar(){
            int sizeScrollbar = width - 1;
            int maxLineLength = buffer.getLongestLineLength();
            int visibleWidth = width - 1;

            double sizeRatio = (double) visibleWidth / maxLineLength;
            int sizeVisibleBar = (int) Math.ceil(sizeScrollbar * sizeRatio);

            int maxScrollPosition = maxLineLength - visibleWidth;
            double scrollPositionRatio = (double) horizontalScrollPosition / maxScrollPosition;
            int scrollbarHandlePosition = (int) Math.floor((sizeScrollbar - sizeVisibleBar) * scrollPositionRatio) + 1;

            for (int i = 1; i <= sizeScrollbar; i++) {
                if (i >= scrollbarHandlePosition && i < scrollbarHandlePosition + sizeVisibleBar) {
                    Manager.io.printText(rowPosition + height - 3, columnPosition + i, "#");
                } else {
                    Manager.io.printText(rowPosition + height - 3, columnPosition + i, "-");
                }
            }
            Manager.io.moveCursor(rowCursorPosition, columnCursorPosition);
        }

        /**
         * Changes border to right symbol
         * "+" for non-active views
         * "*" for active views
         * @param rowPosition = top left corner of view in the Terminal
         * @param columnPosition = top left corner of view in the Terminal
         * @param width = width of view
         * @param height = height of view
         */
        public void updateBorder(int rowPosition, int columnPosition, int width, int height) {
            String borderCharacter = "+";
            if (active) {
                borderCharacter = "*";
            }

            // print borders
            for (int i = 0; i <= height; i++) {
                Manager.io.printText(rowPosition + i, columnPosition, borderCharacter);
                Manager.io.printText(rowPosition + i, columnPosition + width, borderCharacter);
            }

            for (int i = 0; i <= width; i++) {
                Manager.io.printText(rowPosition, columnPosition + i, borderCharacter);
                Manager.io.printText(rowPosition + height, columnPosition + i, borderCharacter);
            }
            Manager.io.moveCursor(rowCursorPosition, columnCursorPosition);
        }

        /**
         * Updates status bar to correctly display:
         * - filename
         * - amount of lines
         * - amount of characters
         * - is a view active or not
         * - is a view dirty or not (there are changes made to the file which are not yet saved to the file) with an "*" behind the filename
         * - row and column coordinates of cursor
         */
        public void updateStatusBar(){
            String borderCharacter = "+";
            if (active) {
                borderCharacter = "*";
            }
            // get amount of characters in buffer and amount of lines

            int bufferLength = buffer.getBufferLength();

            int bufferAmountofChars = buffer.getBufferAmountofChars();

            String statusString = String.format("File:%s%s|Lines:%d|Chars:%d|%s|Row:%d|Col:%d",
                    buffer.getFilePath(), buffer.getDirty() ? "*" : "", bufferLength, bufferAmountofChars, active ? "Active" : "Inactive", rowCursorPosition-rowPosition+verticalScrollPosition, columnCursorPosition-columnPosition+horizontalScrollPosition);

            String statusBorder = new String(new char[width]).replace('\0', (borderCharacter).charAt(0));

            // clean status bar row
            Manager.io.printText(rowPosition + height - 1, columnPosition + 2, new String(new char[width - 2]).replace('\0', ' '));

            Manager.io.printText(rowPosition + height - 1, columnPosition + 1, statusString);
            Manager.io.printText( rowPosition + height - 2, columnPosition + 1 , statusBorder);
            Manager.io.moveCursor(rowCursorPosition, columnCursorPosition);
        }

        /**
         * rerenders the text of a view
         */
        public void updateText() {
            // clear text area
            for (int i = 0; i < height - 4; i++) {
                Manager.io.printText(rowPosition + i + 1, columnPosition + 1, new String(new char[width - 2]).replace('\0', ' '));
            }
            int maxContentRows = height - 4; // Adjust for borders and status bar
            int startRow = rowPosition + 1;
            int startColumn = columnPosition + 1;

            for (int i = 0; i < maxContentRows && i < buffer.getBufferLength(); i++) {
                String line = buffer.getLine(i + verticalScrollPosition).toString();
                if (line.length() > horizontalScrollPosition) {
                    int endIndex = Math.min(line.length(), horizontalScrollPosition + width - 2);
                    line = line.substring(horizontalScrollPosition, endIndex);
                    Manager.io.printText(startRow + i, startColumn, line);
                }
            }
            Manager.io.moveCursor(rowCursorPosition, columnCursorPosition);
            updateVerticalScrollbar();
            updateHorizontalScrollbar();
        }

    public void updateJsonObject(SimpleJsonObject jsonObject) {
        // This depends on how you store and display the JSON object in the TextView
        // For example, if you store the JSON object as a string, you can convert the SimpleJsonObject to a string and update the content of the TextView
        String jsonString = jsonObject.toString();
        // Update the content of the TextView with the new JSON string
        // This depends on how you update the content of the TextView
        // For example, you can use a method like setContent(String content)
        buffer.setContent(jsonString);
    }


    /**
         * get row position of view in the Terminal
         * @return rowPosition
         */
        public int getRowPosition(){
            return rowPosition;
        }

        /**
         * sets row position of view in the Terminal to given input
         * @param rowPosition new row position of View
         */
        public void setRowPosition(int rowPosition) {
            this.rowPosition = rowPosition;
            this.horizontalScrollPosition = 0;
            this.rowCursorPosition = rowPosition + 1;
        }

        /**
         * get column position of view in the Terminal
         * @return columnPosition
         */
        public int getColumnPosition(){
            return columnPosition;
        }

        /**
         * sets column position of view in the Terminal to given input
         * @param columnPosition new column position of View
         */
        public void setColumnPosition(int columnPosition) {
            this.columnPosition = columnPosition;
            this.columnCursorPosition = columnPosition + 1;
        }

        /**
         * get height of view in the Terminal
         * @return height
         */
        public int getHeight(){
            return height;
        }

        /**
         * sets height of view in the Terminal to given input
         * @param height new height of view
         */
        public void setHeight(int height) {
            this.height = height;
        }

        /**
         * get width of view in the Terminal
         * @return width
         */
        public int getWidth(){
            return width;
        }

        /**
         * sets width of view in the Terminal to given input
         * @param width new width of view
         */
        public void setWidth(int width) {
            this.width = width;
        }

        /**
         * get buffer with text of file and changes made to it
         * @return buffer
         */
        public FileBuffer getBuffer(){
            return this.buffer;
        }

        public void setBuffer(FileBuffer newbuffer ){ buffer = newbuffer;}

        /**
         * get row position of cursor in the view
         * @return rowCursorPosition
         */
        public int getRowCursorPosition(){
            return this.rowCursorPosition;
        }

        /**
         * get column position of cursor in the view
         * @return columnCursorPosition
         */
        public int getColumnCursorPosition(){
            return this.columnCursorPosition;
        }

        /**
         * set view to the active view
         * @param active = true if view needs to be set to active
         */
        public void setActive(boolean active) {
            this.active = active;
            Manager.io.moveCursor(this.rowCursorPosition, this.columnCursorPosition);
            updateView();
        }

        /**
         * moves cursor to correct position in the view
         * @param key = 1000 || 1001 || 1002 || 1003 (arrow keys)
         */
        public void moveCursor(int key) {
            switch (key) {
                case ARROW_UP:
                    // move up
                    if (rowCursorPosition > rowPosition + 1) {
                        rowCursorPosition--;
                        StringBuilder previousLine = buffer.getLine(rowCursorPosition-rowPosition-1+ verticalScrollPosition);

                        if (previousLine.length() < columnCursorPosition-columnPosition) { columnCursorPosition = previousLine.length() +columnPosition+1; }
                    }
                    // scroll up
                    else if (verticalScrollPosition != 0)  {
                        verticalScrollPosition--;
                        updateText();
                        updateVerticalScrollbar();
                    }

                    break;

                case ARROW_DOWN:
                    // only go down if the buffer is large enough
                    if  (buffer.getBufferLength() > rowCursorPosition - rowPosition + verticalScrollPosition) {

                        // normal cursor move down
                        if (rowCursorPosition < rowPosition + height - 4) {

                            rowCursorPosition++;
                            StringBuilder nextLine = buffer.getLine(rowCursorPosition-rowPosition-1+ verticalScrollPosition);
                            if (nextLine.length() < columnCursorPosition-columnPosition) { columnCursorPosition = nextLine.length() +columnPosition+1; }
                        }
                        // scroll down
                        else if (verticalScrollPosition < buffer.getBufferLength() - height + 2) {
                            verticalScrollPosition++;
                            updateText();
                            updateVerticalScrollbar();
                        }
                    }
                    break;
                case ARROW_RIGHT:
                    StringBuilder currentLine = buffer.getLine(rowCursorPosition-rowPosition-1+ verticalScrollPosition);
                    // normal cursor move
                    if (columnCursorPosition < columnPosition + width - 3 && currentLine.length() +1+ columnPosition > columnCursorPosition) {
                        //System.out.println(columnPosition);
                        //System.out.println(columnCursorPosition);
                        columnCursorPosition++;

                    }
                    //  scroll horizontal
                    else if (columnCursorPosition >= columnPosition + width - 3 &&
                            horizontalScrollPosition < currentLine.length() - width + 3) {
                        horizontalScrollPosition++;
                        updateText();

                        updateHorizontalScrollbar();

                    }
                    //System.out.println("hallo test");
                    break;

                case ARROW_LEFT:
                    if (columnCursorPosition > columnPosition) {
                        columnCursorPosition--;
                    }
                    else if (columnCursorPosition <= columnPosition + 1 && horizontalScrollPosition > 0) {
                        horizontalScrollPosition--;
                        updateText();
                        updateHorizontalScrollbar();
                    }
                    else if (columnCursorPosition <=  2 && horizontalScrollPosition == 0 && rowCursorPosition - rowPosition > 1) {
                        StringBuilder previousLine = buffer.getLine(rowCursorPosition-rowPosition-2+ verticalScrollPosition);
                        horizontalScrollPosition = Math.max(0, previousLine.length() - width + 3);
                        columnCursorPosition = Math.min(previousLine.length() + 2, width - 2 + columnPosition);
                        rowCursorPosition--;
                        updateText();
                        updateHorizontalScrollbar();
                    }
                    if (verticalScrollPosition > 0 && rowCursorPosition == rowPosition + 1) {
                        verticalScrollPosition--;
                        updateVerticalScrollbar();
                        updateText();
                    }
                    break;
            }

            if (columnCursorPosition < 1 + columnPosition) {
                columnCursorPosition = 1 + columnPosition;
                horizontalScrollPosition = 0;
                updateText();
                updateHorizontalScrollbar();
            }
            // if the cursor is greater than the line length, move the horizontal scroll position
        /*
        if (columnCursorPosition > buffer.getLine(rowCursorPosition-rowPosition-1+ verticalScrollPosition).length() + 2 + columnPosition - horizontalScrollPosition) {
            horizontalScrollPosition = Math.max(0, buffer.getLine(rowCursorPosition-rowPosition-1+ verticalScrollPosition).length() - width + 3);
            columnCursorPosition = buffer.getLine(rowCursorPosition-rowPosition-1+ verticalScrollPosition).length() + 2 - horizontalScrollPosition + columnPosition;
            updateText();
            updateHorizontalScrollbar();
        }
        */
            Manager.io.moveCursor(rowCursorPosition, columnCursorPosition);
            updateStatusBar();
        }

        private void doCommand(Command command) {
            oldOperations.subList(oldOperations.size() - nbUndone, oldOperations.size()).clear();
            oldOperations.add(command);
            nbUndone = 0;
            command.doOperation();
        }

        /**
         * puts character where cursor is in the view, moves cursor to the right position in the view
         * @param input = char we want to insert in the view
         * @throws IOException
         */
        public void insertChar(int input) throws IOException {
            int row = rowCursorPosition-rowPosition-1 + verticalScrollPosition;
            int at = columnCursorPosition-columnPosition-1 + horizontalScrollPosition;
            StringBuilder line = buffer.getLine(row);
            char c = (char) input;
            doCommand(new Command() {
                public void doOperation(){
                    if (at <= line.length() - horizontalScrollPosition){
                        line.insert(at,c);
                    }
                    else {
                        line.append(c);
                    }
                    buffer.setContentBuffer(row, line);
                    moveCursor(ARROW_RIGHT);
                    updateText();
                }

                @Override
                public void undo() {
                    line.deleteCharAt(at);
                    buffer.setContentBuffer(row, line);
                    moveCursor(ARROW_LEFT);
                    updateText();
                }
            });
        }

        /**
         * deletes the character in front of the cursor, moves cursor to the right position in the view
         * @throws IOException
         */
        public void deleteChar() throws IOException {
            int row = this.rowCursorPosition-rowPosition-1 + verticalScrollPosition;
            int at = this.columnCursorPosition-columnPosition-1 + horizontalScrollPosition;
            FileBuffer buffer = this.buffer;
            StringBuilder line = buffer.getLine(row);
            doCommand(new Command() {
                private char c;
                private StringBuilder previousLine;
                private int lengthPreviousLine;
                @Override
                public void doOperation() {
                    if (at>0 && at <= line.length()) {
                        c = line.charAt(at-1);
                        line.deleteCharAt(at-1);
                        buffer.setContentBuffer(row, line);
                        moveCursor(ARROW_LEFT);
                    } else if (at == 0 && row > 0) {
                        previousLine = buffer.getLine(row-1);
                        lengthPreviousLine =  previousLine.length();
                        previousLine.append(line);
                        buffer.setContentBuffer(row-1, previousLine);
                        buffer.removeLine(row);
                        moveCursor(ARROW_UP);
                        for (int i = 0; i < lengthPreviousLine; i++) {
                            moveCursor(ARROW_RIGHT);
                        }
                        if (previousLine.isEmpty()) {columnCursorPosition = 1 +columnPosition; }
                        else { columnCursorPosition = lengthPreviousLine +1+columnPosition;}
                    }
                    updateText();
                }

                @Override
                public void undo() {
                    if (at > 0 && at <= line.length()) {
                        line.insert(at - 1, c);
                        buffer.setContentBuffer(row, line);
                        moveCursor(ARROW_RIGHT);
                    } else if (at == 0 && row > 0) {
                        if (previousLine != null) {
                            previousLine.delete(lengthPreviousLine,previousLine.length());
                            lengthPreviousLine = previousLine.length();
                            buffer.addContentBuffer(row-1, previousLine);
                            buffer.setContentBuffer(row, line);
                            moveCursor(ARROW_DOWN);
                            for (int i = 0; i < line.length(); i++) {
                                moveCursor(ARROW_LEFT);
                            }
                        }
                    }
                    updateText();
                }
            });
            this.updateText();
        }

        void undo() {
            if (nbUndone < oldOperations.size()) {
                Command lastCommand = oldOperations.get(oldOperations.size() - 1 - nbUndone);
                lastCommand.undo();
                nbUndone++;
            }
            if (nbUndone == oldOperations.size()) {
                buffer.setDirty(false);
                updateView();
            }
        }

        void redo() {
            if (nbUndone > 0) {
                Command undoneCommand = oldOperations.get(oldOperations.size() - nbUndone);
                undoneCommand.doOperation();
                nbUndone--;
            }
        }

        /**
         * adds new line in the view and moves text if necessary, moves cursor to the right position in the view
         * @throws IOException
         */
        public void addEmptyLine() throws IOException {
            FileBuffer buffer = this.buffer;
            int row = rowCursorPosition-rowPosition-1 + verticalScrollPosition;
            int at = columnCursorPosition-columnPosition-1 + horizontalScrollPosition;
            StringBuilder line = buffer.getLine(row);
            horizontalScrollPosition = 0;
            doCommand(new Command() {
                public void doOperation(){
                    StringBuilder firstPartOfString = new StringBuilder(line.substring(0, at));
                    StringBuilder remainingString = new StringBuilder(line.substring(at));
                    buffer.setContentBuffer(row,firstPartOfString);
                    buffer.addContentBuffer(row+1,remainingString);
                    moveCursor(ARROW_DOWN);
                    columnCursorPosition = 1 + columnPosition;
                    updateText();
                    buffer.lineSplit(row);

                }

                @Override
                public void undo() {
                    StringBuilder string1 = buffer.getLine(row);
                    string1.append(buffer.getLine(row+1));
                    buffer.setContentBuffer(row, string1);
                    buffer.removeLine(row+1);

                    moveCursor(ARROW_UP);
                    for (int i = 0; i < at; i++) {
                        moveCursor(ARROW_RIGHT);
                    }
                    updateText();
                    buffer.lineMerged(row);

                }
            });
        }



        public SimpleJsonObject jsonify() {
            ArrayList<StringBuilder> content = buffer.getContentBuffer();
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < content.size(); i++) {
                StringBuilder sb = content.get(i);
                result.append(sb.toString());
                if (i < content.size() - 1) {
                    result.append(System.lineSeparator());
                }
            }


            String jsonString = result.toString();



            try {
                SimpleJsonObject object = SimpleJsonParser.parseSimpleJsonObject(jsonString);
                return object;
            } catch (SimpleJsonParserException e) {

                rowCursorPosition = e.getLocation().line() + rowPosition+1 -verticalScrollPosition;
                columnCursorPosition = e.getLocation().column() + columnPosition+1 -horizontalScrollPosition;
                updateText();
                return null;
            }





        }

        public LeafLayout duplicate() throws IOException {
                LeafLayout layout = new LeafLayout(rowPosition,columnPosition,width, height+1);
                ((TextView) layout.getView()).setBuffer(buffer);
                buffer.addObserver((TextView)layout.getView());
                return layout;

        }



        /**
         * saves changes made in the buffer to the file itself
         * @throws IOException
         */
        public void save() throws IOException {
            this.buffer.save();
            this.updateStatusBar();

            if (buffer.getOriginalTextView() != null) {
                buffer.getOriginalTextView().updateView();
            }
        }
}

