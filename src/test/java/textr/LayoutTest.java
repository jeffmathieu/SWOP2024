package textr;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class LayoutTest {
    @Test
    public void compositeTest() throws IOException {
        StackedLayout root=new StackedLayout(1,1,60,30);
        String file1 = "src/test/java/textr/file1";
        String file2 = "src/test/java/textr/file2";
        String file3 = "src/test/java/textr/file3";

        root.addChild(file1);
        root.addChild(file2);
        root.addChild(file3);

        ArrayList<Layout> children = root.getChildrenCopy();
        assertEquals(root.getSize(),3);
        assertEquals(children.getFirst().getHeight(),9);
        assertEquals(children.getFirst().getWidth(),59);
        assertEquals(children.get(1).getRowPosition(),11);


        LeafLayout left = CompositeLayout.getMostLeftLeaf(root);
        LeafLayout right = CompositeLayout.getMostRightLeaf(root);

        assertEquals(root.getChildIndex(left),0);
        assertEquals(root.getChildIndex(right),2);

        //removeLayout

        SideBySideLayout childLayout = new SideBySideLayout();
        left.getParent().removeLeaf(left);
        childLayout.addLeaf(left);
        root.addLayout(right,childLayout);
        assertEquals(CompositeLayout.getMostRightLeaf(childLayout),right);

        StackedLayout root2=new StackedLayout(1,1,60,30);

        root2.addChild(file1);
        root2.addChild(file2);
        root2.addChild(file3);
        left = CompositeLayout.getMostLeftLeaf(root2);

        LeafLayout next = left.getRight();
        SideBySideLayout subLayout = new SideBySideLayout();

        root2.removeLeaf(next);
        subLayout.addLeaf(next);
        root2.replaceChildV2(left,subLayout);

        assertEquals(CompositeLayout.getMostLeftLeaf(root2),next);
        assertEquals(left.getLeft(),next);


    }

    @Test
    public void ReplaceChild() throws IOException {
        StackedLayout root=new StackedLayout(1,1,60,30);
        String file1 = "src/test/java/textr/file1";
        String file2 = "src/test/java/textr/file2";
        String file3 = "src/test/java/textr/file3";

        root.addChild(file1);
        root.addChild(file2);
        root.addChild(file3);

        LeafLayout left = CompositeLayout.getMostLeftLeaf(root);

        SideBySideLayout sub =new SideBySideLayout(1,1,60,30);
        root.replaceChild(left,sub);
    }
    @Test
    public void LeafLayoutTest()throws IOException{
        Manager.io = new IOadapter();

        StackedLayout root=new StackedLayout(1,1,60,30);
        String file1 = "src/test/java/textr/file1";
        String file2 = "src/test/java/textr/file2";
        String file3 = "src/test/java/textr/file3";

        root.addChild(file1);
        root.addChild(file2);
        root.addChild(file3);
        LeafLayout activeLeaf = CompositeLayout.getMostLeftLeaf(root);

        activeLeaf.rotateClockWise();

        ArrayList<Layout> children = root.getChildrenCopy();
        assertEquals(CompositeLayout.getMostLeftLeaf(root),activeLeaf.getLeft());
        assertEquals(children.get(0).getClass(),SideBySideLayout.class);
        assertEquals(children.get(0).getHeight(),15);
        assertEquals(children.getFirst().getChildren().getFirst(),activeLeaf.getLeft());

        StackedLayout root2=new StackedLayout(1,1,60,30);
        root2.addChild(file1);
        root2.addChild(file2);
        root2.addChild(file3);
        activeLeaf = CompositeLayout.getMostLeftLeaf(root2);

        activeLeaf.rotateCounterClockWise();

        children = root2.getChildrenCopy();
        assertEquals(CompositeLayout.getMostLeftLeaf(root2),activeLeaf);
        assertEquals(children.get(0).getClass(),SideBySideLayout.class);
        assertEquals(children.get(0).getHeight(),15);

        activeLeaf.removeView();
        children = root2.getChildrenCopy();

        assertEquals(children.size(),2);

        StackedLayout root3=new StackedLayout(1,1,60,30);
        root3.addChild(file1);
        root3.addChild(file2);
        root3.addChild(file3);

        children = root3.getChildrenCopy();
        LeafLayout mostRight = CompositeLayout.getMostRightLeaf(root3);
        mostRight.rotateClockWise();
        children = root3.getChildrenCopy();
        assertEquals(children.size(),3);

        StackedLayout root4=new StackedLayout(1,1,60,30);
        root4.addChild(file1);
        root4.addChild(file2);
        root4.addChild(file3);


        LeafLayout first = (LeafLayout) root4.getChildrenCopy().getFirst();
        first.rotateClockWise();

        CompositeLayout parent = (CompositeLayout) first.getParent();
        LeafLayout child = (LeafLayout) parent.getChildrenCopy().getFirst();
        child.rotateClockWise();



    }


}
