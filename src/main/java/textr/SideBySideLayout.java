package textr;

import io.github.btj.termios.Terminal;

import java.io.IOException;
import java.util.ArrayList;

public class SideBySideLayout extends CompositeLayout {
    /**
     * initializes SideBySideLayout
     * @param rowPosition
     * @param columnPosition
     * @param width
     * @param height
     * @throws IOException If an input or output exception occurred
     */
    public SideBySideLayout(int rowPosition, int columnPosition, int width, int height) throws IOException {
        super(rowPosition, columnPosition, width, height);
    }

    public SideBySideLayout() {
    }


    /**
     *
     * Recalculates width, height, rowPosition, columnPosition after the tree has been changed
     * @throws IOException If an input or output exception occurred
     */
    public void recalculateChildDimensions() throws IOException{
        int width = getWidth();
        int height = getHeight();
        int rowPosition = getRowPosition();
        int columnPosition = getColumnPosition();

        int size = getChildren().size();
        ArrayList<Layout> children = getChildren();
        int rest = width % size;

        int layoutWidth = (width / size) -1;

        for (int i = 0; i < getChildren().size(); i++ ) {
            Layout layout = children.get(i);
            if(layout instanceof LeafLayout){
                layout.setHeight(height-1);
                if(layout != children.getLast()){
                    layout.setWidth(layoutWidth);
                }else{
                    layout.setWidth(layoutWidth+rest);
                }
                layout.setRowPosition(rowPosition);
                layout.setColumnPosition(columnPosition);
                columnPosition += layoutWidth+1;
            }else{
                layout.setHeight(height);
                if(layout != children.getLast()){
                    layout.setWidth(layoutWidth+1);
                }else{
                    layout.setWidth(layoutWidth+1+rest);
                }
                layout.setRowPosition(rowPosition);
                layout.setColumnPosition(columnPosition);
                columnPosition += layoutWidth+1;
            }
        }

        for(int i =0; i<size;i++){
            if(children.get(i) instanceof CompositeLayout l){
                l.recalculateChildDimensions();
            }
        }
        render();
    }
}