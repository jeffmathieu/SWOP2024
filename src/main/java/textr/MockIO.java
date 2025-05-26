package textr;

import io.github.btj.termios.Terminal;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public class MockIO implements IO{
    public ArrayList<Integer> readBuffer = new ArrayList<>();

    Point cursor=new Point(0,0);
    char[][] console = new char[30][60];

    MockIO(int height, int width){
        console = new char[height][width];
    }

    MockIO(int height, int width, char[][] initialConsole){
        console = new char[height][width];
        console = initialConsole.clone();
    }

    public MockIO() {

    }

    @Override
    public void clearScreen() {
        for(int i = 0; i < console.length; i++){
            for(int j =0; j < console[0].length;j++){
                console[i][j] = ' ';
            }
        }
    }

    @Override
    public void enterRawInputMode() {

    }

    @Override
    public void leaveRawInputMode() {

    }

    @Override
    public void moveCursor(int row, int column) {
        this.cursor.x = row;
        this.cursor.y = column;
    }

    @Override
    public void printText(int row, int column, String text) {
        for(int i = 0; i  < text.length() && i < console[0].length-2;i++){
            //console[row-1][column+i-1] = text.charAt(i);
        }
    }


    public void writeByte(int b){
        this.readBuffer.add(b);
    }
    @Override
    public int readByte() throws IOException {
        return readBuffer.removeFirst();
    }

    @Override
    public int readByte(long deadline) throws IOException, TimeoutException {
        return readBuffer.removeFirst();
    }


    @Override
    public void reportTextAreaSize() {
        System.out.print("hello");
        //System.out.print("\033[18t");
        //System.out.flush();
    }

    @Override
    public int[] getDimensions() throws IOException {
        return new int[]{30, 60};
    }

    @Override
    public void setInputListener(Runnable listener) {

    }
}
