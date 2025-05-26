package textr.json;
import java.util.LinkedHashMap;

import static java.util.Collections.copy;

// this code is largely based on the code included in the assignment


public final class SimpleJsonObject extends SimpleJsonValue {
    public LinkedHashMap<String, SimpleJsonProperty> properties;
    public SimpleJsonObject(LinkedHashMap<String, SimpleJsonProperty> properties) {
        this.properties = properties;
        this.properties.values().forEach(p -> p.object = this);
    }

    public LinkedHashMap<String, SimpleJsonProperty> getProperties(){
       return properties;

    }

    public SimpleJsonProperty get(String propertyName) {
        return properties.get(propertyName);
    }
}
