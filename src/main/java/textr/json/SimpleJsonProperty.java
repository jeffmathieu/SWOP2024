package textr.json;

// this code is largely based on the code included in the assignment

public class SimpleJsonProperty {
    public textr.json.SimpleJsonObject object;
    public String name;
    public SimpleJsonValue value;
    public SimpleJsonProperty(String name, SimpleJsonValue value) {
        this.name = name;
        this.value = value;
        value.property = this;
    }
}