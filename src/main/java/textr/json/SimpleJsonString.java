package textr.json;

// this code is largely based on the code included in the assignment

/**
 */
public final class SimpleJsonString extends SimpleJsonValue {
    public TextLocation start;
    public int length;
    public String value;
    public SimpleJsonString(TextLocation start, int length, String value) {
        this.start = start;
        this.length = length;
        this.value = value;
    }

    public String getValue(){
        return value;
    }

}
