
public class Token {

    int line;
    int column;
    String lexeme;

    public enum Type {
        IDENTIFIER, KEYWORD, STRING, INTEGER, DOUBLE
    };
    Type type;

    public Token(Type type, String lexeme, int line, int column) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        String t = "";
        switch (type) {
            case STRING:
                t = "token_string";
                break;
            case INTEGER:
                t = "token_integer";
                break;
            case DOUBLE:
                t = "token_double";
                break;
            case IDENTIFIER:
                t = "id";
                break;
            default:
                break;
        }
        if (!t.equals("")) {
            t += ",";
        }
        return "<" + t + lexeme + "," + line + "," + column + ">";
    }
}
