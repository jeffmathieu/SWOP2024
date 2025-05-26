package textr;


import textr.directoryview.DirectoryView;
import textr.json.SimpleJsonObject;

import java.io.IOException;
import java.util.ArrayList;

import static textr.KeyInput.*;

public class Manager {
    public static IO io;

    private static LeafLayout activeLayout;
    static Layout rootLayout;

    ArrayList<WindowManager> windows = new ArrayList<WindowManager>();

    /**
     * initializes layout for the files given by the user
     * @param filepaths list of filepaths the user wants to open
     * @throws IOException
     */
    public Manager(IO io,String[] filepaths) throws IOException {
        this.io = io;
        initializeLayout(filepaths);
    }

    /**
     *
     * @param filepaths
     * @throws IOException
     */
    private void initializeLayout(String[] filepaths) throws IOException {
        int [] dimensions = io.getDimensions();
        int height = dimensions[0];
        int width = dimensions[1];

        if (filepaths.length == 0) {
            io.clearScreen();
            io.leaveRawInputMode();
            throw new RuntimeException("No file specified");
        }

        rootLayout= new StackedLayout(1, 1, width, height);

        for (String filepath : filepaths) {
            rootLayout.addChild(filepath);
        }
        setActiveLayout((LeafLayout) rootLayout.getChildren().getFirst());
    }



    /**
     *
     * @param layout activeLayout
     */
    public static void setActiveLayout(LeafLayout layout) throws IOException {
        if (activeLayout != null) {
            activeLayout.getView().setActive(false);
        }
       activeLayout= layout;
       activeLayout.getView().setActive(true);
    }


    private void closeActiveView() throws IOException {
        LeafLayout leftLayout = activeLayout.getLeft();
        LeafLayout rightLayout = activeLayout.getRight();
        if (rightLayout != null) {
            activeLayout.removeView();
            setActiveLayout(rightLayout);
        }else if(leftLayout != null){
            activeLayout.removeView();
            setActiveLayout(leftLayout);
        } else {
            io.clearScreen();
            io.leaveRawInputMode();
            System.exit(0);

        }
        ((CompositeLayout) rootLayout).recalculateChildDimensions();
        rootLayout.render();

    }



