package textr.directoryview;

import textr.json.SimpleJsonProperty;
import textr.json.SimpleJsonValue;

public class JsonLeafEntry extends LeafEntry {
    private String name;
    private SimpleJsonValue value;

    public JsonLeafEntry(SimpleJsonProperty property) {
        this.name = property.name;
        this.value = property.value;
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Entry getParent() {
        return parent;
    }

    public SimpleJsonValue getValue(){
        return value;
    }
}
