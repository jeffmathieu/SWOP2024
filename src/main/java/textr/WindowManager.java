package textr;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

import static textr.KeyInput.*;

public class WindowManager extends JFrame {
    TerminalPanel terminalPanel = new TerminalPanel();
    Manager manager;
    private KeyAdapter keyAdapter;

    /**
     * Creates and displays a new window containing a duplicate of the given view.
     */
    public WindowManager(String filepath, int[] dim) throws IOException {
        super("Additional Window");
        terminalPanel.buffer = new char[dim[0]][dim[1]];

        setVisible(true);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        try {
            SwingIO io = new SwingIO(terminalPanel);
            getContentPane().add(terminalPanel);


            manager = new Manager(io, new String[]{filepath});
            pack();
            manager.windows.add(this);

            terminalPanel.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    FontMetrics metrics = terminalPanel.getFontMetrics(getFont());
                    int nbRows = (terminalPanel.getHeight()-metrics.getHeight()) / metrics.getHeight()-1;
                    int nbCols = terminalPanel.getWidth() / 7;
                    terminalPanel.buffer = new char[nbRows][nbCols];
                    terminalPanel.clearBuffer();
                    Manager.io.clearScreen();
                    CompositeLayout layout = (CompositeLayout) manager.rootLayout;
                    layout.setWidth(nbCols);
                    layout.setHeight(nbRows);
                    try {
                        layout.recalculateChildDimensions();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    java.util.List<Runnable> resizeListenersCopy = List.copyOf(terminalPanel.resizeListeners);
                    for (Runnable listener : resizeListenersCopy)
                        listener.run();
                }
            });
            this.addWindowFocusListener(new WindowFocusListener() {
                @Override
                public void windowGainedFocus(WindowEvent e) {
                    if (keyAdapter == null) {
                        keyAdapter = createKeyAdapter();
                        terminalPanel.addKeyListener(keyAdapter);
                    }
                }

                @Override
                public void windowLostFocus(WindowEvent e) {
                    if (keyAdapter != null) {
                        terminalPanel.keyListeners.clear();
                        terminalPanel.removeKeyListener(keyAdapter);
                        keyAdapter = null;
                    }
                }
            });
            setLocationRelativeTo(null);
            //handleIO();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    private KeyAdapter createKeyAdapter() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyPressed = mapKeyEventToCode(e);
                try {
                    if (keyPressed == 3) {
                        terminalPanel.removeKeyListener(this);
                        keyAdapter = null;
                        dispose();
                    } else if (keyPressed == 7) {
                        terminalPanel.removeKeyListener(this);
                        keyAdapter = null;
                    }
                    manager.handleInput(keyPressed);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
    }

    public void handleIO() throws IOException {
            final int[] keyPressed = {0};
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyPressed[0] = mapKeyEventToCode(e);
                try {
                    if(keyPressed[0] == 3){
                        terminalPanel.removeKeyListener(this);
                        terminalPanel.keyListeners.remove(this);
                        restoreState();
                    }else if(keyPressed[0] == 7){
                        terminalPanel.removeKeyListener(this);
                        terminalPanel.keyListeners.remove(this);
                    }
                    manager.handleInput(keyPressed[0]);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        if(terminalPanel.keyListeners.isEmpty()){
            terminalPanel.keyListeners.add(keyAdapter);
            terminalPanel.addKeyListener(keyAdapter);
            this.keyAdapter = keyAdapter;
        }

    }
    private int mapKeyEventToCode(KeyEvent e) {
            if (e.isControlDown()) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_N: return CTRL_N;
                    case KeyEvent.VK_P: return CTRL_P;
                    case KeyEvent.VK_T: return CTRL_T;
                    case KeyEvent.VK_R: return CTRL_R;
                    case KeyEvent.VK_C: return CTRL_C;
                    case KeyEvent.VK_S: return CTRL_S;
                    case KeyEvent.VK_Z: return CTRL_Z;
                    case KeyEvent.VK_U: return CTRL_U;
                    case KeyEvent.VK_G: return CTRL_G;
                    case KeyEvent.VK_D: return CTRL_D;
                    case KeyEvent.VK_W: return CTRL_W;
                    case KeyEvent.VK_J: return CTRL_J;
                    case KeyEvent.VK_O: return CTRL_O;
                    default: return 0; // No valid control combination
                }
            } else {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT: return ARROW_LEFT;
                    case KeyEvent.VK_RIGHT: return ARROW_RIGHT;
                    case KeyEvent.VK_UP: return ARROW_UP;
                    case KeyEvent.VK_DOWN: return ARROW_DOWN;
                    case KeyEvent.VK_F4: return F4;
                    case KeyEvent.VK_BACK_SPACE: return BACKSPACE;
                    case KeyEvent.VK_ENTER: return ENTER;
                    default: {
                        char c = e.getKeyChar();
                        return (c >= 32 && c <= 126) ? c : 0; // Printable characters or 0 if non-printable
                    }
                }
            }
    }


    public void restoreState() throws IOException {
        ((CompositeLayout)manager.rootLayout).recalculateChildDimensions();
    }
}