    /**
     * Handle input:
     * - Ctrl+N focus on the next view
     * - Ctrl+P focus on the previous view
     * - Ctrl+T rotate focused view counterclockwise
     * - Ctrl+R rotate focused view clockwise
     * - ARROW_UP, ARROW_DOWN, ARROW_LEFT, ARROW_RIGHT
     * - Ctrl+C to exit
     * - Ctrl+S to save the contents of focused view
     * - backspace,enter to edit the buffer
     * - insert character from the pressed key in buffer
     *
     * @param input
     * @return
     * @throws IOException
     */
    public int handleInput(int input) throws IOException{
        if ( input == 14 /* Ctrl+N */) {
            LeafLayout rightLayout = activeLayout.getRight();
            if (rightLayout != null) {
                setActiveLayout(rightLayout);
                activeLayout.render();

            }
        }
        else if (input == 16 /* Ctrl+P */) {
            LeafLayout leftLayout = activeLayout.getLeft();
            if (leftLayout != null) {
                setActiveLayout(leftLayout);
                activeLayout.render();
            }
        }
        else if (input == 20 /* Ctrl+T */) {
            activeLayout.rotateCounterClockWise();
            activeLayout.render();
        }
        else if (input == 18 /* Ctrl+R */) {
            activeLayout.rotateClockWise();
            activeLayout.render();
        }

        // pijltjes
        if (input == ARROW_UP || input == ARROW_DOWN || input == ARROW_LEFT || input == ARROW_RIGHT) {
            if (this.activeLayout.getView() instanceof TextView) {
                ((TextView) this.activeLayout.getView()).moveCursor(input);
            }
            if (this.activeLayout.getView() instanceof DirectoryView) {
                ((DirectoryView) this.activeLayout.getView()).moveCursor(input);
            }
        }
        // Check for 'ctr+c' to exit
        else if (input == 3) {
            io.clearScreen();
            io.leaveRawInputMode();
            System.exit(0);
        }
        else if (input == 19 /* Ctrl+S */) {
            if (activeLayout.getView() instanceof TextView)
                ((TextView) activeLayout.getView()).save();
            //rootLayout.render();
        }
        else if (input == 26 /* ctrl+Z */ ){
            if (activeLayout.getView() instanceof TextView)
                ((TextView) activeLayout.getView()).undo();
        }

        else if (input == 21 /* ctrl+U*/ ){
            if (activeLayout.getView() instanceof TextView)
                ((TextView) activeLayout.getView()).redo();
        }
        else if (input == 7 /* Ctrl+G */ ) {
            startGame();
        }
        else if (input == 127 /* backspace */){
            if (activeLayout.getView() instanceof TextView)
                ((TextView) activeLayout.getView()).deleteChar();
        }
        else if (input == 13 /* enter */){
            if (activeLayout.getView() instanceof TextView)
                ((TextView) activeLayout.getView()).addEmptyLine();
            if (activeLayout.getView() instanceof DirectoryView)
                ((DirectoryView) activeLayout.getView()).openEntry();
        }
        else if (input == 4 /* ctrl+D */){
            if (activeLayout.getView() instanceof TextView)
                copyView();
        }
        else if (input == 23 /* Ctrl+W */) {
            int [] dimensions = io.getDimensions();
            TextView view = (TextView) activeLayout.getView();
            windows.add(new WindowManager(view.getBuffer().getFilePath(),dimensions));
            return 0;
        }
        else if (input == F4){
            closeActiveView();
        }
        else if (input == 10 /* ctrl+j*/){
            TextView view = (TextView) activeLayout.getView();
            SimpleJsonObject jsonObject = view.jsonify();
            if (jsonObject != null){
               openDirectoryView(jsonObject);
            }


        }

        else if (32 <= input && input <= 126){
            if (activeLayout.getView() instanceof TextView)
                ((TextView) activeLayout.getView()).insertChar(input);
        }

        else if (input == 15 /* Ctrl+O */) {
            openDirectoryView();
        }

        return 1;
    }


    private void openDirectoryView() throws IOException {
        io.clearScreen();
        DirectoryView directoryView = new DirectoryView(activeLayout.getRowPosition(), activeLayout.getColumnPosition() + activeLayout.getWidth() / 2, activeLayout.getWidth() / 2, activeLayout.getHeight(), true);
        LeafLayout directoryLayout = new LeafLayout(activeLayout.getRowPosition(), activeLayout.getColumnPosition() + activeLayout.getWidth()/2, activeLayout.getWidth()/2, activeLayout.getHeight()+1, directoryView);
        directoryLayout.setRight(activeLayout.getRight());
        SideBySideLayout Twinlayout = new SideBySideLayout(activeLayout.getRowPosition(), activeLayout.getColumnPosition(), activeLayout.getWidth(), activeLayout.getHeight()+1);
        Twinlayout.addChild(activeLayout);
        Twinlayout.addChild(directoryLayout);
        rootLayout.replaceActiveChild(activeLayout, Twinlayout);
        setActiveLayout(directoryLayout);
        rootLayout.render();
    }

    private void openDirectoryView(SimpleJsonObject object) throws IOException {
        io.clearScreen();
        DirectoryView directoryView = new DirectoryView(activeLayout.getRowPosition(), activeLayout.getColumnPosition() + activeLayout.getWidth() / 2, activeLayout.getWidth() / 2, activeLayout.getHeight(), true, object);
        LeafLayout directoryLayout = new LeafLayout(activeLayout.getRowPosition(), activeLayout.getColumnPosition() + activeLayout.getWidth()/2, activeLayout.getWidth()/2, activeLayout.getHeight()+1, directoryView);
        directoryLayout.setRight(activeLayout.getRight());
        SideBySideLayout Twinlayout = new SideBySideLayout(activeLayout.getRowPosition(), activeLayout.getColumnPosition(), activeLayout.getWidth(), activeLayout.getHeight()+1);
        Twinlayout.addChild(activeLayout);
        Twinlayout.addChild(directoryLayout);
        rootLayout.replaceActiveChild(activeLayout, Twinlayout);
        setActiveLayout(directoryLayout);
        rootLayout.render();
    }


