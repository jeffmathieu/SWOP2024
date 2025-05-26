package textr;


import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import static textr.KeyInput.*;


public class GameView extends View {
    private SnakeGame game;
    private static Timer timer;


    /**
     * initialises gameView
     * @param rowPosition
     * @param columnPosition
     * @param width
     * @param height
     * @param active
     * @throws IOException
     */
    public GameView(int rowPosition, int columnPosition, int width, int height, boolean active) throws IOException {
        super(rowPosition, columnPosition, width, height, active);
        game = new SnakeGame(height, width, rowPosition, columnPosition);
    }


    /**
     * updates border of game view (without scrollbar)
     */
    public void updateborder(){
        String borderCharacter = "+";
        if (active) {
            borderCharacter = "*";
        }

        // print borders
        for (int i = 0; i < height+1; i++) {
            Manager.io.printText(rowPosition + i, columnPosition, borderCharacter);
            Manager.io.printText(rowPosition + i+1, columnPosition + width, borderCharacter);
        }

        for (int i = 0; i < width+1; i++) {
            Manager.io.printText(rowPosition, columnPosition + i, borderCharacter);
            Manager.io.printText(rowPosition + height, columnPosition + i, borderCharacter);
        }
    }

    @Override
    public void setActive(boolean b) {
        active = b;
    }

    @Override
    public void updateView() {
        updateborder();
    }


    public void startGameWindow(WindowManager window) {
        SwingIO io = (SwingIO) Manager.io;
        long deadline = 1000;
        AtomicReference<Boolean> repeat = new AtomicReference<>(false);
        timer = new Timer((int) deadline, e -> {
            if(game.move()==-1){
                timer.stop();
                game.clearStatus();
            }
            game.printSnake();
        });
            KeyAdapter keyAdapter = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int keyCode = io.mapKeyEventToCode(e);
                    if (keyCode == 3 && !timer.isRunning()) {
                        timer.stop();
                        window.terminalPanel.removeKeyListener(this);
                        try {
                            window.restoreState();
                            window.handleIO();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }else if (keyCode == 13 && !timer.isRunning()){
                        Manager.io.printText((rowPosition+1)+ (game.HEIGHT/3), (columnPosition+1)+(game.WIDTH /3), "                                  ");
                        timer.start();
                        game.printSnake();
                    }
                    else{
                        try {
                            if(keyCode == ARROW_UP || keyCode == ARROW_LEFT || keyCode == ARROW_RIGHT || keyCode==ARROW_DOWN){
                                game.setDirection(keyCode);
                                game.move();
                                game.printSnake();
                            }

                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
                }
            };
        window.terminalPanel.addKeyListener(keyAdapter);


        timer.setRepeats(true);
        timer.start();
    }

    public void startGameTerminal() {
        game.printSnake();
        while(true) {
            long startTime = System.currentTimeMillis();
            try {
                // deadline is 1 second divided by time multiplier so snake moves faster when longer
                long deadline = startTime + 1000 / game.getTimeMulti();
                int c = Manager.io.readByte(deadline);
                if(c == 3){
                    return;
                }
                game.setDirection(c);
            } catch (TimeoutException | IOException e) {
                // do nothing
            }
            if(game.move() == 3){
                return;
            }
            game.printSnake();
        }
    }

}
