package textr;


import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import static textr.KeyInput.*;

public class SnakeGame {

    int HEIGHT;
    int WIDTH;
    private LinkedList<Point> snake;
    private Point food;
    private char[][] grid;
    private Direction direction;
    private Random random;
    private final int INITIAL_SNAKE_LENGTH = 6;
    private boolean isPaused;
    private int score;
    private int highScore;
    private int rowPosition;
    private int columnPosition;
    private long timeMulti;


    public int getHEIGHT() { return HEIGHT; }
    public int getWIDTH() { return WIDTH; }
    public LinkedList<Point> getSnake() { return new LinkedList<Point>(snake); }
    public Point getPoint() { return food; }
    public char[][] getGrid() {
        char[][] result = new char[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                result[i][j] = grid[i][j];
            }
        }
        return result;
    }

    public Direction getDirection() { return direction; }
    public boolean getIsPaused() { return isPaused; }
    public int getScore() { return score; }
    public int getHighScore() { return highScore; }
    public long getTimeMulti() { return timeMulti; }


    /**
     * Initialises a snake game
     * @param height of playing field
     * @param width of playing field
     * @param rowPosition where in the terminal the game is played
     * @param columnPosition where in the terminal the game is played
     */
    public SnakeGame(int height, int width, int rowPosition, int columnPosition) {
        this.HEIGHT = height-4;
        this.WIDTH = width-3;
        this.rowPosition = rowPosition+1;
        this.columnPosition = columnPosition+1;
        grid = new char[HEIGHT][WIDTH];
        snake = new LinkedList<>();
        direction = Direction.RIGHT;
        random = new Random();
        score = 0;
        timeMulti = 1;
        highScore = 0;
        initGrid();
        spawnFood();
        spawnSnake();
    }

    enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    /**
     * prints grid for testing
     */
    public void printGrid() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                Manager.io.printText(rowPosition+i, columnPosition+j, String.valueOf(grid[i][j]));
            }
        }
    }

    /**
     * prints the snake, food item and scores (on the bottom) relative to the rowPosition and columnPosition in the Terminal
     */
    public void printSnake() {
        Manager.io.printText(rowPosition + food.y, columnPosition + food.x, "*");
        for (int i = 0; i < snake.size(); i++) {
            if (i == 0) {
                if(direction == Direction.UP) {
                    Manager.io.printText(rowPosition + snake.get(i).y, columnPosition + snake.get(i).x, "^");
                } else if (direction == Direction.RIGHT) {
                    Manager.io.printText(rowPosition + snake.get(i).y, columnPosition + snake.get(i).x, ">");
                } else if (direction == Direction.LEFT) {
                    Manager.io.printText(rowPosition + snake.get(i).y, columnPosition + snake.get(i).x, "<");
                } else if (direction == Direction.DOWN) {
                    Manager.io.printText(rowPosition + snake.get(i).y, columnPosition + snake.get(i).x, "v");
                }
            } else {
                Manager.io.printText(rowPosition + snake.get(i).y, columnPosition + snake.get(i).x, "O");
            }
        }
        printStatusGame();
    }

    /**
     * prints scores (on the bottom) and bottom edge of playing field, relative to the rowPosition and columnPosition, in the Terminal
     */
    private void printStatusGame(){
        for(int i = 0; i < getWIDTH()+1; i++) {
            Manager.io.printText(rowPosition + getHEIGHT(), columnPosition + i, "*");
        }
        checkScore();
        Manager.io.printText(rowPosition + getHEIGHT()+1, columnPosition + 1, "Score: " + score + "    HighScore: " + highScore);
    }


    /**
     * clears the text of scores, so terminal is clean again
     */
    public void clearStatus() {
        for (int i = 0; i < getWIDTH()+1; i++) {
            Manager.io.printText(rowPosition + getHEIGHT() + 1, columnPosition + i, " ");
        }
    }


    /**
     * deletes the snake from the terminal
     */
    private void deleteSnake(){
        for(int i = 0; i < snake.size(); i++) {
            Manager.io.printText(rowPosition + snake.get(i).y, columnPosition + snake.get(i).x, " ");
        }
    }

    /**
     * deletes the food item from the terminal
     */
    private void deleteFood(){
        Manager.io.printText(rowPosition + food.y, columnPosition + food.x, " ");
    }


    /**
     * initializes grid (is not really necessary)
     */
    public void initGrid() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                    grid[i][j] = ' ';
            }
        }
    }

    /**
     * spawns snake in the grid
     */
    private void spawnSnake() {
        int x = WIDTH / 2;
        int y = HEIGHT / 2;
        snake.clear();
        for (int i = 0; i < INITIAL_SNAKE_LENGTH; i++) {
            snake.add(new Point(x - i, y));
            grid[y][x - i] = 'O';
        }
    }

    /**
     * sets game to default settings
     */
    public void restoreGame(){
        initDirection();
        deleteFood();
        initGrid();
        spawnSnake();
        spawnFood();
        this.score = 0;
        this.timeMulti = 1;
    }


    /**
     * spawns a food item on a random place in the grid (which is not occupied by the snake)
     */
    private void spawnFood() {
        int x, y;
        do {
            x = random.nextInt(WIDTH - 2) + 1;
            y = random.nextInt(HEIGHT - 2) + 1;
        } while (grid[y][x] != ' ');
        food = new Point(x, y);
        grid[y][x] = '*';
    }

    /**
     * sets direction back to initial direction (right)
     */
    private void initDirection() {
        this.direction = Direction.RIGHT;
    }


    /**
     * moves the snake one step in the set direction, checks for eating food items, collisions with border, updates score
     */
    public int move() {
        Point head = snake.getFirst();
        Point newHead = new Point(head.x, head.y);
        this.score += 1;
        deleteSnake();
        switch (direction) {
            case UP:
                newHead.y--;
                break;
            case DOWN:
                newHead.y++;
                break;
            case LEFT:
                newHead.x--;
                break;
            case RIGHT:
                newHead.x++;
                break;
        }
        if (newHead.equals(food)) {
            spawnFood();
            // score is 100 nu
            this.score = score + 100;

            if(this.timeMulti <= 10) {
                this.timeMulti += 1;
            }
            snake.addFirst(newHead);
            grid[newHead.y][newHead.x] = 'O';
        } else if (collided(newHead)) {
            Manager.io.printText(rowPosition+(HEIGHT/3), columnPosition+(WIDTH /3), "Game Over!, press enter to restart");

            restoreGame();

            boolean test = true;
            while(test) {
                long startTime = System.currentTimeMillis();

                long currentTime = System.currentTimeMillis();
                long secondsPassed = (currentTime - startTime) / 1000;

                try {
                    long deadline = startTime + (secondsPassed + 1) * 1000;
                    if(Manager.io instanceof SwingIO){
                        return -1;
                    }else{
                        int c = Manager.io.readByte(deadline);
                        if (c == 13) {
                            Manager.io.printText(rowPosition+ (HEIGHT/3), columnPosition+(WIDTH /3), "                                  ");
                            clearStatus();
                            test = false;
                        }else if(c==3){
                            Manager.io.printText(rowPosition+ (HEIGHT/3), columnPosition+(WIDTH /3), "                                  ");
                            return 3;
                        }
                    }
                } catch (TimeoutException | IOException e) {
                    // do nothing
                }
            }
        } else {
            Point tail = snake.removeLast();
            grid[tail.y][tail.x] = ' ';
            snake.addFirst(newHead);
            grid[newHead.y][newHead.x] = 'O';
        }
        checkScore();
        return 0;
    }

    /**
     * checks if high score needs to be overwritten
     */
    private void checkScore() {
        if (this.score >= this.highScore) {
            this.highScore = this.score;
        }
    }

    /**
     * checks if point p collides with border
     * @param p
     * @return
     */
    private boolean collided(Point p) {
        return p.x < 0 || p.x >= WIDTH || p.y < 0 || p.y >= HEIGHT || this.snake.contains(p);
    }


    /**
     * sets direction of snake to given direction (checks so that snake cant make 180° turn)
     * @param newDirection
     */
    public void setDirection(int newDirection) throws IOException {
        if(Manager.io instanceof SwingIO){
            switch (newDirection){
                case ARROW_UP -> handleDirection('A');
                case ARROW_DOWN -> handleDirection('B');
                case ARROW_RIGHT -> handleDirection('C');
                case ARROW_LEFT -> handleDirection('D');
            }
        }else{
            if(newDirection == '\033'){
                int c1 = Manager.io.readByte();
                if (c1 == '['){
                    int c2 = Manager.io.readByte();
                    handleDirection(c2);
                }

            }
        }


    }

    private void handleDirection(int c){
        switch (c) {
            case 'A':
                if (direction != Direction.DOWN)
                    direction = Direction.UP;
                break;
            case 'B':
                if (direction != Direction.UP)
                    direction = Direction.DOWN;
                break;
            case 'C':
                if (direction != Direction.LEFT)
                    direction = Direction.RIGHT;
                break;
            case 'D':
                if (direction != Direction.RIGHT)
                    direction = Direction.LEFT;
                break;

        }
    }

    static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Point point = (Point) obj;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }
}