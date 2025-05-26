package textr;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class FileBufferTest {
    @Test
    public void bufferTest() throws IOException {
        FileBuffer buffer1 = new FileBuffer("src/test/java/textr/example.txt");

        assertEquals(buffer1.getFilePath(),"src/test/java/textr/example.txt");
        ArrayList<String> result = new ArrayList<String>(Arrays.asList("hallo Jeff", "dit", "is", "een", "test", "file"));
        //assertEquals(buffer1.getContentBuffer(), result);
        assertEquals(buffer1.getBufferLength(), 6);
        /*ArrayList<String>  lineTwoToFour = buffer1.getFromToLine(1,4);
        assertEquals(lineTwoToFour, Arrays.asList("dit", "is", "een", "test"));
        assertEquals(buffer1.getBufferAmountofChars(), 26);
        assertEquals(buffer1.getLine(2), "is");
        buffer1.setContentBuffer(2, "test");
        assertEquals(buffer1.getLine(2), "test");
        ArrayList<String> tempList = new ArrayList<String>(Arrays.asList("hallo", "test", "file", "changed"));
        buffer1.setNewContentBuffer(tempList);
        assertEquals(buffer1.getContentBuffer(), tempList);*/
    }
}