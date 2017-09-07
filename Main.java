
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class Main {

    enum State {
        INITIAL, LINE_BREAK, IDENTIFIER, IDENTIFIER_END, INTEGER, DOUBLE, STRING, STRING_END, COMMENT, IGNORE, COMMENT_END, INTEGER_END, DOUBLE_BEGIN, DOUBLE_END
    };

    public static Set<State> accept = new HashSet<>();

    public static State next(char c, State s) {
        switch (s) {
            case INITIAL:
                if (c == '"') {
                    return State.STRING;
                }
                if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                    return State.IDENTIFIER;
                }
                if (c == ' ' || c == '\t' || c == '\r') {
                    return State.IGNORE;
                }
                if (c >= '0' && c <= '9') {
                    return State.INTEGER;
                }
                if (c == '\n') {
                    return State.LINE_BREAK;
                }
                if (c == '#') {
                    return State.COMMENT;
                }
                System.err.println("Caracter desconocido ('" + c + "')...");
                System.exit(1);
                return State.INITIAL;
            case INTEGER:
                if (c >= '0' && c <= '9') {
                    return State.INTEGER;
                }
                if (c == '.') {
                    return State.DOUBLE_BEGIN;
                }
                return State.INTEGER_END;
            case DOUBLE_BEGIN:
            case DOUBLE:
                if (c >= '0' && c <= '9') {
                    return State.DOUBLE;
                }
                if (s == State.DOUBLE) {
                    return State.DOUBLE_END;
                } else {
                    System.out.println("error double");
                    System.exit(1);
                    return State.INITIAL;
                }
            case COMMENT:
                if (c == '\n') {
                    return State.COMMENT_END;
                }
                return State.COMMENT;
            case STRING:
                if (c == '"') {
                    return State.STRING_END;
                }
                return State.STRING;
            case IDENTIFIER:
                if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9') {
                    return State.IDENTIFIER;
                }
                return State.IDENTIFIER_END;
            default:
                System.err.println("Unknown State...");
                System.exit(1);
                return State.INITIAL;
        }

    }

    public static void main(String[] args) throws IOException {
        List<Token> tokens = new ArrayList<>();
        Set<String> keywords = new HashSet<String>();
        Set<State> ignorelast = new HashSet<State>();
        ignorelast.add(State.IDENTIFIER_END);
        ignorelast.add(State.COMMENT_END);
        ignorelast.add(State.INTEGER_END);
        ignorelast.add(State.DOUBLE_END);
        keywords.add("for");
	keywords.add("foreach");
	keywords.add("if");
	keywords.add("then");
	keywords.add("list");
	keywords.add("array");
	keywords.add("elseif");
	keywords.add("else");
	keywords.add("break");
	keywords.add("continue");
	keywords.add("return");
	keywords.add("set");
	keywords.add("proc");
	keywords.add("puts");
	keywords.add("read");
	keywords.add("incr");
	keywords.add("global");
        accept.add(State.STRING_END);
        accept.add(State.COMMENT_END);
        accept.add(State.LINE_BREAK);
        accept.add(State.IDENTIFIER_END);
        accept.add(State.IGNORE);
        accept.add(State.INTEGER_END);
        accept.add(State.DOUBLE_END);
        State current = State.INITIAL;
        String lexeme = "";
        int line = 1, col = 0;
        int startLine = line, startCol = col;
        PushbackInputStream in = new PushbackInputStream(System.in);
        Boolean completo = false;
        while (!completo) {
            int i = in.read();
            col++;
            if (i == -1) {
                completo = true;
                i = (int) '\n';
            }

            char caracter = (char) i;

            lexeme += caracter;
            State new_state = next(caracter, current);

            if (accept.contains(new_state)) {

                if (ignorelast.contains(new_state)) {
                    in.unread((int) caracter);
                    lexeme = lexeme.substring(0, lexeme.length() - 1);
                }

                switch (new_state) {
                    case IDENTIFIER_END:
                        tokens.add(new Token(keywords.contains(lexeme) ? Token.Type.KEYWORD : Token.Type.IDENTIFIER, lexeme, startLine, startCol));
                        break;
                    case INTEGER_END:
                        tokens.add(new Token(Token.Type.INTEGER, lexeme, startLine, startCol));
                        break;
                    case DOUBLE_END:
                        tokens.add(new Token(Token.Type.DOUBLE, lexeme, startLine, startCol));
                        break;
                    case STRING_END:
                        tokens.add(new Token(Token.Type.STRING, lexeme.substring(1, lexeme.length() - 1), startLine, startCol));
                        break;//TODO
                    case LINE_BREAK:
                        line++;
                        col = 0;
                        break;
                    case IGNORE:
                        break;
                }
                lexeme = "";
                startLine = line;
                startCol = col;
                new_state = State.INITIAL;
            }

            current = new_state;

        }
        for (Token t : tokens) {
            System.out.println(t);
        }

    }

}
