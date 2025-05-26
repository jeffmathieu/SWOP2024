package textr;
import io.github.btj.termios.Terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

public abstract class View {
    private static final int ARROW_UP = 1000, ARROW_DOWN = 1001, ARROW_LEFT = 1002, ARROW_RIGHT = 1003;

    protected int rowPosition;
    protected int columnPosition;
    protected int height;
    protected int width;
    protected boolean active;
    private SnakeGame game;


    /**
     * Initialize a class View
     *
     * @param rowPosition    = row position in the Terminal
     * @param columnPosition = column position in the Terminal
     * @param width          = width of the view
     * @param height         = height of the view
     * @param active         = if true this view can be edited and read
     * @throws IOException
     */
    public View(int rowPosition, int columnPosition, int width, int height, boolean active) throws IOException {

        this.active = active;
        this.rowPosition = rowPosition;
        this.columnPosition = columnPosition;
        this.width = width;
        this.height = height;
    }


    /**
     * get row position of view in the Terminal
     *
     * @return rowPosition
     */
    public int getRowPosition() {
        return rowPosition;
    }

    /**
     * sets row position of view in the Terminal to given input
     *
     * @param rowPosition new row position of View
     */
    public void setRowPosition(int rowPosition) {
        this.rowPosition = rowPosition;
    }

    /**
     * get column position of view in the Terminal
     *
     * @return columnPosition
     */
    public int getColumnPosition() {
        return columnPosition;
    }

    /**
     * sets column position of view in the Terminal to given input
     *
     * @param columnPosition new column position of View
     */
    public void setColumnPosition(int columnPosition) {
        this.columnPosition = columnPosition;
    }

    /**
     * get height of view in the Terminal
     *
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     * sets height of view in the Terminal to given input
     *
     * @param height new height of view
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * get width of view in the Terminal
     *
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     * sets width of view in the Terminal to given input
     *
     * @param width new width of view
     */
    public void setWidth(int width) {
        this.width = width;
    }

    public abstract void setActive(boolean b);

    public abstract void updateView();

}




