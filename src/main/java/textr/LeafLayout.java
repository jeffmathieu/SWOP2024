package textr;

import io.github.btj.termios.Terminal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class LeafLayout extends Layout{

    private View view;
    private LeafLayout left;
    private LeafLayout right;


    /**
     * initializes LeafLayout
     * @param filepath
     * @param rowPosition
     * @param columnPosition
     * @param width
     * @param height
     * @throws IOException
     */
    public LeafLayout(String filepath, int rowPosition, int columnPosition, int width, int height) throws IOException {
        super(rowPosition, columnPosition, width, height);
        view = new TextView(filepath, rowPosition, columnPosition, width, height, false);
    }

    public LeafLayout(int rowPosition, int columnPosition, int width, int height) throws IOException {
        super(rowPosition, columnPosition, width, height);
        view = new TextView( rowPosition, columnPosition, width, height, false);
    }

    public LeafLayout(int rowPosition, int columnPosition, int width, int height, View view) throws IOException {
        super(rowPosition, columnPosition, width, height);
        this.view = view;
    }


    /**
     * 
     * @return LeafLayout's associated view
     */
    public View getView() {
        return view;
    }

    /**
     *
     * @param view
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     *
     * @return left leafLayout
     */
    public LeafLayout getLeft() {
        return left;
    }

    /**
     *
     * @param left
     */
    public void setLeft(LeafLayout left) {
        this.left = left;
        if (left != null){
            if (left.getRight() != this){
                left.setRight(this);
            }
        }
        
    }

    /**
     *
     * @return left leafLayout
     */
    public LeafLayout getRight() {
        return right;
    }

    /**
     *
     * @param right
     */
    public void setRight(LeafLayout right) {
        this.right = right;
        if (right != null){
            if (right.getLeft() != this){
                right.setLeft(this);
            }
        }
    }


    /**
     * Rotates the view clockwise with the next view in the tree
     * @throws IOException If an input or output exception occurred
     */
    public void rotateClockWise() throws IOException{
        if (right != null){
            LeafLayout v1 = this;
            LeafLayout v2 = right;


            if (v1.getParent() == v2.getParent()){
                CompositeLayout parent = (CompositeLayout) v1.getParent();
                CompositeLayout grandparent = (CompositeLayout) v1.getParent().getParent();

                if(grandparent!= null && parent.getSize() ==2){
                    if(parent instanceof StackedLayout){
                        SideBySideLayout subLayout = new SideBySideLayout();
                        subLayout.addLeaf(v1);
                        subLayout.addLeaf(v2);

                        parent.getParent().replaceLayout(parent,subLayout);
                    }else{
                        StackedLayout subLayout = new StackedLayout();
                        setPointers(v1,v2);
                        subLayout.addLeaf(v2);
                        subLayout.addLeaf(v1);

                        parent.getParent().replaceLayout(parent,subLayout);
                    }
                    parent = (CompositeLayout) v1.getParent();
                    if(parent.getClass() == grandparent.getClass()){
                        parent.sinkChild();
                    }

                    grandparent.recalculateChildDimensions();


                } else{
                    if(v1.getParent() instanceof StackedLayout){

                        SideBySideLayout subLayout = new SideBySideLayout();

                        parent.removeLeaf(v2);
                        subLayout.addLeaf(v2);

                        parent.replaceChildV2(v1, subLayout);

                    }else if (v1.getParent() instanceof SideBySideLayout){

                        StackedLayout subLayout = new StackedLayout();

                        parent.removeLeaf(v2);
                        subLayout.addLeaf(v2);

                        parent.replaceChildV2(v1,subLayout);

                    }

                }
            } else  {
                CompositeLayout parent = (CompositeLayout) v2.getParent(); /*parent == SideBySide*/
                parent.removeLeaf(v2);
                v1.getParent().addLeaf(v2);
                CompositeLayout grandparent = (CompositeLayout) v1.getParent().getParent();
                if(v1.getParent() == parent.getParent())/*one level below*/{
                    if(parent.getChildren().size() < 2){
                        parent.removeLayout();
                    }
                    parent = (CompositeLayout) v1.getParent();
                    parent.recalculateChildDimensions();
                } else if(grandparent.containsChild(parent))/*same level*/{
                    if(!parent.getChildren().isEmpty()){
                        parent.removeLayout();
                    }
                    grandparent.recalculateChildDimensions();
                }else{
                    grandparent.recalculateChildDimensions();
                }

                if(grandparent != null && grandparent.getChildrenCopy().size()==1){
                    grandparent.removeLeaf(v1.getParent());
                    v1.getParent().setParent(null);
                }
            }




        }else{
            System.out.print("\007");
        }
        
    }

    /**
     *
     * @throws IOException If an input or output exception occurred
     */
    public void rotateCounterClockWise() throws IOException{
        if (right != null){
            LeafLayout v1 = this;
            LeafLayout v2 = right;

            if (v1.getParent() == v2.getParent()){
                CompositeLayout grandparent = (CompositeLayout) v1.getParent().getParent();

                if(grandparent!= null && grandparent.getSize() == 2){
                    CompositeLayout parent = (CompositeLayout) v1.getParent();

                    if(parent instanceof StackedLayout){
                        SideBySideLayout subLayout = new SideBySideLayout();

                        setPointers(v1,v2);

                        subLayout.addLeaf(v2);
                        subLayout.addLeaf(v1);

                        parent.getParent().replaceLayout(parent,subLayout);
                    }else{
                        StackedLayout subLayout = new StackedLayout();

                        setPointers(v1,v2);

                        subLayout.addLeaf(v2);
                        subLayout.addLeaf(v1);
                        parent.getParent().replaceLayout(parent,subLayout);
                    }


                }
                else{
                    CompositeLayout parent = (CompositeLayout) v2.getParent();

                    if(v1.getParent() instanceof StackedLayout){
                        SideBySideLayout subLayout = new SideBySideLayout();

                        parent.removeLeaf(v1);
                        subLayout.addLeaf(v1);
                        parent.addLayout(v2,subLayout);

                    }else if (v1.getParent() instanceof SideBySideLayout){
                        StackedLayout subLayout = new StackedLayout();
                        parent.removeLeaf(v1);
                        subLayout.addLeaf(v1);

                        parent.addLayout(v2,subLayout);

                    }
                    parent.recalculateChildDimensions();


                }
            } else  {
                CompositeLayout parent = (CompositeLayout) v2.getParent(); /*parent == SideBySide*/
                parent.removeLeaf(v2);
                v1.getParent().addLeaf(v2);
                CompositeLayout grandparent = (CompositeLayout) v1.getParent().getParent();
                if(v1.getParent() == parent.getParent())/*one level below*/{
                    if(parent.getChildren().size() < 2){
                        parent.removeLayout();
                    }
                    grandparent.recalculateChildDimensions();
                }else if(grandparent.containsChild(parent))/*same level*/{
                    if(!parent.getChildren().isEmpty()){
                        parent.removeLayout();
                    }
                    grandparent.recalculateChildDimensions();
                }else{
                    grandparent.recalculateChildDimensions();
                }

                if(grandparent != null && grandparent.getChildrenCopy().size()==1){
                    grandparent.removeLeaf(parent);
                    parent.setParent(null);
                }

            }

        }else{
            System.out.print("\007");
        }

    }

    public void removeView() throws IOException {
        

        LeafLayout leftNode = left;
        LeafLayout rightNode = right;
        Layout currentParent = this.parent;
        currentParent.removeChild(this);

        PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8");

        
        if (leftNode != null){
            writer.println("leftnode:");
            writer.println(leftNode.toString());
        } else {
            writer.println("leftnode: null");
        }

        if (rightNode != null){
            writer.println("rightnode:");
            writer.println(rightNode.toString());
        } else {
            writer.println("rightnode: null");
        }

        writer.close();



        if (leftNode != null && rightNode != null){
            leftNode.setRight(rightNode);
            rightNode.setLeft(leftNode);
        } else if (leftNode != null){
            leftNode.setRight(null);
        } else if (rightNode != null){
            rightNode.setLeft(null);
        }

    }

    public void setPointers(LeafLayout child1, LeafLayout child2){
        LeafLayout left = child1.getLeft();
        LeafLayout right = child2.getRight();

        if(left != null && right != null){
            child2.setLeft(left);
            child2.setRight(child1);
            child1.setLeft(child2);
            child1.setRight(right);
            left.setRight(child2);
            right.setLeft(child1);
        }else if(right != null){
            child2.setLeft(null);
            child2.setRight(child1);
            child1.setRight(right);
            child1.setLeft(child2);
            right.setLeft(child1);
        }else if(left != null){
            child1.setRight(null);
            child1.setLeft(child2);
            child2.setLeft(left);
            child2.setRight(child1);
            left.setRight(child2);
        }
    }

    /**
     * Renders the view attached to this LeafLayout
     * @throws IOException If an input or output exception occurred
     */
    public void render() throws IOException {
        view.setRowPosition(getRowPosition());
        view.setColumnPosition(getColumnPosition());
        view.setHeight(getHeight());
        view.setWidth(getWidth());
        view.updateView();
    }

}
