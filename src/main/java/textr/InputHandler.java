package textr;
import java.io.IOException;
import static textr.KeyInput.*;


public class InputHandler {
    private IO io;
    private Manager manager;

    public InputHandler(IO io, Manager manager) {
        this.io = io;
        this.manager = manager;
    }

    public void run()  {
        io.setInputListener(() -> java.awt.EventQueue.invokeLater(() -> {
            try {
                int input = readInput();
                if(input != 23){
                    manager.handleInput(input);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    private int readByte() throws IOException {


        int i = io.readByte();
        if(i==23){
            manager.handleInput(i);
        }else{
            io.setInputListener(() -> java.awt.EventQueue.invokeLater(() -> {
                try {
                    int input = readInput();
                    manager.handleInput(input);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
        }
        return i;
    }



/**
 * reads key input
 * @return input
 * @throws IOException
 */
    private int readInput() throws IOException {
        int c = readByte();
        if (c != '\033') {
            return c;
        } else {
            int c1 = readByte();
            if (c1 != '[') {
                if (c1 == 'O') {
                    int c3 = readByte();
                    if (c3 == 'S') {
                        return F4;
                    } else {
                        return c1;
                    }
                }

            } else {
                int c2 = readByte();
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
        return c;
    }


    public void handleIOAfter(){

    }

}