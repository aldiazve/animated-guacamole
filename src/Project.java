
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
public class Project {


static class Lexer {

    enum State {
        INITIAL, LINE_BREAK, IDENTIFIER, IDENTIFIER_END, INTEGER, DOUBLE,
        STRING, STRING_END, COMMENT, IGNORE, COMMENT_END, INTEGER_END,
        DOUBLE_BEGIN, DOUBLE_END, LEXICAL_ERROR, SINGLE_TOKEN_END,
        GREATER, GREATER_EQUAL, GREATER_END,
        LESS, LESS_EQUAL, LESS_END,
        NOT, NOT_EQUAL, NOT_END,
        STAR, DOUBLE_STAR, STAR_END,
        AND, AND_END, OR, OR_END, EQUALS, EQUALS_END
    };

    static final Set<State> accept = new HashSet<>();
    static final Set<String> keywords = new HashSet<String>();
    static final Set<State> ignorelast = new HashSet<State>();

    static {
        ignorelast.add(State.IDENTIFIER_END);
        ignorelast.add(State.COMMENT_END);
        ignorelast.add(State.INTEGER_END);
        ignorelast.add(State.DOUBLE_END);
        ignorelast.add(State.GREATER_END);
        ignorelast.add(State.LESS_END);
        ignorelast.add(State.NOT_END);
        ignorelast.add(State.STAR_END);
        keywords.add("for");
        keywords.add("size");
        keywords.add("foreach");
        keywords.add("stdin");
        keywords.add("gets");
        keywords.add("while");
        keywords.add("expr");
        keywords.add("if");
        keywords.add("then");
        keywords.add("list");
        keywords.add("array");
        keywords.add("elseif");
        keywords.add("else");
        keywords.add("switch");
        keywords.add("default");
        keywords.add("break");
        keywords.add("continue");
        keywords.add("return");
        keywords.add("set");
        keywords.add("proc");
        keywords.add("puts");
        keywords.add("read");
        keywords.add("incr");
        keywords.add("global");
        keywords.add("exists");
        accept.add(State.STRING_END);
        accept.add(State.COMMENT_END);
        accept.add(State.LINE_BREAK);
        accept.add(State.IDENTIFIER_END);
        accept.add(State.IGNORE);
        accept.add(State.INTEGER_END);
        accept.add(State.DOUBLE_END);
        accept.add(State.SINGLE_TOKEN_END);
        accept.add(State.GREATER_EQUAL);
        accept.add(State.GREATER_END);
        accept.add(State.LESS_EQUAL);
        accept.add(State.LESS_END);
        accept.add(State.NOT_EQUAL);
        accept.add(State.NOT_END);
        accept.add(State.DOUBLE_STAR);
        accept.add(State.STAR_END);
        accept.add(State.AND_END);
        accept.add(State.OR_END);
        accept.add(State.EQUALS_END);
    }

