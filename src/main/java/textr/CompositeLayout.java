package textr;

import java.io.IOException;

import java.util.ArrayList;

public abstract class CompositeLayout extends Layout{
    private final ArrayList<Layout> children = new ArrayList<>();
    private Layout parent;

    /**
     * initializes CompositeLayout
     * @param rowPosition row position of the layout in the terminal
     * @param columnPosition column position of the layout in the terminal
     * @param width width of the layout
     * @param height height of the layout
     * @throws IOException
     */
    public CompositeLayout(int rowPosition, int columnPosition, int width, int height) throws IOException {
        super(rowPosition, columnPosition, width, height);
    }

    public CompositeLayout() {
        super();
    }

    /**
     * gets children nodes
     * @return children
     */
    ArrayList<Layout> getChildren(){
        return children;
    }
    public int getChildIndex(Layout child){
        return children.indexOf(child);
    }

    public int getSize(){
        return children.size();
    }

    public ArrayList<Layout> getChildrenCopy(){
        return (ArrayList<Layout>) children.clone();
    }

    public boolean containsChild(Layout child){
        return children.contains(child);
    }


    /**
     * Adds child to this.children
     * @param filepath
     * @throws IOException
     */
    @Override
    public void addChild(String filepath) throws IOException{

        LeafLayout child = new LeafLayout(filepath, 1, 1, getWidth(), getHeight());
        child.setParent(this);

        if (!children.isEmpty()) {
            // find the most right leaf layout in node
            LeafLayout rightNode = getMostRightLeaf(this);

            // find the most left leaf layout in the new tree
            LeafLayout leftNode = getMostLeftLeaf(child);

            // set the left and right pointers

            if (leftNode != null) {
                leftNode.setLeft(rightNode);
            }
            if (rightNode != null) {
                rightNode.setRight(leftNode);
            }
        }

        // add the new child to the children list
        children.add(child);
        recalculateChildDimensions();
    }


    /**
     *
     * @param node
     * @return right leafLayout
     */
    // recursive function to find the most right leaf layout in the tree
    public static LeafLayout getMostRightLeaf(Layout node){
        if (node instanceof LeafLayout){
            return (LeafLayout) node;
        }
        else{
            if (!node.getChildren().isEmpty()){
                if (node.getChildren().getLast() instanceof LeafLayout){
                    return (LeafLayout) node.getChildren().getLast();
                }
                else{
                    return getMostRightLeaf(node.getChildren().getLast());
                }
            }
            return null;
        }
    }

    /**
     *
     * @param node
     * @return left leafLayout
     */
    // recursive function to find the most left leaf layout in the tree
    public static LeafLayout getMostLeftLeaf(Layout node){
        if (node instanceof LeafLayout){
            return (LeafLayout) node;
        }
        else{
            if (!node.getChildren().isEmpty()){
                if (node.getChildren().getFirst() instanceof LeafLayout){
                    return (LeafLayout) node.getChildren().getFirst();
                }
                else{
                    return getMostLeftLeaf(node.getChildren().getFirst());
                }
            }
        }
        return null;
    }

    /**
     * Add child in the tree; if children not empty -> adds to the right of the node
     *                        if empty              -> adds to the node
     * @param child child to be added to the tree
     * @throws IOException
     */
    @Override
    public void addChild(Layout child) throws IOException{
        if (!children.isEmpty()) {
            // find the most right leaf layout in node
            LeafLayout rightNode = getMostRightLeaf(this);

            // find the most left leaf layout in the new tree
            LeafLayout leftNode = getMostLeftLeaf(child);
            children.add(child);
            // set the left and right pointers
            if (leftNode != null) {
                leftNode.setLeft(rightNode);
            }
            if (rightNode != null) {
                rightNode.setRight(leftNode);
            }
        }
        else{
            children.add(child);
        }
        child.setParent(this);
        recalculateChildDimensions();
    }

    @Override
    public void addLeaf(Layout layout) throws IOException {
        children.add(layout);
        layout.setParent(this);
    }

    @Override
    public void addLayout(LeafLayout child, CompositeLayout layout) throws IOException {
        int index = children.indexOf(child);
        layout.setParent(this);
        children.set(index,layout);
        layout.addLeaf(child);
    }


    /**
     * Removes child from the node
     * @param child child to be added to the tree
     * @throws IOException
     */
    public void removeChild(Layout child) throws IOException{
        children.remove(child);
    }

    public void removeChild(LeafLayout child) throws IOException {
        children.remove(child);
        recalculateChildDimensions();
    }

    public void removeLeaf (Layout leaf){
        children.remove(leaf);
    }


    public void removeLayout() throws IOException {
        if(!children.isEmpty()){
            for(Layout child:children){
                parent.addLeaf(child);
            }
        }
        parent.removeChild(this);
    }

    public void addAt(Layout child, int index){
        child.setParent(this);
        this.children.add(index,child);
    }
    public void sinkChild() throws IOException {
        CompositeLayout grandParent = (CompositeLayout)this.getParent();
        int index = grandParent.getChildrenCopy().indexOf(this);
        grandParent.removeChild(this);
        if(!children.isEmpty()){
            for(int i = this.children.size()-1; i >= 0; i--){
                grandParent.addAt(this.children.get(i),index);
            }
        }
    }

    /**
     * Adds the child in the new sublayout in the tree
     * @param child child to be added to the tree
     * @param subLayout
     * @throws IOException
     */
    public void replaceChild(LeafLayout child, CompositeLayout subLayout) throws IOException {
        int index = children.indexOf(child);
        subLayout.addChild(child);
        subLayout.setParent(this);
        children.set(index, subLayout);
        subLayout.recalculateChildDimensions();
        for(Layout l : children){
            l.render();
        }
    }

    public void replaceChildV2(LeafLayout child, CompositeLayout subLayout) throws IOException {
        LeafLayout rightNode = child.getRight();
        LeafLayout leftNode = child.getLeft();
        int index = children.indexOf(child);

        if(rightNode.getRight() == null){
            child.setRight(null);
        }else{
            rightNode.getRight().setLeft(child);
            child.setRight(rightNode.getRight());
        }

        if(leftNode == null){
            rightNode.setLeft(null);
        }else{
            leftNode.setRight(rightNode);
            rightNode.setLeft(leftNode);
        }

        subLayout.addLeaf(child);
        subLayout.setParent(this);

        rightNode.setRight(child);
        child.setLeft(rightNode);

        children.set(index, subLayout);

        this.recalculateChildDimensions();
    }

    public void replaceActiveChild(LeafLayout child, Layout subLayout) throws IOException {
        int index = children.indexOf(child);
        if (index >= 0){
            // If the child is found in the current node's children, replace it with subLayout
            children.set(index, subLayout);
            subLayout.setParent(this);
        } else {
            // If the child is not found in the current node's children, search in the grandchildren
            for (Layout layout : children) {
                if (layout instanceof CompositeLayout) {
                    ((CompositeLayout) layout).replaceActiveChild(child, subLayout);
                }
            }
        }
    }



    @Override
    public void replaceLayout(Layout child, CompositeLayout layout) throws IOException {
        int index = children.indexOf(child);
        layout.setParent(this);
        children.set(index,layout);


        this.recalculateChildDimensions();
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
     * @param parent parent of this
     */
    public void setParent(Layout parent){
        this.parent = parent;
    }


    /**
     *
     * @throws IOException
     */
    public abstract void recalculateChildDimensions() throws IOException;

    /**
     *
     * @throws IOException
     */
    @Override
    public void render() throws IOException {
        for (Layout layout : getChildren()) {
            layout.render();
        }
    }


}