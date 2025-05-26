package textr;

import io.github.btj.termios.Terminal;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

public class Main {


    public static void awtMain(String[] args) throws IOException, RuntimeException {



        /*
        for (; ; ) {
            Terminal.enterRawInputMode();
            int c = Terminal.readByte();
            if (c == 'q')
                break;
            if (c == '\033') {
                int c1 = Terminal.readByte();
                if (c1 == '[') {
                    while (true) {
                        int c2 = Terminal.readByte();
                        System.out.printf("You entered escape sequence \\033[%c\n", c2);
                        if (c2 == 't') {
                            System.out.printf("You entered escape sequence \\033[%c\n", c2);
                        }
                    }
                }
            } else if (32 <= c && c <= 126)
                System.out.printf("You entered character %d = '%c'\n", c, c);
            else
                System.out.printf("You entered character %d\n", c);
        }*/


        //TODO kijken of dit juiste plaats is
        JFrame dummyFrame = new JFrame();
        dummyFrame.pack();
            if (!java.awt.EventQueue.isDispatchThread())
                throw new AssertionError("Should run in AWT dispatch thread!");
            String lineSeparator = getLineSeparator(args);
            System.setProperty("line.separator", lineSeparator);
            startApplication(new IOadapter(), args);
    }





    public static void main(String[] args) {
        /*
         * Note: it turns out that, at least on MacOS, using the
         * AWT thread causes AWT to be activated and the terminal
         * window to lose OS-level focus,
         * which means the user has to click the terminal window
         * to continue interacting with the app. An (optional)
         * workaround is to start using AWT only when you actually
         * want to show a GUI, and until then use
         * the old approach with blocking readByte calls.
         */

        java.awt.EventQueue.invokeLater(() -> {
            try {
                awtMain(args);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }



    private static String getLineSeparator(String[] args) {
        String lineSeparator = System.lineSeparator();
        for (String arg : args) {
            if (arg.equals("--lf")) {
                lineSeparator = "\n";
            } else if (arg.equals("--crlf")) {
                lineSeparator = "\r\n";
            }
        }
        return lineSeparator;
    }

    public static void startApplication(IO io, String[] args) {
        try {
            prepareScreen(io);
            validateFilePaths(args);
            Manager manager = new Manager(io, args);
            InputHandler inputHandler = new InputHandler(io, manager);
            inputHandler.run();
        } catch (Exception e) {
            io.clearScreen();
            e.printStackTrace();
        }
    }

    private static void prepareScreen(IO io) throws IOException {
        io.clearScreen();
        io.enterRawInputMode();
    }

    private static void validateFilePaths(String[] filepaths) {
        if (filepaths.length == 0) {
            Terminal.clearScreen();
            Terminal.leaveRawInputMode();
            throw new RuntimeException("No file specified");
        }
    }

}


