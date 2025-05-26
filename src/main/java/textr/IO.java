package textr;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface IO {
    public void clearScreen();
    public void enterRawInputMode();
    public void leaveRawInputMode();

    public void moveCursor(int row, int column);

    public void printText(int row, int column, String text);

    public int readByte() throws IOException;

    public int readByte(long deadline) throws IOException, TimeoutException;


    public void reportTextAreaSize();

    public int[] getDimensions() throws IOException;

    public void setInputListener(Runnable listener);

}
