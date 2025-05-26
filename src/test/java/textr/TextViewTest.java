package textr;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;


import static org.junit.Assert.assertEquals;

public class TextViewTest {
    private TextView textView;

    @Before
    public void setUp() throws IOException {
        // Initialize the TextView object before each test
        // You may need to adjust the parameters as per your requirements

        // create a new testfile to save the text

        try {
            FileWriter writer = new FileWriter("src/test/java/textr/textviewtestfile");
            writer.write("New content for the file");
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


        this.textView = new TextView("src/test/java/textr/textviewtestfile", 1, 1, 50, 50, true);
    }

   @Test
    public void testDuplication() throws IOException {
        // Test the duplication of the TextView object
        LeafLayout duplicate = this.textView.duplicate();
        assertEquals(this.textView.getBuffer(), ((TextView) duplicate.getView()).getBuffer());

    }

    @Test
    public void testSave() throws IOException {
        // Test the save method of the TextView object

        // create a new testfile to save the text
        Manager.io = new IOadapter();
        textView.insertChar(33);
        this.textView.save();
        // check if the file is saved
        assertEquals("!New content for the file", new BufferedReader(new java.io.FileReader("src/test/java/textr/textviewtestfile")).readLine());
    }



}