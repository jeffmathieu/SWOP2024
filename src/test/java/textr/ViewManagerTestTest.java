package textr;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ViewManagerTestTest {

    // Testing ViewManager interaction with View classes
    @Test
    public void viewManagerTest() throws IOException {
        String[] viewsFilePaths = {"src/test/java/textr/example.txt", "src/test/java/textr/example2.txt"};

        ViewManagerTest manager = new ViewManagerTest(viewsFilePaths);

        // Initialize Test
        assertEquals(manager.getViews().length, 2);
        //assertEquals(manager.getViews()[0].getBuffer().getLine(1), "dit");
        assertEquals(manager.getViews()[0].getRowCursorPosition(), 2);
        assertEquals(manager.getViews()[0].getColumnCursorPosition(), 2);
        assertEquals(manager.getViews()[0].getFilepath(), "src/test/java/textr/example.txt");
        assertEquals(manager.getActiveViewIndex(), 0);
        assertTrue(manager.getViews()[0].getActive());
        assertFalse(manager.getViews()[0].getDirty());
        assertEquals(manager.getViews()[0].getWidth(), 500);
        assertEquals(manager.getViews()[0].getHeight(), 249);
        assertEquals(manager.getViews()[0].getRowPosition(), 1);
        assertEquals(manager.getViews()[0].getColumnPosition(), 1);
        assertEquals(manager.getViews()[1].getWidth(), 500);
        assertEquals(manager.getViews()[1].getHeight(), 249);
        assertEquals(manager.getViews()[1].getRowPosition(), 251);
        assertEquals(manager.getViews()[1].getColumnPosition(), 1);
/*
        // Insert Char Test
        manager.getViews()[0].insertChar('a');
        assertEquals(manager.getViews()[0].getBuffer().getLine(0), "ahallo Jeff");
        assertEquals(manager.getViews()[0].getRowCursorPosition(), 2);
        assertEquals(manager.getViews()[0].getColumnCursorPosition(), 3);
        assertTrue(manager.getViews()[0].getDirty());

        // Backspace Test
        manager.getViews()[0].deleteChar();
        assertEquals(manager.getViews()[0].getBuffer().getLine(0), "hallo Jeff");
        assertEquals(manager.getViews()[0].getRowCursorPosition(), 2);
        assertEquals(manager.getViews()[0].getColumnCursorPosition(), 2);

        // Enter Test
        manager.getViews()[0].addEmptyLine();
        assertEquals(manager.getViews()[0].getBuffer().getLine(0), "");
        assertEquals(manager.getViews()[0].getBuffer().getLine(1), "hallo Jeff");
        assertEquals(manager.getViews()[0].getBuffer().getBufferLength(), 7);
        assertEquals(manager.getViews()[0].getRowCursorPosition(), 3);
        assertEquals(manager.getViews()[0].getColumnCursorPosition(), 2);

        // Backspace after Enter Test
        manager.getViews()[0].deleteChar();
        assertEquals(manager.getViews()[0].getBuffer().getLine(0), "hallo Jeff");
        assertEquals(manager.getViews()[0].getBuffer().getLine(1), "dit");
        assertEquals(manager.getViews()[0].getBuffer().getBufferLength(), 6);
        assertEquals(manager.getViews()[0].getRowCursorPosition(), 2);
        assertEquals(manager.getViews()[0].getColumnCursorPosition(), 2);

        // Move cursor Tests
        final int ARROW_UP = 1000;
        final int ARROW_DOWN = 1001;
        final int ARROW_LEFT = 1002;
        final int ARROW_RIGHT = 1003;
        // UP on init (so no movement should happen)
        manager.getViews()[0].moveCursor(ARROW_UP);
        assertEquals(manager.getViews()[0].getRowCursorPosition(), 2);
        assertEquals(manager.getViews()[0].getColumnCursorPosition(), 2);
        // DOWN
        manager.getViews()[0].moveCursor(ARROW_DOWN);
        assertEquals(manager.getViews()[0].getRowCursorPosition(), 3);
        assertEquals(manager.getViews()[0].getColumnCursorPosition(), 2);
        // RIGHT
        manager.getViews()[0].moveCursor(ARROW_RIGHT);
        assertEquals(manager.getViews()[0].getRowCursorPosition(), 3);
        assertEquals(manager.getViews()[0].getColumnCursorPosition(), 3);
        // UP
        manager.getViews()[0].moveCursor(ARROW_UP);
        assertEquals(manager.getViews()[0].getRowCursorPosition(), 2);
        assertEquals(manager.getViews()[0].getColumnCursorPosition(), 3);
        // LEFT
        manager.getViews()[0].moveCursor(ARROW_LEFT);
        assertEquals(manager.getViews()[0].getRowCursorPosition(), 2);
        assertEquals(manager.getViews()[0].getColumnCursorPosition(), 2);

        // Enter test text moves
        for (int i = 0; i < 6; i++) { manager.getViews()[0].moveCursor(ARROW_RIGHT); }
        manager.getViews()[0].addEmptyLine();
        assertEquals(manager.getViews()[0].getBuffer().getLine(0), "hallo ");
        assertEquals(manager.getViews()[0].getBuffer().getLine(1), "Jeff");
        assertEquals(manager.getViews()[0].getBuffer().getBufferLength(), 7);
        assertEquals(manager.getViews()[0].getRowCursorPosition(), 3);
        assertEquals(manager.getViews()[0].getColumnCursorPosition(), 2);

        // Active Test
        manager.setActiveView(1);
        assertEquals(manager.getActiveViewIndex(), 1);
        assertFalse(manager.getViews()[0].getActive());
        assertTrue(manager.getViews()[1].getActive());
        assertFalse(manager.getViews()[1].getDirty());

        // Save Test
        manager.getViews()[0].save();
        assertFalse(manager.getViews()[0].getDirty());*/
    }
}