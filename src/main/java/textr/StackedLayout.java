package textr;

import java.io.IOException;
import java.util.ArrayList;


public class StackedLayout extends CompositeLayout {
    /**
     * initializes StackedLayout
     * @param rowPosition
     * @param columnPosition
     * @param width
     * @param height
     * @throws IOException If an input or output exception occurred
     */
    public StackedLayout(int rowPosition, int columnPosition, int width, int height) throws IOException {
        super(rowPosition, columnPosition, width, height);
    }

    public StackedLayout(){
    }

    /**
     * Recalculates width, height, rowPosition, columnPosition after the tree has been changed
     * @throws IOException If an input or output exception occurred
     */
    public void recalculateChildDimensions() throws IOException{
        int width = getWidth();
        int height = getHeight();
        int rowPosition = getRowPosition();
        int columnPosition = getColumnPosition();


        ArrayList<Layout> children = this.getChildren();
        int size = children.size();
        int rest = height % size;
        int layoutHeight = ((height )/ size)-1;

        for (int i = 0; i < getChildren().size(); i++ ) {
            Layout layout = children.get(i);
            if(layout instanceof LeafLayout){
                layout.setWidth(width-1);
                if(layout != children.getLast()){
                    layout.setHeight(layoutHeight);
                }else{
                    layout.setHeight(layoutHeight+rest);
                }
            }else{
                layout.setWidth(width);
                if(layout != children.getLast()){
                    layout.setHeight(layoutHeight+1);
                }else{
                    layout.setHeight(layoutHeight+1+rest);
                }
            }
            layout.setRowPosition(rowPosition);
            layout.setColumnPosition(columnPosition);
            rowPosition += layoutHeight+1;

        }
        for(int i =0; i<size;i++){
            if(children.get(i) instanceof CompositeLayout l){
                l.recalculateChildDimensions();
            }
        }

        render();


    }
}