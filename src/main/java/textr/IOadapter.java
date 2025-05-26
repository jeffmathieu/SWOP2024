package textr;

import io.github.btj.termios.Terminal;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class IOadapter implements IO{

    public IOadapter(){

    }


    public void clearScreen(){
        Terminal.clearScreen();
    }

    @Override
    public void enterRawInputMode() {
        Terminal.enterRawInputMode();
    }

    @Override
    public void leaveRawInputMode() {
        Terminal.leaveRawInputMode();
    }

    @Override
    public void moveCursor(int row, int column) {
        Terminal.moveCursor(row,column);

    }

    @Override
    public void printText(int row, int column, String text) {
        Terminal.printText(row,column,text);

    }

    @Override
    public int readByte() throws IOException {
        return Terminal.readByte();
    }

    @Override
    public int readByte(long deadline) throws IOException, TimeoutException {
        return Terminal.readByte(deadline);
    }

    @Override
    public void reportTextAreaSize() {
        Terminal.reportTextAreaSize();
    }

    @Override
    public int[] getDimensions() throws IOException {
        Terminal.reportTextAreaSize();
        StringBuilder dimensions = new StringBuilder();
        // Read the IO bytes until t
        for (; ; ) {
            int c = Terminal.readByte();
            if (c == '\033') {
                int c1 = Terminal.readByte();
                if (c1 == '[') {
                    while (true) {
                        int c2 = Terminal.readByte();
                        if (c2 == 't') {
                            break;
                        }
                        dimensions.append((char) c2);
                    }
                    break;
                } else {
                    System.out.printf("Unknown escape sequence \\033[%c\n", c1);
                }
            }
        }
        // Extract the individual dimensions
        String[] dimensionArray = dimensions.toString().split(";");
        if (dimensionArray.length == 3) {
            int height = Integer.parseInt(dimensionArray[1]);
            int width = Integer.parseInt(dimensionArray[2]);
            return new int[]{height, width};
        } else {
            throw new RuntimeException("Invalid Width and or Height received");
        }

    }

    @Override
    public void setInputListener(Runnable listener) {
        Terminal.setInputListener(listener);
    }
}
