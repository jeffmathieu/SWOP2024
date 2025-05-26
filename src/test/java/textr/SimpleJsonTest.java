package textr;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashMap;

import org.junit.Test;
import textr.json.*;

public class SimpleJsonTest {
	
	void checkObject(textr.json.SimpleJsonObject object) {
		assertNull(object.property);
		assertArrayEquals(new String[] {"Documents"}, object.properties.keySet().toArray());
		SimpleJsonProperty documentsProperty = object.properties.get("Documents");
		assertSame(object, documentsProperty.object);
		assertEquals("Documents", documentsProperty.name);
		assertInstanceOf(textr.json.SimpleJsonObject.class, documentsProperty.value);
		textr.json.SimpleJsonObject documents = (textr.json.SimpleJsonObject)documentsProperty.value;
		assertArrayEquals(new String[] {"SWOP", "json_in_string.json"}, documents.properties.keySet().toArray());
		SimpleJsonProperty jsonProperty = documents.properties.get("json_in_string.json");
		assertSame(documents, jsonProperty.object);
		assertEquals("json_in_string.json", jsonProperty.name);
		assertInstanceOf(SimpleJsonString.class, jsonProperty.value);
		SimpleJsonString json = (SimpleJsonString)jsonProperty.value;
		assertEquals(new TextLocation(6, 27), json.start);
		assertEquals(30, json.length);
		assertEquals("""
				{
				  "foo": "bar"
				}""".replace("\n", "\r\n"), json.value);
	}

	@Test
    public void test() {
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
		String exampleJsonObjectCRLF = exampleJsonObjectLF.replace("\n", "\r\n");
		{
			textr.json.SimpleJsonObject object = textr.json.SimpleJsonParser.parseSimpleJsonObject(exampleJsonObjectLF);
			checkObject(object);
			assertEquals(exampleJsonObjectLF, textr.json.SimpleJsonGenerator.generate("\n", object));
			assertEquals(exampleJsonObjectCRLF, textr.json.SimpleJsonGenerator.generate("\r\n", object));
		}
		{
			textr.json.SimpleJsonObject object = textr.json.SimpleJsonParser.parseSimpleJsonObject(exampleJsonObjectCRLF);
			checkObject(object);
			assertEquals(exampleJsonObjectLF, textr.json.SimpleJsonGenerator.generate("\n", object));
			assertEquals(exampleJsonObjectCRLF, textr.json.SimpleJsonGenerator.generate("\r\n", object));
		}
		
		String assignment2bis = "This is the assignment for iteration 2,\nwith attachment.";
		String assignment2bisLiteral = textr.json.SimpleJsonGenerator.generateStringLiteral(assignment2bis);
		assertEquals("\"This is the assignment for iteration 2,\\nwith attachment.\"", assignment2bisLiteral);
	}

}
