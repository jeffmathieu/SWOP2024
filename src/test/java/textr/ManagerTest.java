package textr;

import org.junit.Test;

import java.io.IOException;
import java.util.NoSuchElementException;

public class ManagerTest {
    @Test
    public void testManager() throws IOException {
        String[] files = {"src/test/java/textr/file1","src/test/java/textr/file2"};
        MockIO io = new MockIO();
        io.readBuffer.add(18);
        io.readBuffer.add(7);
        io.readBuffer.add(3);
        io.readBuffer.add(33);

        Manager manager = new Manager(io,files);
        InputHandler handler = new InputHandler(io, manager);
        io.readBuffer.add(3);
        handler.run();

    }
}
