package textr;
import io.github.btj.termios.Terminal;
import java.io.IOException;

public class ViewManagerTest {

    private static final int ARROW_UP = 1000, ARROW_DOWN = 1001, ARROW_LEFT = 1002, ARROW_RIGHT = 1003;
    private int activeViewIndex;
    private ViewTest[] views;

    public ViewManagerTest(String[] filepaths) throws IOException {
        initializeViews(filepaths);
        setActiveView(0);
    }

    private void initializeViews(String[] filepaths) throws IOException {

        views = new ViewTest[filepaths.length];

        int height = 500;
        int width = 500;

        if (height/2 < filepaths.length) {
            throw new RuntimeException("Too many files on commandline");
        }

        int viewHeight = (height / filepaths.length)-1;
        int viewWidth = width;

        int rowPosition = 1;
        int columnPosition = 1;
        for (int i = 0; i < filepaths.length; i++) {
            String filepath = filepaths[i];
            boolean isActive = (i == 0);
            views[i] = new ViewTest(filepath, rowPosition, columnPosition, viewWidth, viewHeight, isActive);
            rowPosition += viewHeight + 1;
        }
    }

    public ViewTest[] getViews() { return this.views; }

    public void setActiveView(int index) {
        if (index >= 0 && index < views.length) {
            if (views[activeViewIndex] != null) {
                views[activeViewIndex].setActive(false);
            }
            activeViewIndex = index;
            views[activeViewIndex].setActive(true);
        }
    }

    public int getActiveViewIndex() { return this.activeViewIndex; }

    /*public void run() throws IOException {
        while (true) {
            int input = readInput();
            handleInput(input);
        }
    }

   /* private void handleInput(int input) throws IOException {
         view switching
        if (input == 16) {
            setActiveView((activeViewIndex - 1 + views.length) % views.length);
        }
        else if (input == 14) {
            setActiveView((activeViewIndex + 1) % views.length);
        }

        else if (input == ARROW_UP || input == ARROW_DOWN || input == ARROW_LEFT || input == ARROW_RIGHT) {
            views[activeViewIndex].moveCursor(input);
        }
         Check for 'ctr+c' to exit
        else if (input == 3) {
            Terminal.clearScreen();
            Terminal.leaveRawInputMode();
            System.exit(0);
        }

        else if (input == 19) {
            views[activeViewIndex].save();
        }

        else if (input == 127){
            views[activeViewIndex].deleteChar();
        }

        else if (input == 13){
            views[activeViewIndex].addEmptyLine();
        }

        else if (32 <= input && input <= 126){
            views[activeViewIndex].insertChar(input);
        }
    }
    private static int readInput() throws IOException {
        int c = Terminal.readByte();
        if (c != '\033') {
            return c;
        } else {
            int c1 = Terminal.readByte();
            if (c1 != '[') {
                return c1;
            } else {
                int c2 = Terminal.readByte();
                switch (c2) {
                    case 'A':
                        return ARROW_UP;
                    case 'B':
                        return ARROW_DOWN;
                    case 'C':
                        return ARROW_RIGHT;
                    case 'D':
                        return ARROW_LEFT;
                    default:
                        return c2;
                }
            }
        }
    }
    */
    /*
    @returns [height, width]
     */
    private static int[] getTerminalDimensions() throws IOException {
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
}
