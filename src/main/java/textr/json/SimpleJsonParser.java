package textr.json;

import java.util.LinkedHashMap;

// this code is largely based on the code included in the assignment

public class SimpleJsonParser {

    String text;
    /**
     * Zero-based char offset
     */
    int offset;
    /**
     * Zero-based line number
     */
    int line;
    /**
     * Zero-based column number
     */
    int column;
    int objectNestingDepth;

    SimpleJsonParser(String text) {
        this.text = text;
    }

    TextLocation location() { return new TextLocation(line, column); }

    int peek() {
        if (offset == text.length())
            return -1;
        return text.charAt(offset);
    }

    void eat() {
        int c = text.charAt(offset);
        if (c == '\r') {
            // We assume the text has either CRLF or LF line separators.
            offset += 2;
            line++;
            column = 0;
        } else if (c == '\n') {
            offset++;
            line++;
            column = 0;
        } else {
            offset++;
            column++;
        }
    }

    String charToString(int c) {
        return switch (c) {
            case -1 -> "end of text";
            case '\r' -> "carriage return";
            case '\n' -> "newline";
            default -> "'" + (char)c + "'";
        };
    }

    void expect(int c) {
        if (peek() != c)
            throw new SimpleJsonParserException(location(), "Expected " + charToString(c) + " but found " + charToString(peek()));
        eat();
    }

    void expectLineSeparator() {
        if (peek() == '\r') {
            eat();
        } else
            expect('\n');
    }

    void expectIndentation() {
        int n = 2 * objectNestingDepth;
        for (int i = 0; i < n; i++)
            expect(' ');
    }

    void expectLineBreak() {
        expectLineSeparator();
        expectIndentation();
    }

    /**
     * Returns the simple JSON value starting at the current location
     * and advances the current location.
     */
    SimpleJsonValue parseSimpleJsonValue() {
        return switch (peek()) {
            case '"' -> parseSimpleJsonString();
            case '{' -> parseSimpleJsonObject();
            default -> throw new SimpleJsonParserException(location(), "Value expected");
        };
    }

    SimpleJsonString parseSimpleJsonString() {
        TextLocation start = location();
        expect('"');
        StringBuilder builder = new StringBuilder();
        for (;;) {
            int c = peek();
            switch (c) {
                case '"' -> {
                    eat();
                    return new SimpleJsonString(start, column - start.column(), builder.toString());
                }
                case '\\' -> {
                    eat();
                    switch (peek()) {
                        case '\\' -> {
                            eat();
                            builder.append('\\');
                        }
                        case '"' -> {
                            eat();
                            builder.append('"');
                        }
                        case 'r' -> {
                            eat();
                            builder.append('\r');
                        }
                        case 'n' -> {
                            eat();
                            builder.append('\n');
                        }
                        default ->
                                throw new SimpleJsonParserException(location(), "Invalid escape sequence");
                    }
                }
                default -> {
                    if (32 <= c && c <= 126) {
                        eat();
                        builder.append((char)c);
                    } else
                        throw new SimpleJsonParserException(location(), "Invalid character in string value");
                }
            }
        }
    }

    SimpleJsonObject parseSimpleJsonObject() {
        expect('{');
        objectNestingDepth++;
        expectLineBreak();
        LinkedHashMap<String, SimpleJsonProperty> properties = new LinkedHashMap<>();
        for (;;) {
            String propertyName = parseSimpleJsonString().value;
            if (properties.containsKey(propertyName))
                throw new SimpleJsonParserException(location(), "Duplicate property");
            expect(':');
            expect(' ');
            SimpleJsonValue propertyValue = parseSimpleJsonValue();
            SimpleJsonProperty property = new SimpleJsonProperty(propertyName, propertyValue);
            properties.put(propertyName, property);
            if (peek() != ',')
                break;
            expect(',');
            expectLineBreak();
        }
        objectNestingDepth--;
        expectLineBreak();
        expect('}');
        return new SimpleJsonObject(properties);
    }

    public static SimpleJsonObject parseSimpleJsonObject(String text) {
        SimpleJsonParser parser = new SimpleJsonParser(text);
        SimpleJsonObject result = parser.parseSimpleJsonObject();
        if (parser.peek() != -1)
            throw new SimpleJsonParserException(parser.location(), "End of text expected");
        return result;
    }

}