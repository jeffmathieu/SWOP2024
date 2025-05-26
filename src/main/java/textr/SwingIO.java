package textr;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import static textr.KeyInput.*;

class TerminalPanel extends JPanel {

    char[][] buffer = new char[20][30];
    ArrayList<Runnable> resizeListeners = new ArrayList<>();
    ArrayList<KeyAdapter> keyListeners = new ArrayList<>();

    int cursorRow = 1;
    int cursorColumn = 1;
    void clearBuffer() {
        for (int i = 0; i < buffer.length; i++)
            Arrays.fill(buffer[i], ' ');
    }

    TerminalPanel() {
        setFont(new Font("Monospaced", Font.PLAIN, 12));
        clearBuffer();

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        FontMetrics fontMetrics = g.getFontMetrics();
        int fontHeight = fontMetrics.getHeight();
        int baseLineOffset = fontHeight - fontMetrics.getDescent();
        int charWidth = fontMetrics.charWidth('m');

        int y = baseLineOffset;
        for (int lineIndex = 0; lineIndex < buffer.length; lineIndex++) {
            g.drawChars(buffer[lineIndex], 0, buffer[lineIndex].length, 0, y);
            y += fontHeight;
        }

        // Draw the cursor as a vertical line
        g.setColor(Color.RED);
        int cursorX = (cursorColumn * charWidth) - charWidth;
        int cursorY = cursorRow * fontHeight;
        g.drawLine(cursorX, cursorY-fontHeight, cursorX, cursorY);
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fontMetrics = this.getFontMetrics(getFont());
        int width = fontMetrics.charWidth('m');
        int fontHeight = fontMetrics.getHeight();
        return new Dimension((width * buffer[0].length), (fontMetrics.getHeight() * buffer.length)+fontHeight);
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    public void printString(String str, int row, int col) {
        if (row <= buffer.length && col <= buffer[row-1].length && (col-1) + str.length() <= buffer[row-1].length) {
            str.getChars(0, str.length(), buffer[row-1], col-1);
            repaint();
        }
    }

    public void moveCursor(int row, int column) {
        if (row >= 1 && row <= buffer.length && column >= 1 && column <= buffer[row - 1].length) {
            this.cursorRow = row;
            this.cursorColumn = column;
            repaint();
        } else {
            throw new IllegalArgumentException("Invalid row or column");
        }
    }
}

public class SwingIO implements IO{
    TerminalPanel terminalPanel;


    public SwingIO(TerminalPanel terminalPanel){
        this.terminalPanel = terminalPanel;
    }

    @Override
    public void setInputListener(Runnable listener) {

    }


    @Override
    public void clearScreen() {
        terminalPanel.clearBuffer();
        terminalPanel.repaint();
    }

    @Override
    public void enterRawInputMode() {
        //do nothing
    }

    @Override
    public void leaveRawInputMode() {
        //do nothing
    }

    @Override
    public void moveCursor(int row, int column) {
        terminalPanel.moveCursor(row, column);
    }

    @Override
    public void printText(int row, int column, String text) {
        terminalPanel.printString(text, row, column);
    }


    @Override
    public int readByte() throws IOException {
        return 0;
    }

    public int mapKeyEventToCode(KeyEvent e) {
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
    @Override
    public int readByte(long deadline) throws IOException, TimeoutException {
        return 0;
    }


    @Override
    public void reportTextAreaSize() {
    }

    public int[] getDimensions(){
        return new int[]{terminalPanel.buffer.length,terminalPanel.buffer[0].length};

    }


}
