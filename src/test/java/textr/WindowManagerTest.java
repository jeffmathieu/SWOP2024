package textr;

import org.junit.Test;

import javax.swing.*;
import java.io.IOException;

public class WindowManagerTest extends JFrame {
    static TerminalPanel terminalPanel = new TerminalPanel();
    static int starRow = 0;
    static int starCol = 0;
    WindowManager windowManager;

    @Test
    public void testManager() throws IOException {
        awtMain(new String[]{});
        windowManager.handleIO();
    }
    private void awtMain(String[] args) throws IOException {
        terminalPanel.buffer = new char[30][60];
        String file1 = "src/test/java/textr/file1";
        String[] files = new String[]{"src/test/java/textr/file1"};
        int[] dim = new int[]{30,60};
        SwingIO io = new SwingIO(terminalPanel);
        windowManager = new WindowManager(file1,dim);
    }


}
