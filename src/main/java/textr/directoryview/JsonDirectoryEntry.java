package textr.directoryview;

import textr.json.SimpleJsonObject;
import textr.json.SimpleJsonProperty;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class JsonDirectoryEntry extends DirectoryEntry {
    private String name;
    private LinkedHashMap<String, SimpleJsonProperty> properties;



    public JsonDirectoryEntry(SimpleJsonObject json) {
        this.name = "root";
        this.properties = json.properties;
        this.parent = null;
    }

    public JsonDirectoryEntry(SimpleJsonProperty property, Entry parent){
        this.name = property.name;
        this.properties = ((SimpleJsonObject) property.value).properties;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    @Override
    public Entry getParent() {
        return parent;
    }


    public List<Entry> getChildren() {
            Collection<SimpleJsonProperty> childproperties = properties.values();
            List<Entry> entries = new java.util.ArrayList<>();
            for (SimpleJsonProperty property : childproperties) {
                if (property.value instanceof SimpleJsonObject) {
                    entries.add(new JsonDirectoryEntry(property, this));
                } else {
                    entries.add(new JsonLeafEntry(property));
                }
            }
            return entries;

    }
}
;