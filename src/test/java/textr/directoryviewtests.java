package textr;

import org.junit.Before;
import org.junit.Test;
import textr.directoryview.*;
import textr.json.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static textr.KeyInput.*;

public class directoryviewtests {


    @Before
    public void setUp() {
        Manager.io = new IOadapter();
    }


    @Test

    public void normaltest() throws IOException {
        DirectoryView view = new DirectoryView(20, 20, 20, 20, true);

    }

    @Test
    public void jsonTest() throws IOException {
        String exampleJsonObjectLF = """
				{
				  "Documents": {
				    "SWOP": {
				      "assignment_it2.txt": "This is the assignment for iteration 2.",
				      "assignment_it3.txt": "This is the assignment for iteration 3."
				    },
				    "json_in_string.json": "{\\r\\n  \\"foo\\": \\"bar\\"\\r\\n}"
				  }
				}""";

        textr.json.SimpleJsonObject object = textr.json.SimpleJsonParser.parseSimpleJsonObject(exampleJsonObjectLF);
        DirectoryView view = new DirectoryView(20, 20, 20, 20, true, object);

    }

    @Test
    public void setactive() throws IOException {
        DirectoryView view = new DirectoryView(20, 20, 20, 20, true);
        view.setActive(true);
    }

    @Test
    public void setinactive() throws IOException {
        DirectoryView view = new DirectoryView(20, 20, 20, 20, true);
        view.setActive(false);
    }

    @Test
    public void updateView() throws IOException {
        DirectoryView view = new DirectoryView(20, 20, 20, 20, true);
        view.updateView();
        view.clearView();
    }

    @Test
    public void moveCursor() throws IOException {
        DirectoryView view = new DirectoryView(20, 20, 20, 20, true);
        view.moveCursor(ARROW_DOWN);
        view.moveCursor(ARROW_UP);
        view.moveCursor(ARROW_LEFT);
        view.moveCursor(ARROW_RIGHT);
    }

    @Test
    public void openEntry() throws IOException {
        DirectoryView view = new DirectoryView(20, 20, 20, 20, true);
        view.openEntry();
        view.moveCursor(ARROW_DOWN);
        view.openEntry();
    }

    @Test
    public void leafEntry() throws IOException {
        SimpleJsonString jsonString = new SimpleJsonString(new TextLocation(6, 27), 30, "test");
        SimpleJsonProperty jsonProperty = new SimpleJsonProperty("test", jsonString);
        JsonLeafEntry leafEntry = new JsonLeafEntry(jsonProperty);
        assertEquals(leafEntry.getValue(), jsonProperty.value);
        assertEquals(leafEntry.getName(), jsonProperty.name);


    }

}
