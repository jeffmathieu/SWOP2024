package textr;

import io.github.btj.termios.Terminal;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class SnakeGameTest {

    @Before
    public void setUp() throws Exception {
        Manager.io = new IOadapter();
    }

    @Test
    public void initTest() throws IOException {
        // test init game and 2 moves and a direction change
        SnakeGame game = new SnakeGame(20,20, 1, 1);
        assertEquals(game.getHEIGHT(), 16);
        assertEquals(game.getWIDTH(), 17);
        assertFalse(game.getIsPaused());
        assertEquals(game.getDirection(), SnakeGame.Direction.RIGHT);
        assertEquals(game.getScore(), 0);
        game.move();
        assertEquals(game.getScore(), 1);
        assertEquals(game.getTimeMulti(), 1);

        LinkedList<SnakeGame.Point> snake = new LinkedList<SnakeGame.Point>();
        snake.add(new SnakeGame.Point(9, 8));
        snake.add(new SnakeGame.Point(8, 8));
        snake.add(new SnakeGame.Point(7, 8));
        snake.add(new SnakeGame.Point(6, 8));
        snake.add(new SnakeGame.Point(5, 8));
        snake.add(new SnakeGame.Point(4, 8));
        for (int i = 0; i < game.getSnake().size(); i++){
            assertEquals(game.getSnake().get(i), snake.get(i));
        }
        game.setDirection('z');
        game.move();
        snake.removeLast();
        snake.addFirst(new SnakeGame.Point(9, 7));
//        for (int i = 0; i < game.getSnake().size(); i++){
//            assertEquals(game.getSnake().get(i), snake.get(i));
//        }

        // test restore game
        game.restoreGame();
        snake.clear();
        snake.add(new SnakeGame.Point(8, 8));
        snake.add(new SnakeGame.Point(7, 8));
        snake.add(new SnakeGame.Point(6, 8));
        snake.add(new SnakeGame.Point(5, 8));
        snake.add(new SnakeGame.Point(4, 8));
        snake.add(new SnakeGame.Point(3, 8));
        for (int i = 0; i < game.getSnake().size(); i++){
            assertEquals(game.getSnake().get(i), snake.get(i));
        }
        assertEquals(game.getScore(), 0);
        assertEquals(game.getHighScore(), 2);

        // test going left if going right already
        game.setDirection('q');
        game.move();
        snake.removeLast();
        snake.addFirst(new SnakeGame.Point(9, 8));
        for (int i = 0; i < game.getSnake().size(); i++){
            assertEquals(game.getSnake().get(i), snake.get(i));
        }

        // test going down
        game.setDirection('s');
        game.move();
        snake.removeLast();
        snake.addFirst(new SnakeGame.Point(9, 9));
//        for (int i = 0; i < game.getSnake().size(); i++){
//            assertEquals(game.getSnake().get(i), snake.get(i));
//        }

        // test going left
        game.setDirection('q');
        game.move();
        snake.removeLast();
        snake.addFirst(new SnakeGame.Point(8, 9));
//        for (int i = 0; i < game.getSnake().size(); i++){
//            assertEquals(game.getSnake().get(i), snake.get(i));
//        }

        // test going down and right
        game.setDirection('s');
        game.move();
        snake.removeLast();
        snake.addFirst(new SnakeGame.Point(8, 10));
        game.setDirection('d');
        game.move();
        snake.removeLast();
        snake.addFirst(new SnakeGame.Point(9, 10));


//        for (int i = 0; i < game.getSnake().size(); i++){
//            assertEquals(game.getSnake().get(i), snake.get(i));
//        }



        /*
        assertEquals(game.getPoint(), new SnakeGame.Point(7, 6));


        game.move();

        assertEquals(game.getScore(), 100);
        snake.addFirst(new SnakeGame.Point(7, 6));
        for (int i = 0; i < game.getSnake().size(); i++){
            assertEquals(game.getSnake().get(i), snake.get(i));
        }
        game.printGrid();

        game.setDirection(1000);
        game.move();
        game.printGrid();
        game.move();
        game.printGrid();
        assertEquals(game.getScore(), 200);
        assertEquals(game.getSnake().size(), 8);

        LinkedList<SnakeGame.Point> snake1 = new LinkedList<SnakeGame.Point>();
        snake1.add(new SnakeGame.Point(7, 4));
        snake1.add(new SnakeGame.Point(7, 5));
        snake1.add(new SnakeGame.Point(7, 6));
        snake1.add(new SnakeGame.Point(6, 6));
        snake1.add(new SnakeGame.Point(5, 6));
        snake1.add(new SnakeGame.Point(4, 6));
        snake1.add(new SnakeGame.Point(3, 6));
        snake1.add(new SnakeGame.Point(2, 6));
        for (int i = 0; i < game.getSnake().size(); i++){
            assertEquals(game.getSnake().get(i), snake1.get(i));
        }
        game.setDirection(1003);
        game.move();
        game.printGrid();
        game.move();
        game.printGrid();
        game.move();
        game.printGrid();
        game.move();
        game.printGrid();
        game.move();
        game.printGrid();

        assertEquals(game.getIsPaused(), true);
        assertEquals(game.getScore(), 300);
        assertEquals(game.getSnake().size(), 9);

         */
    }

}