    private static State nextState(char c, State s) {

        switch (s) {
            case INITIAL:
                switch (c) {
                    case '"':
                        return State.STRING;
                    case '>':
                        return State.GREATER;
                    case '<':
                        return State.LESS;
                    case '!':
                        return State.NOT;
                    case '*':
                        return State.STAR;
                    case '\n':
                        return State.LINE_BREAK;
                    case '#':
                        return State.COMMENT;
                    case '&':
                        return State.AND;
                    case '=':
                        return State.EQUALS;
                    case '|':
                        return State.OR;
                }

                if (Token.operators.get(Character.toString(c)) != null) {
                    return State.SINGLE_TOKEN_END;
                }
                if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_') {
                    return State.IDENTIFIER;
                }
                if (c == ' ' || c == '\t' || c == '\r') {
                    return State.IGNORE;
                }
                if (c >= '0' && c <= '9') {
                    return State.INTEGER;
                }
                return State.LEXICAL_ERROR;
            case GREATER:
                return (c == '=') ? State.GREATER_EQUAL : State.GREATER_END;
            case LESS:
                return (c == '=') ? State.LESS_EQUAL : State.LESS_END;
            case NOT:
                return (c == '=') ? State.NOT_EQUAL : State.NOT_END;
            case STAR:
                return (c == '*') ? State.DOUBLE_STAR : State.STAR_END;
            case AND:
                return (c == '&') ? State.AND_END : State.LEXICAL_ERROR;
            case EQUALS:
                return (c == '=') ? State.EQUALS_END : State.LEXICAL_ERROR;
            case OR:
                return (c == '|') ? State.OR_END : State.LEXICAL_ERROR;
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
                    return State.LEXICAL_ERROR;
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
                if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_') {
                    return State.IDENTIFIER;
                }
                return State.IDENTIFIER_END;
            default:
                System.err.println("Unknown State...");
                System.exit(1);
                return State.INITIAL;
        }

    }

    public List<Token> Tokenize(PushbackInputStream in) throws IOException {
        List<Token> tokens = new ArrayList<>();

        State current = State.INITIAL;
        String lexeme = "";
        int line = 1, col = 0;
        int startLine = 1, startCol = 1;
        Boolean done = false;
        while (!done) {
            int i = in.read();
            col++;
            if (current == State.INITIAL) {
                startLine = line;
                startCol = col;
            }

            if (i == -1) {
                done = true;
                i = (int) '\n';
            }

            char caracter = (char) i;

            lexeme += caracter;
            State new_state = nextState(caracter, current);
            //System.out.println(new_state);

            if (new_state == State.LEXICAL_ERROR) {
                for (Token t : tokens) {
                    System.out.println(t);
                }
                System.out.println(">>> Error lexico (linea: " + line + ", posicion: " + (col==1 || current == State.INITIAL?col:--col) + ")");
                return tokens;
            }

            if (accept.contains(new_state)) {

                if (ignorelast.contains(new_state)) {
                    in.unread((int) caracter);
                    col--;
                    lexeme = lexeme.substring(0, lexeme.length() - 1);
                }

                switch (new_state) {
                    case IDENTIFIER_END:
                        Token.Type t = null;
                        if (keywords.contains(lexeme)) {
                            t = Token.Type.KEYWORD;
                        } else if (Token.operators.get(lexeme) != null) {
                            t = Token.Type.OPERATOR;
                        } else {
                            t = Token.Type.IDENTIFIER;
                        }
                        tokens.add(new Token(t, lexeme, startLine, startCol));
                        break;

                    case INTEGER_END:
                        tokens.add(new Token(Token.Type.INTEGER, lexeme, startLine, startCol));
                        break;
                    case DOUBLE_END:
                        tokens.add(new Token(Token.Type.DOUBLE, lexeme, startLine, startCol));
                        break;
                    case STRING_END:
                        tokens.add(new Token(Token.Type.STRING, lexeme.substring(1, lexeme.length() - 1), startLine, startCol));
                        break;
                    case SINGLE_TOKEN_END:
                    case GREATER_END:
                    case GREATER_EQUAL:
                    case LESS_END:
                    case LESS_EQUAL:
                    case NOT_END:
                    case NOT_EQUAL:
                    case DOUBLE_STAR:
                    case STAR_END:
                    case AND_END:
                    case EQUALS_END:
                    case OR_END:
                        tokens.add(new Token(Token.Type.OPERATOR, lexeme, startLine, startCol));
                        break;
                    case LINE_BREAK:
                        line++;
                        col = 0;
                        break;
                    case IGNORE:
                        break;
                }
                lexeme = "";
                new_state = State.INITIAL;
            }
            current = new_state;
        }
        for (Token t : tokens) {
            System.out.println(t);
        }
        return tokens;
    }
}

static class Token {

    public enum Type {
        IDENTIFIER, KEYWORD, STRING, INTEGER, DOUBLE, OPERATOR
    };
    Type type;
    int line;
    int column;
    String lexeme;

    public static final Map<String, String> operators = new HashMap<>();

    static {

        operators.put("{", "token_llave_izq");
        operators.put("}", "token_llave_der");
        operators.put("$", "token_dollar");
        operators.put(";", "token_pyc");
        operators.put("[", "token_cor_izq");
        operators.put("]", "token_cor_der");
        operators.put("(", "token_par_izq");
        operators.put(")", "token_par_der");
        operators.put("+", "token_mas");
        operators.put("-", "token_menos");
        operators.put("/", "token_div");
        operators.put("%", "token_mod");
        operators.put("<", "token_menor");
        operators.put("<=", "token_menor_igual");
        operators.put(">=", "token_mayor_igual");
        operators.put(">", "token_mayor");
        operators.put("!", "token_not");
        operators.put("!=", "token_diff_num");
        operators.put("*", "token_mul");
        operators.put("**", "token_pot");
        operators.put("&&", "token_and");
        operators.put("||", "token_or");
        operators.put("==", "token_igual_num");
        operators.put("ne", "token_diff_str");
        operators.put("eq", "token_igual_str");

    }

    public Token(Type type, String lexeme, int line, int column) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        String t = "";
        String l = lexeme;
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
            case OPERATOR:
                t = operators.get(lexeme);
                l = "";
                break;
            default:
                break;
        }
        if (!t.equals("")) {
            t += ",";
        }
        if (!l.equals("")) {
            l += ",";
        }
        return "<" + t + l + line + "," + column + ">";
    }
}

    public static void main(String[] args) throws IOException {
        Lexer l = new Lexer();
        l.Tokenize(new PushbackInputStream(System.in));

    }
}
