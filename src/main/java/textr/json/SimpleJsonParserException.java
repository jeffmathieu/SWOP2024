package textr.json;

// this code is largely based on the code included in the assignment

public class SimpleJsonParserException extends RuntimeException {
    TextLocation location;
    String innerMessage;
    public SimpleJsonParserException(TextLocation location, String message) {
        super(location + ": " + message);
        this.location = location;
        this.innerMessage = message;
    }

    public TextLocation getLocation() {
        return location;
    }
}