    private void copyView() throws IOException {
        io.clearScreen();
        LeafLayout layout = ((TextView) activeLayout.getView()).duplicate();
        layout.setRight(activeLayout.getRight());
        SideBySideLayout twinlayout = new SideBySideLayout(activeLayout.getRowPosition(), activeLayout.getColumnPosition(), activeLayout.getWidth(), activeLayout.getHeight()+1);
        twinlayout.addChild(activeLayout);
        twinlayout.addChild(layout);
        rootLayout.replaceActiveChild(activeLayout, twinlayout);
        setActiveLayout(layout);
        rootLayout.render();
    }


    public static void replaceActiveLayout(LeafLayout layout) throws IOException {
        activeLayout.getParent().replaceActiveChild(activeLayout, layout);
        setActiveLayout(layout);
        io.clearScreen();
        rootLayout.render();
    }

    /**
     * splits active view into two view (a textview and a gameview), new gameView becomes active view, starts game
     * @throws IOException
     */
    public void startGame() throws IOException {
        Layout parent = activeLayout.getParent();
        int index = parent.getChildren().indexOf(activeLayout);
        LeafLayout old = activeLayout;
        int rowposition = activeLayout.getRowPosition();
        int columnposition = activeLayout.getColumnPosition();
        int width = activeLayout.getWidth();
        int height = activeLayout.getHeight();
        GameView gameView;
        LeafLayout gameLeaf;
        activeLayout.getView().setActive(false);

        CompositeLayout layout;
        if(parent instanceof StackedLayout){
            gameView = new GameView(rowposition, columnposition + width/2+1,width/2+2, height,true);
            gameLeaf = new LeafLayout(rowposition, columnposition + width/2+1,width/2+2, height,gameView);
            layout = new SideBySideLayout(rowposition, columnposition, width+1, height+1);

        }else{
            gameView = new GameView(rowposition + height/2+1, columnposition,width, height/2+2,true);
            gameLeaf = new LeafLayout(rowposition + height/2+1,columnposition,width,height/2+2,gameView);
            layout = new StackedLayout(rowposition, columnposition, width+2, height);
        }

        layout.addLeaf(activeLayout);
        layout.addLeaf(gameLeaf);

        parent.replaceActiveChild(activeLayout, layout);
        setActiveLayout(gameLeaf);
        io.clearScreen();
        ((CompositeLayout) parent).recalculateChildDimensions();

        if(io instanceof SwingIO){
            gameView.startGameWindow(windows.getFirst());
        }else{
            gameView.startGameTerminal();
            setActiveLayout(old);
            parent.removeChild(layout);
            ((CompositeLayout) parent).addAt(old,index);
            ((CompositeLayout) parent).recalculateChildDimensions();
        }




    }

    public static LeafLayout getActiveLayout() {
        return activeLayout;
    }

    public static FileBuffer getFileBuffer(String filepath) {
        // Iterate over all LeafLayouts in rootLayout
        for (Layout layout : rootLayout.getChildren()) {
            if (layout instanceof LeafLayout) {
                LeafLayout leafLayout = (LeafLayout) layout;
                View view = leafLayout.getView();
                // Check if the View is a TextView
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    FileBuffer fileBuffer = textView.getBuffer();
                    // Check if the FileBuffer's filepath matches the provided filepath
                    if (fileBuffer.getFilePath().equals(filepath)) {
                        return fileBuffer;
                    }
                }
            }
        }
        // Return null if no matching FileBuffer is found
        return null;
    }







}
