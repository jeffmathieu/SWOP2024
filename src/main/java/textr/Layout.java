package textr;

import io.github.btj.termios.Terminal;

import java.io.IOException;
import java.util.ArrayList;


public abstract class Layout{

    private int rowPosition;
    private int columnPosition;
    private int height;
    private int width;
    protected Layout parent;

    /**
     *
     * @param rowPosition row position of the layout in the terminal
     * @param columnPosition column position of the layout in the terminal
     * @param width width of the layout
     * @param height height of the layout
     * @throws IOException
     */
    public Layout(int rowPosition, int columnPosition, int width, int height) throws IOException {
        this.rowPosition = rowPosition;
        this.columnPosition = columnPosition;
        this.width = width;
        this.height = height;
    }

    public Layout() {

    }


    /**
     *
     * @param parent parent of the layout in the tree
     */
    public void setParent(Layout parent){
        this.parent = parent;
    }

    /**
     *
     * @return parent
     */
    public Layout getParent(){
        return this.parent;
    }


    /**
     *
     * @return columnPosition
     */
     public int getColumnPosition(){
        return this.columnPosition;
     }

    /**
     *
     * @return rowPosition
     */
    public int getRowPosition(){
        return this.rowPosition;
    }

    /**
     *
     * @return height
     */
    public int getHeight(){
        return this.height;
    }

    /**
     *
     * @return width
     */
    public int getWidth(){
        return this.width;
    }


    /**
     *
     * @param columnPosition column position of the layout in the terminal
     */
    public void setColumnPosition(int columnPosition) {
        this.columnPosition = columnPosition;
    }

    /**
     *
     * @param rowPosition row position of the layout in the terminal
     */
    public void setRowPosition(int rowPosition) {
        this.rowPosition = rowPosition;
    }

    /**
     *
     * @param height height of the layout
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     *
     * @param width width of the layout
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Renders the nex terminal view
     * @throws IOException
     */
    public  void render() throws IOException {
        // do nothing
    }


    /**
     *
     * @param filepath path of the layout bufferView
     * @throws IOException
     */
    public void addChild(String filepath) throws IOException {
    }

    /**
     *
     * @param layout child layout
     * @throws IOException
     */
    public void addChild(Layout layout) throws IOException {
    }

    /**
     *
     * @return children
     */
    ArrayList<Layout> getChildren() {
        return null;
    }

    /**
     *
     * @throws IOException
     */

    /**
     *
     * @param leafLayout lowest layout in the tree
     */

    /**
     * @param child child of this in tree
     * @param subLayout sublayout of this in the tree
     * @throws IOException
     */
    public void replaceChild(LeafLayout child, CompositeLayout subLayout) throws IOException {
    }

    protected void replaceChildV2(LeafLayout v1, CompositeLayout subLayout) throws IOException {
    }

    public void addLeaf(Layout layout) throws IOException {

    }

    public void addLayout(LeafLayout child, CompositeLayout layout) throws IOException {

    }

    protected void removeChild(Layout parent) throws IOException {
    }

    protected void replaceLayout(Layout v1, CompositeLayout parent) throws IOException {
    }

    public void replaceActiveChild (LeafLayout child, Layout subLayout) throws IOException{}

    protected void removeLeaf(Layout leaf) {
    }
}